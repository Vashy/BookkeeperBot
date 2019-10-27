package it.vashykator.sheets

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.isZero
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class SheetsIOCoreTest {

    @Test
    fun `BookkeeperExpenseRow instantiation`() {
        val line = BookkeeperRow(LocalDate.now(), 5.5, "Description", BookkeeperCategory.FOOD)

        // Builder
        val row: BookkeeperRow = expenseRow {
            date = LocalDate.now()
            price = 5.5
            description = "Description"
            category = BookkeeperCategory.FOOD
        }

        assertThat(line).isEqualTo(row)
    }

    @Test
    fun `default values, category should be null`() {
        val row3 = expenseRow { description = "Description" }
        assertThat(row3.category).isNull()
        assertThat(row3.price).isZero()
    }
}
