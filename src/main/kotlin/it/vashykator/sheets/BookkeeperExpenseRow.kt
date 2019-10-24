package it.vashykator.sheets

import java.time.LocalDate
import java.time.LocalDate.parse
import java.util.regex.Pattern.matches

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

fun expenseRow(body: BookkeeperExpenseRow.Builder.() -> Unit): BookkeeperExpenseRow {
    val builder = BookkeeperExpenseRow.Builder()
    builder.body()
    return builder.build()
}

fun fromList(args: List<String>): BookkeeperExpenseRow? {
    val matchesUsFormat = matches("""\d\d\d\d[/-]\d\d[/-]\d\d""", args[0])

    return try {
        if (matchesUsFormat) {
            BookkeeperExpenseRow(
                date = parse(args[0].replace("/", "-")),
                price = args[1].toDouble(),
                description = args.subList(2, args.size).joinToString(separator = " ")
            )
        } else {
            BookkeeperExpenseRow(
                price = args[0].toDouble(),
                description = args.subList(1, args.size).joinToString(separator = " ")
            )
        }
    } catch (e: Exception) {
        null
    }
}
