package it.vashykator.sheets

import java.util.regex.Pattern

private const val RANGE_FORMAT: String = """.*![A-Z]+\d*:[A-Z]+\d*"""

data class BookkeeperSection(
    val earning: SheetRange,
    val expenditure: SheetRange,
    val waste: SheetRange
) {
    init {
        earning.validate()
        expenditure.validate()
        waste.validate()
    }
}

inline class SheetRange(val range: String)

fun SheetRange.validate() = require(Pattern.matches(RANGE_FORMAT, range)) { "'$range' is not a valid range" }
