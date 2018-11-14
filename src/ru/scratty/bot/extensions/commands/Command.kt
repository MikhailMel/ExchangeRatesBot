package ru.scratty.bot.extensions.commands

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

abstract class Command(final override val commandPattern: Regex,
                       private val description: String = ""): IBotCommand {

    companion object {
        const val COMMAND_CHAR = '/'
    }

    init {
        if (commandPattern.toString().isEmpty()) {
            throw IllegalArgumentException("command pattern can not be empty")
        }
    }

    override fun helpInfo(): String {
        if (description.isEmpty())
            return ""

        val command = commandPattern.toString().split("|")[0]
        return "$COMMAND_CHAR$command - $description"
    }

    protected fun sendMessage(sender: AbsSender, chatId: Long, text: String, replyKeyboard: ReplyKeyboard = ReplyKeyboard {}) {
        val sendMessage = SendMessage()

        with(sendMessage) {
            enableMarkdown(true)
            this.chatId = chatId.toString()
            this.text = text
            replyMarkup = replyKeyboard
        }

        try {
            sender.execute(sendMessage)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    protected fun mainMenuReplyKeyboard(): ReplyKeyboard {
        val keyboardMarkup = ReplyKeyboardMarkup()
        val keyboard = ArrayList<KeyboardRow>()

        var row = KeyboardRow()
        row.add("Курс валют")
        keyboard.add(row)


        row = KeyboardRow()
        row.add("Валюты")
        row.add("Источники")
        keyboard.add(row)
        keyboardMarkup.keyboard = keyboard
        keyboardMarkup.resizeKeyboard = true

        return keyboardMarkup
    }
}