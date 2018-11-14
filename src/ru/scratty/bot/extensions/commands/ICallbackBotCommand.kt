package ru.scratty.bot.extensions.commands

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender

interface ICallbackBotCommand {

    val commandPattern: Regex

    fun handleMessage(sender: AbsSender, callbackQuery: CallbackQuery)

}