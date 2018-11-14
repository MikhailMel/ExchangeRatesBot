package ru.scratty.bot.extensions.commands

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

interface IBotCommand {

    val commandPattern: Regex

    fun helpInfo(): String

    fun handleMessage(sender: AbsSender, message: Message)

}