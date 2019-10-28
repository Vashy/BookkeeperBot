package it.vashykator.sheets

class Spreadsheet(val id: String, val worksheets: List<Worksheet>)

class Worksheet(val name: String, val bookkeeper: BookkeeperSection)

class SpreadsheetProvider(val id: String) {
    val worksheetProviders: MutableList<WorksheetProvider> = mutableListOf()

    operator fun invoke(): Spreadsheet = Spreadsheet(id, worksheetProviders.toWorksheetList())
}

class WorksheetProvider(val name: String) {
    private val builder by lazy { BookkeeperSectionBuilder(name) }
    val bookkeeperSection by lazy { builder.build() }

    fun earn(range: String) {
        builder.earning = range
    }

    fun expenditure(range: String) {
        builder.expenditure = range
    }

    fun waste(range: String) {
        builder.waste = range
    }
}

inline fun spreadsheet(id: String, body: SpreadsheetProvider.() -> Unit): Spreadsheet =
    SpreadsheetProvider(id).apply(body).invoke()

inline fun SpreadsheetProvider.worksheet(name: String, body: WorksheetProvider.() -> Unit) {
    val worksheet = WorksheetProvider(name).apply(body)
    worksheetProviders.add(worksheet)
}

private class BookkeeperSectionBuilder(private val sheetName: String) {
    var earning: String? = null
    var expenditure: String? = null
    var waste: String? = null

    fun build() = BookkeeperSection(
        sheetRange(earning),
        sheetRange(expenditure),
        sheetRange(waste)
    )

    fun sheetRange(range: String?) =
        range?.let { SheetRange("$sheetName!$range") }
            ?: throw IllegalArgumentException("BookkeeperSection not properly initialized")
}

private fun List<WorksheetProvider>.toWorksheetList() =
    map { Worksheet(it.name, it.bookkeeperSection) }
