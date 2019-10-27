package it.vashykator.sheets

import mu.KotlinLogging
import java.time.LocalDate
import java.time.LocalDate.parse
import java.util.regex.Pattern.matches

private val log = KotlinLogging.logger { }

data class BookkeeperRow(
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

        fun build(): BookkeeperRow {
            return BookkeeperRow(date, price, description, category)
        }
    }
}

fun BookkeeperRow.pretty() = toString().substringAfterLast("BookkeeperExpenseRow")

fun expenseRow(body: BookkeeperRow.Builder.() -> Unit): BookkeeperRow {
    val builder = BookkeeperRow.Builder()
    builder.body()
    return builder.build()
}

fun fromListOrNull(args: List<String>): BookkeeperRow? {
    val usDateFormat = """\d\d\d\d[/-]\d\d[/-]\d\d"""

    return try {
        val matchesUsFormat = matches(usDateFormat, args[0])
        if (matchesUsFormat) {
            BookkeeperRow(
                date = parse(args[0].replace("/", "-")),
                price = args[1].toDouble(),
                description = args.takeFrom(2).joinToString(" ")
            )
        } else { // No date
            BookkeeperRow(
                price = args[0].toDouble(),
                description = args.takeFrom(1).joinToString(" ")
            )
        }
    } catch (e: Exception) {
        log.debug { e.localizedMessage }
        null
    }
}

internal fun <T : Any> List<T>.takeFrom(startIndex: Int): List<T> =
    subList(startIndex, size)
