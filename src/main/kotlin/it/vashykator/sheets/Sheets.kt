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
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.AppendValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.lang.ClassLoader.getSystemResourceAsStream
import java.time.LocalDate

private const val APPLICATION_NAME = "Bookkeeper"
private const val VALUE_INPUT_OPTION = "USER_ENTERED"
private const val INSERT_DATA_OPTION = "INSERT_ROWS"
private const val TOKENS_DIRECTORY_PATH = "apiTokens"
private const val CREDENTIALS_FILE_PATH = "credentials.json"

private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
private val SCOPES = listOf(SheetsScopes.SPREADSHEETS)

class SheetsClient(val spreadsheetId: String, val range: String) {
    private val httpTransport: HttpTransport by lazy { GoogleNetHttpTransport.newTrustedTransport() }

    val service: Sheets by lazy {
        Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    fun valueRange(): ValueRange = service.spreadsheets()
        .values()[spreadsheetId, range]
        .execute()

    fun getValues(count: Int = 3): List<String> {
        val values = valueRange().getValues()
        return if (values.isEmpty()) {
            println("No data found")
            listOf()
        } else {
            values.forEach { println("${it[0]} | ${it[1]} | ${it[2]} | ${it[3]}") }
            val map: List<String> = values
                .filterIndexed { index, _ -> index >= values.size - count }
                .map { "${it[0]} | ${it[1]} | ${it[2]} | ${it[3]}\n" }
            map
        }
    }

    fun writeValues(row: BookkeeperExpenseRow): AppendValuesResponse? {
        val values = listOf(listOf(row.date.toSlashyDate(), row.price, row.description))
        val body = ValueRange().setValues(values)

        return service.spreadsheets().values().append(spreadsheetId, range, body)
            .setValueInputOption(VALUE_INPUT_OPTION)
            .setInsertDataOption(INSERT_DATA_OPTION)
            .execute()
    }
}

private fun LocalDate.toSlashyDate(): String = toString().replace("-", "/")


private fun getCredentials(HTTP_TRANSPORT: HttpTransport): Credential {
    val input = getSystemResourceAsStream(CREDENTIALS_FILE_PATH)
    val clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY,
        InputStreamReader(input ?: throw FileNotFoundException("$CREDENTIALS_FILE_PATH not found"))
    )

    val flow = GoogleAuthorizationCodeFlow
        .Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build()

    val receiver = LocalServerReceiver.Builder().setPort(8888).build()
    return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
}
