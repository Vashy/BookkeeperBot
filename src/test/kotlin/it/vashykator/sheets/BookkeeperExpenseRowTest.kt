package it.vashykator.sheets

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class BookkeeperExpenseRowTest {

    @Test
    fun `create BookkeeperExpenseRow from list (fromList method)`() {
        val listToRow = listOf("2019-12-12", "12.3", "Description with spaces", "and", "multiple parameters")
        val row = fromList(listToRow)

        with(assertThat(row)) {
            isNotNull()
            isEqualTo(
                BookkeeperExpenseRow(
                    LocalDate.parse("2019-12-12"),
                    12.3,
                    "Description with spaces and multiple parameters",
                    null
                )
            )
            isNotEqualTo(
                BookkeeperExpenseRow(
                    LocalDate.parse("2019-12-12"),
                    12.3,
                    "Description with spaces nope",
                    null
                )
            )
        }
    }

    @Test
    fun `call failing fromList method and expect null on Double parsing`() {
        val listToRow = listOf("2019-12-12", "aaa", "Description with spaces")
        val row = fromList(listToRow)

        assertThat(row).isNull()
    }

    @Test
    fun `call failing fromList method and expect null on Date parsing`() {
        val listToRow = listOf("2019-12&&12", "15.1", "Description with spaces")
        val row = fromList(listToRow)

        assertThat(row).isNull()
    }
}
