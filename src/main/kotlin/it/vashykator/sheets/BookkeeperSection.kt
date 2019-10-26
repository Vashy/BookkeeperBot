package it.vashykator.sheets

import java.util.regex.Pattern

private const val RANGE_FORMAT: String = """.*![A-Z]+\d*:[A-Z]+\d*"""

inline class Range(val range: String)

data class BookkeeperSection(
    val earning: Range,
    val expenditure: Range,
    val waste: Range
) {
    init {
        earning.validate()
        expenditure.validate()
        waste.validate()
    }
}

private fun Range.validate() = require(Pattern.matches(RANGE_FORMAT, range))
