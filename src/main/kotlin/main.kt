import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.network.fold
import java.lang.ClassLoader.getSystemResource

val TOKEN = readToken("TOKEN")

private fun readToken(path: String): String = getSystemResource(path).readText()

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
        }
    }
    bot.startPolling()
}
