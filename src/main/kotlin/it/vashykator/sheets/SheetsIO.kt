package it.vashykator.sheets

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.AppendValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import it.vashykator.Matrix
import it.vashykator.SPREADSHEET_ID
import it.vashykator.emptyMatrix
import mu.KotlinLogging
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.lang.ClassLoader.getSystemResourceAsStream
import java.time.LocalDate
import com.google.api.services.sheets.v4.Sheets as GoogleApiSheets
import com.google.api.services.sheets.v4.Sheets as SheetsByGoogle

private const val APPLICATION_NAME = "Bookkeeper"
private const val VALUE_INPUT_OPTION = "USER_ENTERED"
private const val INSERT_DATA_OPTION = "INSERT_ROWS"
private const val TOKENS_DIRECTORY_PATH = "apiTokens"
private const val CREDENTIALS_FILE_PATH = "credentials.json"

private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
private val SCOPES = listOf(SheetsScopes.SPREADSHEETS)

private const val DEFAULT_ROWS_FETCHED = 3
private val log = KotlinLogging.logger { }

object Bookkeeper {

    private const val RANGE = "Foglio1!B16:E"
    private val httpTransport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()

    interface SheetsIO {
        fun readRows(rowCount: Int = DEFAULT_ROWS_FETCHED): List<String>
        fun writeRow(row: BookkeeperRow): AppendValuesResponse?
    }

    interface SheetsConnection {
        val httpTransport: HttpTransport
    }

    class SheetsIOClient(
        private val spreadsheetId: String,
        private val range: String,
        override val httpTransport: HttpTransport,
        private val service: SheetsByGoogle
    ) : SheetsIO, SheetsConnection {

        override fun readRows(rowCount: Int): List<String> {
            val values = valueRange().getValues()
            return if (values.isEmpty()) {
                log.warn { "No data found" }
                listOf()
            } else {
                values.toMatrixOfStrings().toStringList(rowCount)
            }
        }

        override fun writeRow(row: BookkeeperRow): AppendValuesResponse? {
            val values = row.toSingleRowMatrix()
            val body = ValueRange().setValues(values)

            return service.spreadsheets().values().append(spreadsheetId, range, body)
                .setValueInputOption(VALUE_INPUT_OPTION)
                .setInsertDataOption(INSERT_DATA_OPTION)
                .execute()
        }

        private fun valueRange(): ValueRange = service.spreadsheets()
            .values()[spreadsheetId, range]
            .execute()

    }

    val client: SheetsIOClient by lazy {
        SheetsIOClient(
            SPREADSHEET_ID,
            RANGE,
            httpTransport,
            GoogleApiSheets.Builder(
                httpTransport,
                JSON_FACTORY,
                httpTransport.getCredentials()
            )
                .setApplicationName(APPLICATION_NAME)
                .build()
        )
    }
}

private fun BookkeeperRow.toSingleRowMatrix(): Matrix<Any> =
    listOf(
        listOf(
            date.toSlashyDateString(),
            price,
            description,
            category.toString()
        )
    )

private fun Matrix<String>.toStringList(count: Int): List<String> {
    val mappedList = takeLast(count).joinToPipedString()

    log.debug { "Read rows: $mappedList" }
    return mappedList
}

private fun Matrix<Any>.toMatrixOfStrings(): Matrix<String> {
    @Suppress("UNCHECKED_CAST")
    return this as? Matrix<String> ?: emptyMatrix()
}

private fun Matrix<String>.joinToPipedString(): List<String> = map { it.joinToString(separator = " | ") }

private fun LocalDate.toSlashyDateString(): String = toString().replace("-", "/")

private fun HttpTransport.getCredentials(): Credential {
    val input = getSystemResourceAsStream(CREDENTIALS_FILE_PATH)
    val clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY,
        InputStreamReader(input ?: throw FileNotFoundException("$CREDENTIALS_FILE_PATH not found"))
    )

    val flow = GoogleAuthorizationCodeFlow
        .Builder(this, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build()

    val receiver = LocalServerReceiver.Builder().setPort(8888).build()
    return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
}
