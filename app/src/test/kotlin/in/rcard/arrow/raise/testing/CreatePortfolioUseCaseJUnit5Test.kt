package `in`.rcard.arrow.raise.testing

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.fold
import `in`.rcard.arrow.raise.testing.DomainError.GenericError
import `in`.rcard.assertj.arrowcore.EitherAssert
import `in`.rcard.assertj.arrowcore.RaiseAssert
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

private val fakeCountUserPortfolios: CountUserPortfoliosPort =
    object : CountUserPortfoliosPort {
        override fun Raise<DomainError>.countByUserId(userId: UserId): Int = if (userId == UserId("bob")) 0 else 1
    }

internal class CreatePortfolioUseCaseJUnit5Test {
    private val underTest = createPortfolioUseCase(fakeCountUserPortfolios)

    @Test
    internal fun `given a userId and an initial amount, when executed, then it create the portfolio`() {
        val actualResult: Either<DomainError, PortfolioId> =
            either {
                with(underTest) {
                    createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0)))
                }
            }
        Assertions.assertThat(actualResult.getOrNull()).isEqualTo(PortfolioId("1"))
    }

    @Test
    internal fun `given a userId and an initial amount, when executed, then it create the portfolio (using AssertJ-Arrow-Core)`() {
        val actualResult: Either<DomainError, PortfolioId> =
            either {
                with(underTest) {
                    createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0)))
                }
            }
        EitherAssert.assertThat(actualResult).containsOnRight(PortfolioId("1"))
    }

    @Test
    internal fun `given a userId and an initial amount, when executed, then it create the portfolio (using fold)`() {
        fold(
            block = {
                with(underTest) {
                    createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0)))
                }
            },
            recover = { Assertions.fail("The use case should not fail") },
            transform = { Assertions.assertThat(it).isEqualTo(PortfolioId("1")) },
        )
    }

    @Test
    internal fun `given a userId and an initial amount, when executed, then it create the portfolio (using RaiseAssert)`() {
        RaiseAssert
            .assertThat {
                with(underTest) {
                    createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0)))
                }
            }.succeedsWith(PortfolioId("1"))
    }

    @Test
    fun `given a userId and an initial amount, when executed with error, then propagates the error properly`() {
        val exception = RuntimeException("Ooops!")
        val actualResult =
            either {
                val countUserPortfoliosPort =
                    mock<CountUserPortfoliosPort> {
                        onBlocking { countByUserId(UserId("bob")) } doAnswer { raise(GenericError(exception)) }
                    }
                with(createPortfolioUseCase(countUserPortfoliosPort)) {
                    createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0)))
                }
            }

        EitherAssert.assertThat(actualResult).containsOnLeft(GenericError(exception))
    }
}
