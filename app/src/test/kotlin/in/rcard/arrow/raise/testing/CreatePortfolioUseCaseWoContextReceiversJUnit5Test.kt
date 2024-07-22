package `in`.rcard.arrow.raise.testing

import arrow.core.Either
import arrow.core.raise.either
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class CreatePortfolioUseCaseWoContextReceiversJUnit5Test {
    private val underTest = createPortfolioUseCaseWoContextReceivers()

    @Test
    internal fun `given a userId and an initial amount, when executed, then it create the portfolio`() =
        runTest {
            val actualResult: Either<DomainError, PortfolioId> =
                with(underTest) {
                    either {
                        createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0)))
                    }
                }
            Assertions.assertThat(actualResult.getOrNull()).isEqualTo(PortfolioId("1"))
        }
}
