package it.vashykator.sheets

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test

internal class SpreadsheetDSLTest {

    @Test
    internal fun `worksheet DSL is properly initialized`() {
        val spreadsheet = spreadsheet("id") {

            worksheet("sheetName 1") {
                earn("B:E")
                expenditure("B:E16")
                waste("A13:D16")
            }

            worksheet("sheetName 2") {
                earn("B:E12")
                expenditure("B:E16")
                waste("A13:D16")
            }

            worksheet("sheetName 3") {
                earn("B12:Z")
                expenditure("B:E16")
                waste("A13:D16")
            }
        }

        with(spreadsheet) {
            assertThat(id).isEqualTo("id")
            assertThat(worksheets).hasSize(3)

            with(worksheets[0]) {
                assertThat(name).isEqualTo("sheetName 1")

                with(bookkeeper) {
                    assertThat(earning).isEqualTo(SheetRange("sheetName 1!B:E"))
                }
            }

            with(worksheets[1]) {
                assertThat(name).isEqualTo("sheetName 2")

                with(bookkeeper) {
                    assertThat(earning).isEqualTo(SheetRange("sheetName 2!B:E12"))
                }
            }

            with(worksheets[2]) {
                assertThat(name).isEqualTo("sheetName 3")

                with(bookkeeper) {
                    assertThat(earning).isEqualTo(SheetRange("sheetName 3!B12:Z"))
                }
            }
        }
    }

    @Test
    internal fun `invalid DSL initialization`() {
        assertThat {
            val spreadsheet = spreadsheet("id") {
                worksheet("Name") {
                    earn("abc")
                }
            }
            spreadsheet.worksheets[0].bookkeeper.expenditure
        }.isFailure().all {
            hasClass(IllegalArgumentException::class)
            hasMessage("BookkeeperSection not properly initialized")
        }
    }
}
