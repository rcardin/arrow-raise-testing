package `in`.rcard.arrow.raise.testing

import arrow.core.Either
import arrow.core.raise.Raise

sealed interface DomainError

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
    context (Raise<DomainError>)
    suspend fun createPortfolio(model: CreatePortfolio): Either<DomainError, PortfolioId>
}

fun main() {
}
