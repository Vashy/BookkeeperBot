package it.vashykator.sheets

import java.time.LocalDate

fun expenseRow(body: BookkeeperExpenseRow.Builder.() -> Unit): BookkeeperExpenseRow =
    BookkeeperExpenseRow.Builder().build(body)

data class BookkeeperExpenseRow(
    val date: LocalDate = LocalDate.now(),
    val price: Double = 0.0,
    val description: String = "",
    val category: BookkeeperCategory? = null
) {

    class Builder {
        var date: LocalDate = LocalDate.now()
        var price: Double = 0.0
        lateinit var description: String
        var category: BookkeeperCategory? = null

        fun build() = BookkeeperExpenseRow(date, price, description, category)

        fun build(builder: Builder.() -> Unit): BookkeeperExpenseRow {
            builder()
            return build()
        }
    }
}
