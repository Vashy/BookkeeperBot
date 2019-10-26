package it.vashykator.sheets

import mu.KotlinLogging
import java.time.LocalDate
import java.time.LocalDate.parse
import java.util.regex.Pattern.matches

private val log = KotlinLogging.logger { }

data class BookkeeperExpenseRow(
    val date: LocalDate = LocalDate.now(),
    val price: Double = 0.0,
    val description: String = "",
    val category: BookkeeperCategory? = null
) {
    class Builder {
        var date: LocalDate = LocalDate.now()
        var price: Double = 0.0
        var description: String = ""
        var category: BookkeeperCategory? = null

        fun build(): BookkeeperExpenseRow {
            return BookkeeperExpenseRow(date, price, description, category)
        }
    }
}

fun BookkeeperExpenseRow.pretty() = toString().substringAfterLast("BookkeeperExpenseRow")

fun expenseRow(body: BookkeeperExpenseRow.Builder.() -> Unit): BookkeeperExpenseRow {
    val builder = BookkeeperExpenseRow.Builder()
    builder.body()
    return builder.build()
}

fun fromListOrNull(args: List<String>): BookkeeperExpenseRow? {
    val usDateFormat = """\d\d\d\d[/-]\d\d[/-]\d\d"""

    return try {
        val matchesUsFormat = matches(usDateFormat, args[0])
        if (matchesUsFormat) {
            BookkeeperExpenseRow(
                date = parse(args[0].replace("/", "-")),
                price = args[1].toDouble(),
                description = args takeFrom 2
            )
        } else { // No date
            BookkeeperExpenseRow(
                price = args[0].toDouble(),
                description = args takeFrom 1
            )
        }
    } catch (e: Exception) {
        log.debug { e.localizedMessage }
        null
    }
}

private infix fun List<String>.takeFrom(startIndex: Int) =
    subList(startIndex, size).joinToString(separator = " ")
