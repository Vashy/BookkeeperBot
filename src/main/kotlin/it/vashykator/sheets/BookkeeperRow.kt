package it.vashykator.sheets

import it.vashykator.sheets.BookkeeperCategory.NONE
import mu.KotlinLogging
import java.time.LocalDate
import java.time.LocalDate.parse
import java.util.regex.Pattern.matches

private val log = KotlinLogging.logger { }

data class BookkeeperRow(
    val date: LocalDate = LocalDate.now(),
    val price: Double,
    val description: String,
    val category: BookkeeperCategory = NONE
)

fun BookkeeperRow.pretty() = toString().substringAfterLast("BookkeeperRow").replace("(", "[").replace(")", "]")

internal fun <T : Any> List<T>.takeFrom(startIndex: Int): List<T> =
    subList(startIndex, size)


internal interface BookkeeperRowFactory {
    fun from(list: List<String>, categorySeparator: String = "|"): BookkeeperRow?
}

internal object BookkeeperRowFactoryInstance : BookkeeperRowFactory {

    override fun from(list: List<String>, categorySeparator: String): BookkeeperRow? {
        val usDateFormat = """\d\d\d\d[/-]\d\d[/-]\d\d"""
        return try {

            when {
                matches(usDateFormat, list[0]) -> {
                    val (description, category) = splitDescriptionAndCategory(list, 2, categorySeparator)
                    BookkeeperRow(
                        parse(list[0].replace("/", "-")),
                        list[1].toDouble(),
                        description,
                        category
                    )
                }
                else -> {
                    val (description, category) = splitDescriptionAndCategory(list, 1, categorySeparator)
                    BookkeeperRow(
                        price = list[0].toDouble(),
                        description = description,
                        category = category
                    )
                }
            }
        } catch (e: Exception) {
            log.debug { e.localizedMessage }
            null
        }
    }
}

private fun splitDescriptionAndCategory(
    args: List<String>,
    fromIndex: Int,
    categorySeparator: String = "|"
): Pair<String, BookkeeperCategory> {

    val descriptionAndCategory = args.takeFrom(fromIndex).joinToString(" ")
    if (!descriptionAndCategory.contains(categorySeparator))
        return Pair(descriptionAndCategory, NONE)

    val split = descriptionAndCategory.split(categorySeparator)
    return Pair(split[0], safeCategoryOf(split[1]))
}

private fun safeCategoryOf(value: String) =
    try {
        BookkeeperCategory.valueOf(value.trim().toUpperCase())
    } catch (e: IllegalArgumentException) {
        NONE
    }
