package `in`.rcard.arrow.raise.testing

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.fold
import `in`.rcard.assertj.arrowcore.EitherAssert
import `in`.rcard.assertj.arrowcore.RaiseAssert
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class CreatePortfolioUseCaseJUnit5Test {
    private val underTest = createPortfolioUseCase()

    @Test
    internal fun `given a userId and an initial amount, when executed, then it create the portfolio`() =
        runTest {
            val actualResult: Either<DomainError, PortfolioId> =
                either {
                    underTest.createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0)))
                }
            Assertions.assertThat(actualResult.getOrNull()).isEqualTo(PortfolioId("1"))
        }

    @Test
    internal fun `given a userId and an initial amount, when executed, then it create the portfolio (using AssertJ-Arrow-Core)`() =
        runTest {
            val actualResult: Either<DomainError, PortfolioId> =
                either {
                    underTest.createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0)))
                }
            EitherAssert.assertThat(actualResult).containsOnRight(PortfolioId("1"))
        }

    @Test
    internal fun `given a userId and an initial amount, when executed, then it create the portfolio (using fold)`() =
        runTest {
            fold(
                block = { underTest.createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0))) },
                recover = { Assertions.fail("The use case should not fail") },
                transform = { Assertions.assertThat(it).isEqualTo(PortfolioId("1")) },
            )
        }

    @Test
    internal fun `given a userId and an initial amount, when executed, then it create the portfolio (using RaiseAssert)`() =
        RaiseAssert
            .assertThat {
                runBlocking {
                    underTest.createPortfolio(
                        CreatePortfolio(
                            UserId("bob"),
                            Money(1000.0),
                        ),
                    )
                }
            }.succeedsWith(PortfolioId("1"))
}