package it.vashykator.sheets

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class BookkeeperSectionTest {
    @Test
    internal fun `check invalid ranges initialization`() {
        assertThrows<IllegalArgumentException> {
            BookkeeperSection(Range("12"), Range("12"), Range("12"))
        }
    }

    @Test
    internal fun `check valid ranges initialization`() {
        BookkeeperSection(Range("aa!B:E"), Range("aa!B16:E"), Range("aa!B13:E32"))
    }
}
