package `in`.rcard.arrow.raise.testing

import arrow.core.raise.Raise
import arrow.core.raise.ensure

sealed interface DomainError {
    data class PortfolioAlreadyExists(
        val userId: UserId,
    ) : DomainError

    data class GenericError(
        val throwable: Throwable,
    ) : DomainError
}

@JvmInline
value class UserId(
    val id: String,
) {
    override fun toString(): String = id
}

@JvmInline
value class PortfolioId(
    val id: String,
) {
    override fun toString(): String = id
}

@JvmInline
value class Money(
    val amount: Double,
)

data class CreatePortfolio(
    val userId: UserId,
    val amount: Money,
)

interface CreatePortfolioUseCase {
    fun Raise<DomainError>.createPortfolio(model: CreatePortfolio): PortfolioId
}

interface CountUserPortfoliosPort {
    fun Raise<DomainError>.countByUserId(userId: UserId): Int
}

fun createPortfolioUseCase(countUserPortfolios: CountUserPortfoliosPort): CreatePortfolioUseCase =
    object : CreatePortfolioUseCase {
        override fun Raise<DomainError>.createPortfolio(model: CreatePortfolio): PortfolioId {
            with(countUserPortfolios) {
                ensure(countByUserId(model.userId) == 0) {
                    raise(DomainError.PortfolioAlreadyExists(model.userId))
                }
            }
            return PortfolioId("1")
        }
    }

interface CreatePortfolioUseCaseWoContextReceivers {
    fun Raise<DomainError>.createPortfolio(model: CreatePortfolio): PortfolioId
}

fun createPortfolioUseCaseWoContextReceivers(): CreatePortfolioUseCaseWoContextReceivers =
    object : CreatePortfolioUseCaseWoContextReceivers {
        override fun Raise<DomainError>.createPortfolio(model: CreatePortfolio): PortfolioId = PortfolioId("1")
    }

fun main() {
}
