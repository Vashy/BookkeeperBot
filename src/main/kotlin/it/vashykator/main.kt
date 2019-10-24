package it.vashykator


import it.vashykator.bot.BotInitializer
import it.vashykator.sheets.SheetsIOClient
import java.lang.ClassLoader.getSystemResource

private const val TOKEN_PATH = "TOKEN"
val TOKEN = readToken()

private const val SPREADSHEET_ID_PATH: String = "SPREADSHEET_ID"
val SPREADSHEET_ID: String = readSpreadsheetId()

private const val RANGE = "Foglio1!B16:E"
private val client: SheetsIOClient by lazy { SheetsIOClient(SPREADSHEET_ID, RANGE) }

fun main() = BotInitializer(TOKEN, client).startPolling()

private fun readToken(): String = getSystemResource(TOKEN_PATH).readText().removeSuffix("\n")
private fun readSpreadsheetId(): String = getSystemResource(SPREADSHEET_ID_PATH).readText().removeSuffix("\n")
