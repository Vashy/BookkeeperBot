package it.vashykator.sheets

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class DSLTest {

    @Test
    internal fun name() {
        val spreadsheet = spreadsheet("id") {

            worksheet("sheetName 1") {
                earn { "B:E" }
                expenditure { "B:E16" }
                waste { "A13:D16" }
            }

            worksheet("sheetName 2") {
                earn { "B:E" }
                expenditure { "B:E16" }
                waste { "A13:D16" }
            }

            worksheet("sheetName 3") {
                earn { "B:E" }
                expenditure { "B:E16" }
                waste { "A13:D16" }
            }
        }

        with(spreadsheet) {
            assertThat(id).isEqualTo("id")
            assertThat(worksheets).hasSize(3)

            with(worksheets[0]) {
                assertThat(name).isEqualTo("sheetName 1")

                with(bookkeeperSection) {
                    assertThat(earning).isEqualTo(SheetRange("sheetName 1!B:E"))
                }
            }

            with(worksheets[1]) {
                assertThat(name).isEqualTo("sheetName 2")

                with(bookkeeperSection) {
                    assertThat(earning).isEqualTo(SheetRange("sheetName 2!B:E"))
                }
            }

            with(worksheets[2]) {
                assertThat(name).isEqualTo("sheetName 3")

                with(bookkeeperSection) {
                    assertThat(earning).isEqualTo(SheetRange("sheetName 3!B:E"))
                }
            }
        }

    }
}

class MySpreadsheet(val id: String) {

    private val workerWorksheets: MutableList<MyWorksheet> = mutableListOf()

    val worksheets: List<MyWorksheet>
        get() = workerWorksheets.toList()

    class MyWorksheet(val name: String) {
        private val builder by lazy { BookkeeperSectionBuilder(name) }
        val bookkeeperSection by lazy { builder.build() }

        fun earn(body: () -> String) {
            builder.earning = body()
        }

        fun expenditure(body: () -> String) {
            builder.expenditure = body()
        }

        fun waste(body: () -> String) {
            builder.waste = body()
        }
    }

    fun worksheet(
        name: String,
        body: MyWorksheet.() -> Unit
    ): MyWorksheet {
        val worksheet = MyWorksheet(name).apply(body)
        workerWorksheets.add(worksheet)

        return worksheet
    }

}

inline fun spreadsheet(id: String, body: MySpreadsheet.() -> Unit): MySpreadsheet = MySpreadsheet(id).apply(body)

private class BookkeeperSectionBuilder(private val sheetName: String) {
    lateinit var earning: String
    lateinit var expenditure: String
    lateinit var waste: String

    fun build() = BookkeeperSection(
        sheetRange(earning),
        sheetRange(expenditure),
        sheetRange(waste)
    )

    private fun sheetRange(range: String) = SheetRange("$sheetName!$range")
}
