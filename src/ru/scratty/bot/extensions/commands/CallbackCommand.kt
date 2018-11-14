package ru.scratty.bot.extensions.commands

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

abstract class CallbackCommand(final override val commandPattern: Regex): ICallbackBotCommand {

    protected fun updateMsg(sender: AbsSender, message: Message, text: String, replyKeyboard: ReplyKeyboard = ReplyKeyboard {}) {
        val editMessage = EditMessageText()
        with(editMessage) {
            chatId = message.chatId.toString()
            messageId = message.messageId
            this.text = text
            replyMarkup = replyKeyboard as InlineKeyboardMarkup
        }

        try {
            sender.execute(editMessage)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }
}