package ru.scratty.bot.commands

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.scratty.bot.extensions.commands.Command
import ru.scratty.bot.extensions.commands.IHelpBotCommand

class HelpCommand: Command(Regex("help|помощь"), ""), IHelpBotCommand {

    override val commands = ArrayList<String>()

    override fun handleMessage(sender: AbsSender, message: Message) {
        var text = "Доступные команды бота:\n"
        commands.forEach {
            if (it.isNotEmpty()) {
                text += "\t$it\n"
            }
        }

        sendMessage(sender, message.chatId, text)
    }
}