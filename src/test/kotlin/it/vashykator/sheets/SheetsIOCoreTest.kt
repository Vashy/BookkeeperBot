package it.vashykator.sheets

import assertk.assertThat
import assertk.assertions.isEqualTo
import it.vashykator.sheets.BookkeeperCategory.FOOD
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class SheetsIOCoreTest {

    @Test
    fun `BookkeeperRow instantiation`() {
        val line = BookkeeperRow(LocalDate.now(), 5.5, "Description", FOOD)

        assertThat(line.category).isEqualTo(FOOD)
        assertThat(line.price).isEqualTo(5.5)
    }

    @Test
    fun `default values, category should be None`() {
        val row3 = BookkeeperRow(description = "Description", price = 2.0)
        assertThat(row3.category).isEqualTo(FOOD)
        assertThat(row3.price).isEqualTo(2.0)
    }
}
