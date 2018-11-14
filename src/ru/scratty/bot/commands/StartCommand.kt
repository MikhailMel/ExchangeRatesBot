package ru.scratty.bot.commands

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.scratty.bot.extensions.commands.Command
import ru.scratty.db.DBHandlerMongo
import ru.scratty.db.IDBHandler
import ru.scratty.model.User

class StartCommand: Command(Regex("start"), "начало работы с ботом") {

    private val db: IDBHandler = DBHandlerMongo.INSTANCE

    override fun handleMessage(sender: AbsSender, message: Message) {
        val user = User(message.from)
        val text = if (!db.isRegistered(user._id)) {
            db.registrationUser(user)

            "Добро пожаловать, ${user.fName}!\n" +
                    "Данный бот предназаначен для отслеживания курса валют в разных банках.\n" +
                    "Для получения справки используйте команду /help"
        } else {
            "${user.fName}, вы уже зарегестрированы:)"
        }

        sendMessage(sender, message.chatId, text, mainMenuReplyKeyboard())
    }
}