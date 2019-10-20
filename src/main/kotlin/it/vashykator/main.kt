package it.vashykator

import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.dispatcher.text
import me.ivmg.telegram.network.fold
import java.lang.ClassLoader.getSystemResource

val TOKEN = readToken()

private const val TOKEN_PATH = "TOKEN"
private fun readToken(): String = getSystemResource(TOKEN_PATH).readText()

fun main() {
    val bot = bot {
        token = TOKEN
        dispatch {
            command("start") { bot, update ->
                val result = bot.sendMessage(chatId = update.message!!.chat.id, text = "Hi there!")
                result.fold({
                    // Do something with the response
                }, {
                    // Do something with the error
                })
            }

            text { bot, update ->
                if (update.message != null)
                    bot.sendMessage(chatId = update.message!!.chat.id, text = "AA!")
            }
        }
    }
    bot.startPolling()
}
