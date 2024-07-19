package `in`.rcard.arrow.raise.testing

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.fold
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlin.test.DefaultAsserter.fail

internal class CreatePortfolioUseCaseKotestTest :
    ShouldSpec({

        val underTest = createPortfolioUseCase()

        context("The create portfolio use case") {
            should("create a portfolio for a user") {
                val actualResult: Either<DomainError, PortfolioId> =
                    either {
                        underTest.createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0)))
                    }

                actualResult.shouldBeRight(PortfolioId("1"))
            }

            should("create a portfolio for a user (using fold)") {
                fold(
                    block = { underTest.createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0))) },
                    recover = { fail("The use case should not fail") },
                    transform = { it.shouldBe(PortfolioId("1")) },
                )
            }
        }
    })
