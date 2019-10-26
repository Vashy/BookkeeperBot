package it.vashykator.bot

import com.google.api.services.sheets.v4.model.AppendValuesResponse
import it.vashykator.sheets.SheetsIOClient
import it.vashykator.sheets.pretty
import me.ivmg.telegram.Bot
import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.entities.ParseMode.MARKDOWN
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.network.fold
import mu.KotlinLogging
import okhttp3.logging.HttpLoggingInterceptor
import it.vashykator.sheets.fromListOrNull as fromListToBookkeeperRow

private val log = KotlinLogging.logger { }

class BotInitializer(private val token: String, private val client: SheetsIOClient) {

    fun startPolling() = initializeBookkeeperBot().startPolling()

    private fun initializeBookkeeperBot(): Bot {
        return bot {
            token = this@BotInitializer.token
            logLevel = HttpLoggingInterceptor.Level.BASIC

            dispatch {
                command("start") { bot, update ->
                    bot.sendMessage(chatId = update.chatId, text = "Hi there!").fold { log.warn { it.errorBody } }
                }

                command("add") { bot, update, args ->
                    val bookkeeperRow = fromListToBookkeeperRow(args)
                    log.debug { "Converting $args to BookkeeperRow" }

                    if (bookkeeperRow == null) {
                        log.error { "Conversion failed" }
                        return@command
                    }

                    val result: AppendValuesResponse? =
                        client.writeRow(bookkeeperRow)

                    if (result?.isNotEmpty() == true)
                        bot.sendMessage(
                            chatId = update.chatId,
                            text = """Updated cells = `${result.updates.updatedCells}`
                                |
                                |Value = `${bookkeeperRow.pretty()}`""".trimMargin(),
                            parseMode = MARKDOWN
                        ).fold { log.warn { it.errorBody } }
                }

                command("get") { bot, update, args ->

                    val values =
                        if (args.isNotEmpty()) client.readRows(args[0].toInt())
                        else client.readRows()

                    val msg = values.reduce { acc, s -> "$acc\n$s" }

                    bot.sendMessage(
                        chatId = update.chatId,
                        text = msg,
                        parseMode = MARKDOWN
                    ).fold { log.warn { it.errorBody } }
                }
            }
        }
    }
}

val Update.chatId: Long
    get() = message?.chat?.id ?: -1L
