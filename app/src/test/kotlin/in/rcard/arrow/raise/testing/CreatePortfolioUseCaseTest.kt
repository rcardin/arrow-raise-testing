package `in`.rcard.arrow.raise.testing

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class CreatePortfolioUseCaseTest {
    private val underTest = createPortfolioUseCase()

    @Test
    internal fun `given a userId and an initial amount, when executed, then it create the portfolio`() =
        runTest {
            val actualResult: PortfolioId =
                underTest.createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0)))
        }
}
