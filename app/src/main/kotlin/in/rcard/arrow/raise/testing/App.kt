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
) {
//    operator fun plus(money: Money): Money = Money(this.amount + money.amount)
//
//    operator fun minus(money: Money): Money = Money(this.amount - money.amount)
//
//    operator fun times(quantity: Quantity): Money = Money(this.amount * quantity.amount)
//
//    operator fun compareTo(money: Money): Int =
//        when {
//            this.amount > money.amount -> 1
//            this.amount < money.amount -> -1
//            else -> 0
//        }
}

data class CreatePortfolio(
    val userId: UserId,
    val amount: Money,
)

interface CreatePortfolioUseCase {
    suspend fun Raise<DomainError>.createPortfolio(model: CreatePortfolio): PortfolioId
}

interface CountUserPortfoliosPort {
    suspend fun Raise<DomainError>.countByUserId(userId: UserId): Int
}

fun createPortfolioUseCase(countUserPortfolios: CountUserPortfoliosPort): CreatePortfolioUseCase =
    object : CreatePortfolioUseCase {
        override suspend fun Raise<DomainError>.createPortfolio(model: CreatePortfolio): PortfolioId {
            with(countUserPortfolios) {
                ensure(countByUserId(model.userId) == 0) {
                    raise(DomainError.PortfolioAlreadyExists(model.userId))
                }
            }
            return PortfolioId("1")
        }
    }

interface CreatePortfolioUseCaseWoContextReceivers {
    suspend fun Raise<DomainError>.createPortfolio(model: CreatePortfolio): PortfolioId
}

fun createPortfolioUseCaseWoContextReceivers(): CreatePortfolioUseCaseWoContextReceivers =
    object : CreatePortfolioUseCaseWoContextReceivers {
        override suspend fun Raise<DomainError>.createPortfolio(model: CreatePortfolio): PortfolioId = PortfolioId("1")
    }

fun main() {
}
