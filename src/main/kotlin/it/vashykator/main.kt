package it.vashykator


import it.vashykator.bot.BotInitializer
import it.vashykator.sheets.SheetsIOClient
import it.vashykator.sheets.spreadsheet
import it.vashykator.sheets.worksheet
import org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY
import java.lang.ClassLoader.getSystemResource

private const val TOKEN_PATH = "TOKEN"
val TOKEN = readToken()

private const val SPREADSHEET_ID_PATH: String = "SPREADSHEET_ID"
val SPREADSHEET_ID: String = readSpreadsheetId()

private const val RANGE = "Foglio1!B16:E"
private val client: SheetsIOClient by lazy { SheetsIOClient(SPREADSHEET_ID, RANGE) }

fun main() {
    val spreadsheet =
        spreadsheet(SPREADSHEET_ID) {
            worksheet("Ottobre 2019") {
                earn("A3:C")
                expenditure("E3:H")
                waste("J3:M")
            }
        }

    System.setProperty(DEFAULT_LOG_LEVEL_KEY, "DEBUG")
    BotInitializer(TOKEN, client).startPolling()
}

private fun readToken(): String = getSystemResource(TOKEN_PATH).readText().removeSuffix("\n")
private fun readSpreadsheetId(): String = getSystemResource(SPREADSHEET_ID_PATH).readText().removeSuffix("\n")
