package it.vashykator.sheets

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import it.vashykator.sheets.BookkeeperCategory.FOOD
import it.vashykator.sheets.BookkeeperRowFactoryInstance.from
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class BookkeeperRowTest {

    @Test
    fun `create BookkeeperExpenseRow from list (fromList method)`() {
        val listToRow = listOf("2019-12-12", "12.3", "Description with spaces", "and", "multiple parameters")
        val row = from(listToRow)

        with(assertThat(row)) {
            isNotNull()
            isEqualTo(
                BookkeeperRow(
                    LocalDate.parse("2019-12-12"),
                    12.3,
                    "Description with spaces and multiple parameters"
                )
            )
            isNotEqualTo(
                BookkeeperRow(
                    LocalDate.parse("2019-12-12"),
                    12.3,
                    "Description with spaces nope"
                )
            )
        }
    }

    @Test
    fun `call failing fromList method and expect null on Double parsing`() {
        val listToRow = listOf("2019-12-12", "aaa", "Description with spaces")
        val row = from(listToRow)

        assertThat(row).isNull()
    }

    @Test
    fun `call failing fromList method and expect null on Date parsing`() {
        val listToRow = listOf("2019-12&&12", "15.1", "Description with spaces")
        val row = from(listToRow)

        assertThat(row).isNull()
    }

    @Test
    internal fun `BookkeeperRow pretty()`() {
        val row = BookkeeperRow(
            date = LocalDate.parse("2000-10-01"),
            price = 1.5,
            description = "description",
            category = FOOD
        ).pretty()

        assertThat(row).isEqualTo("[date=2000-10-01, price=1.5, description=description, category=FOOD]")
    }
}

internal class FactoryTest {
    @Test
    internal fun `create BookkeeperRow with category, separated by pipe`() {

        val listToRow = listOf(
            "2019-12-12",
            "12.3",
            "Description with spaces",
            "and",
            "multiple parameters with category",
            "|",
            " FOOD"
        )
        val row = from(listToRow)

        assertThat(row?.category).isEqualTo(FOOD)
    }

    @Test
    internal fun `create BookkeeperRow with category, separated by comma`() {

        val listToRow = listOf(
            "2019-12-12",
            "12.3",
            "Description with spaces",
            "and",
            "multiple parameters with category",
            ",",
            " FOOD"
        )
        val row = from(listToRow, ",")

        assertThat(row?.category).isEqualTo(FOOD)
    }
}
