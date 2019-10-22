package it.vashykator

import com.google.api.services.sheets.v4.model.AppendValuesResponse
import it.vashykator.sheets.SheetsClient
import me.ivmg.telegram.Bot
import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.entities.ParseMode
import me.ivmg.telegram.entities.Update
import java.lang.ClassLoader.getSystemResource
import it.vashykator.sheets.fromList as fromListToBookkeeperRow

val TOKEN = readToken()
const val SPREADSHEET_ID: String = "1ezLJtE8f7cOfT39HBl0UObnWrBjjfBPvMNz_nHItcpc"
const val RANGE = "Foglio1!B16:E"

private const val TOKEN_PATH = "TOKEN"

val client: SheetsClient by lazy { SheetsClient(SPREADSHEET_ID, RANGE) }

fun main() {
    initializeBookkeeperBot().startPolling()
}

private fun initializeBookkeeperBot(): Bot {
    return bot {
        token = TOKEN
        dispatch {
            command("start") { bot, update -> bot.sendMessage(chatId = update.chatId, text = "Hi there!") }

            command("add") { bot, update, args ->
                val bookkeeperRow = fromListToBookkeeperRow(args)
                println(bookkeeperRow)

                if (bookkeeperRow == null) return@command

                val result: AppendValuesResponse? = client.writeValues(bookkeeperRow)

                if (result?.isEmpty() == false)
                    bot.sendMessage(chatId = update.chatId, text = "Updated cells: $bookkeeperRow")
            }

            command("get") { bot, update ->
                if (update.message != null) {
                    val values = client.getValues(3)
                    val msg = values.reduce { acc, s -> acc + s }
                    bot.sendMessage(chatId = update.chatId, text = msg, parseMode = ParseMode.MARKDOWN)
                }
            }
        }
    }
}

val Update.chatId: Long
    get() = message?.chat?.id ?: -1L

private fun readToken(): String = getSystemResource(TOKEN_PATH).readText()
