package it.vashykator.sheets

import com.google.api.client.http.HttpTransport
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.AppendValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class SheetsIOClientTest {

    @Test
    internal fun `Sheets io initialization`() {
        val httpTransport = mockk<HttpTransport>()
        val service = mockk<Sheets>()
        val client = Bookkeeper.SheetsIOClient("123", "1A23", httpTransport, service)

        val spreadsheets = service.Spreadsheets()
        val values = spreadsheets.Values()
        every { service.spreadsheets() } returns spreadsheets
        every { spreadsheets.values() } returns values
        every {
            values.append(
                "123",
                "1A23",
                ValueRange().setValues(listOf(listOf("", 1.2, "desc", BookkeeperCategory.NONE)))
            )
        }


        val rows: AppendValuesResponse? = client.writeRow(BookkeeperRow(price = 1.2, description = "desc"))

    }
}
