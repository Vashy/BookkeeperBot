package it.vashykator.bot

import com.google.api.services.sheets.v4.model.AppendValuesResponse
import it.vashykator.sheets.BookkeeperRowFactoryInstance
import it.vashykator.sheets.Bookkeeper
import it.vashykator.sheets.pretty
import me.ivmg.telegram.*
import me.ivmg.telegram.dispatcher.Dispatcher
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.entities.ParseMode.MARKDOWN
import me.ivmg.telegram.entities.Update
import me.ivmg.telegram.network.fold
import mu.KotlinLogging
import okhttp3.logging.HttpLoggingInterceptor

private val log = KotlinLogging.logger { }

class BotInitializer(private val token: String, private val client: Bookkeeper.SheetsIOClient) {

    fun startPolling() = initializeBookkeeperBot().startPolling()

    private fun initializeBookkeeperBot(): Bot {
        return bot {
            token = this@BotInitializer.token
            logLevel = HttpLoggingInterceptor.Level.BASIC

            dispatch {
                safeCommand("start") { bot, update ->
                    bot.sendMessage(chatId = update.chatId, text = "Hi there!").fold { log.error { it.errorBody } }
                }

                safeCommand("add") { bot, update, args ->
                    val bookkeeperRow = BookkeeperRowFactoryInstance.from(args)
                    log.debug { "Converting $args to BookkeeperRow" }

                    if (bookkeeperRow == null) {
                        log.warn { "Conversion failed" }
                        bot.sendMessage(
                            chatId = update.chatId,
                            text = "Cannot add row `$args`. Command format:\n`/add [yyyy-MM-dd] x.y description [| CATEGORY]` ",
                            parseMode = MARKDOWN
                        ).fold { log.error { it.errorBody } }
                        return@safeCommand
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
                        ).fold { log.error { it.errorBody } }
                }

                safeCommand("get") { bot, update, args ->

                    val values =
                        if (args.isNotEmpty()) client.readRows(args[0].toInt())
                        else client.readRows()

                    val msg = values.reduce { acc, s -> "$acc\n$s" }

                    bot.sendMessage(
                        chatId = update.chatId,
                        text = msg,
                        parseMode = MARKDOWN
                    ).fold { log.error { it.errorBody } }
                }
            }
        }
    }
}

val Update.chatId: Long
    get() = message?.chat?.id ?: -1L

fun Dispatcher.safeCommand(
    command: String,
    body: HandleUpdate
) = try {
    command(command, body)
} catch (e: Exception) {
    log.warn { e.localizedMessage }
}

fun Dispatcher.safeCommand(
    command: String,
    body: CommandHandleUpdate
) = try {
    command(command, body)
} catch (e: Exception) {
    log.warn { e.localizedMessage }
}
