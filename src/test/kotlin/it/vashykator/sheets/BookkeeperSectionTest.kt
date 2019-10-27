package it.vashykator.sheets

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class BookkeeperSectionTest {
    @Test
    internal fun `check invalid ranges initialization`() {
        assertThrows<IllegalArgumentException> {
            BookkeeperSection(SheetRange("12"), SheetRange("12"), SheetRange("12"))
        }
    }

    @Test
    internal fun `check valid ranges initialization`() {
        BookkeeperSection(SheetRange("Sheet 2!B:E"), SheetRange("aa!B16:E"), SheetRange("aa!B13:E32"))
    }
}
