package it.vashykator.bot

import com.google.api.services.sheets.v4.model.AppendValuesResponse
import it.vashykator.sheets.SheetsIOClient
import me.ivmg.telegram.Bot
import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.entities.ParseMode
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.network.fold
import it.vashykator.sheets.fromList as fromListToBookkeeperRow

class BotInitializer(private val token: String, private val client: SheetsIOClient) {

    fun startPolling() = initializeBookkeeperBot().startPolling()

    private fun initializeBookkeeperBot(): Bot {
        return bot {
            token = this@BotInitializer.token
//        logLevel = HttpLoggingInterceptor.Level.BASIC

            dispatch {
                command("start") { bot, update ->
                    bot.sendMessage(chatId = update.chatId, text = "Hi there!").fold { println(it.errorBody) }
                }

                command("add") { bot, update, args ->
                    val bookkeeperRow = fromListToBookkeeperRow(args)
                    println(bookkeeperRow)

                    if (bookkeeperRow == null) return@command

                    val result: AppendValuesResponse? =
                        client.writeRow(bookkeeperRow)

                    if (result?.isNotEmpty() == true)
                        bot.sendMessage(
                            chatId = update.chatId,
                            text = "Updated cells: $bookkeeperRow"
                        ).fold { println(it.errorBody) }
                }

                command("get") { bot, update, args ->

                    val values =
                        if (args.isNotEmpty()) client.getRows(args[0].toInt())
                        else client.getRows()

                    val msg = values.reduce { acc, s -> acc + s }

                    bot.sendMessage(
                        chatId = update.chatId,
                        text = msg,
                        parseMode = ParseMode.MARKDOWN
                    ).fold { println(it.errorBody) }
                }
            }
        }
    }
}

val Update.chatId: Long
    get() = message?.chat?.id ?: -1L
