import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.network.fold
import com.typesafe.config.ConfigFactory
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.stream.Collectors
import java.util.stream.Collectors.toList
import com.github.kotlintelegrambot.dispatch as dispatch1

fun main() {
    val config = ConfigFactory.parseFile(File("config.cfg"))
    val password = config.getString("password")

    val authorizedFile = File("authorized")
    val s = if (authorizedFile.exists()) {
        authorizedFile
            .bufferedReader()
            .readText()
            .split(",")
            .stream()
            .map {
                it.toLong()
            }
            .collect(toList())
    } else {
        ArrayList<Long>()
    }


    val bot = bot {
        token = config.getString("token")

        dispatch1 {
            command("start") {
                val result = bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "please authorize by command /auth {password}"
                )
            }
            command("auth") {
                if (s.contains(message.from!!.id)) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "user is already authorized"
                    )
                } else {
                    val joinedArgs = args.joinToString()
                    if (password.equals(joinedArgs)) {
                        s.add(message.from!!.id)

                        val ps = PrintStream(FileOutputStream(authorizedFile))
                        ps.print(s.stream()
                            .map {
                                it.toString()
                            }
                            .collect(Collectors.joining(",")))
                        ps.close()

                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = message.from!!.id.toString() + " was successfull authorized"
                        )
                    } else {
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = "wrong password"
                        )
                    }
                }
            }
            command("getimage") {
                if (!s.contains(message.from!!.id)) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "user is not authorized"
                    )
                } else {
                    val joinedArgs = args.joinToString()
                    sendPhoto(joinedArgs)
                }
            }
            command("m") {
                if (!s.contains(message.from!!.id)) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "user is not authorized"
                    )
                } else {
                    val joinedArgs = args.joinToString()
                    val list = config.getStringList("macros.$joinedArgs")
                    list.forEach {
                        sendPhoto(it)
                    }
                }
            }
            command("m1") {
                if (!s.contains(message.from!!.id)) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "user is not authorized"
                    )
                } else {
                    val list = config.getStringList("macros.1")
                    list.forEach {
                        sendPhoto(it)
                    }
                }
            }
            command("m2") {
                if (!s.contains(message.from!!.id)) {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "user is not authorized"
                    )
                } else {
                    val list = config.getStringList("macros.2")
                    list.forEach {
                        sendPhoto(it)
                    }
                }
            }
            command("ping") {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "pong"
                )
            }
        }
    }
    bot.startPolling()
}

private fun CommandHandlerEnvironment.sendPhoto(joinedArgs: String) {
    val f = File(joinedArgs)
    if (f.exists()) {
        bot.sendPhoto(
            chatId = ChatId.fromId(message.chat.id),
            photo = TelegramFile.ByFile(f),
            caption = joinedArgs
        )
    } else {
        bot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "file does not exists"
        )
    }
}