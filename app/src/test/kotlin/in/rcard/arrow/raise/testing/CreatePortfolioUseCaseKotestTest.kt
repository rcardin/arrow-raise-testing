package `in`.rcard.arrow.raise.testing

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.fold
import `in`.rcard.arrow.raise.testing.DomainError.PortfolioAlreadyExists
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.DefaultAsserter.fail

private val fakeCountUserPortfolios: CountUserPortfoliosPort =
    object : CountUserPortfoliosPort {
        context(Raise<DomainError>)
        override suspend fun countByUserId(userId: UserId): Int = if (userId == UserId("bob")) 0 else 1
    }

internal class CreatePortfolioUseCaseKotestTest :
    ShouldSpec({
        val underTest = createPortfolioUseCase(fakeCountUserPortfolios)

        context("The create portfolio use case") {

            should("raise a PortfolioAlreadyExists") {
                val alice = UserId("alice")
                val actualResult: Either<DomainError, PortfolioId> =
                    either {
                        underTest.createPortfolio(CreatePortfolio(alice, Money(1000.0)))
                    }

                actualResult.shouldBeLeft(PortfolioAlreadyExists(alice))
            }

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

            should("create a portfolio for a user (using mockk") {

                val countUserPortfoliosMock: CountUserPortfoliosPort = mockk()
                val underTestWithMock = createPortfolioUseCase(countUserPortfoliosMock)

                coEvery {
                    with(any<Raise<DomainError>>()) {
                        countUserPortfoliosMock.countByUserId(UserId("bob"))
                    }
                } returns 0

                val actualResult: Either<DomainError, PortfolioId> =
                    either {
                        underTestWithMock.createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0)))
                    }

                actualResult.shouldBeRight(PortfolioId("1"))
            }

            should("return a PortfolioAlreadyExists error for an existing user") {

                val countUserPortfoliosMock: CountUserPortfoliosPort = mockk()
                val underTestWithMock = createPortfolioUseCase(countUserPortfoliosMock)

//                coEvery {
//                    with(any<Raise<DomainError>>()) {
//                        countUserPortfoliosMock.countByUserId(UserId("bob"))
//                    }
//                } answers {
//                    raise(DomainError.PortfolioAlreadyExists(UserId("bob")))
//                }

                val actualResult: Either<DomainError, PortfolioId> =
                    either {
                        underTestWithMock.createPortfolio(CreatePortfolio(UserId("bob"), Money(1000.0)))
                    }

                actualResult.shouldBeLeft(PortfolioAlreadyExists(UserId("bob")))
            }
        }
    })
