package ru.scratty.bot.extensions

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.scratty.bot.extensions.commands.Command
import ru.scratty.bot.extensions.commands.IBotCommand
import ru.scratty.bot.extensions.commands.ICallbackBotCommand
import ru.scratty.bot.extensions.commands.IHelpBotCommand

abstract class CommandsBot(private val username: String,
                  private val token: String): TelegramLongPollingBot() {

    private val commands = HashMap<Regex, IBotCommand>()
    private val callbackCommands = HashMap<Regex, ICallbackBotCommand>()

    private var helpCommand: IHelpBotCommand = EmptyHelpCommand()

    override fun getBotUsername() = username

    override fun getBotToken() = token

    override fun onUpdateReceived(update: Update?) {
        if (update!!.hasMessage() && update.message.hasText()) {
            if (!handleCommand(update.message)) {
                incorrectCommand(update.message)
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackCommand(update.callbackQuery)
        }
    }

    protected fun registerCommand(command: IBotCommand) {
        commands[command.commandPattern] = command
        helpCommand.commands.add(command.toString())
    }

    protected fun registerHelpCommand(command: IHelpBotCommand) {
        helpCommand = command
        helpCommand.commands.addAll(commands.map { it.value.helpInfo() })
    }

    protected fun registerCallbackCommand(command: ICallbackBotCommand) {
        callbackCommands[command.commandPattern] = command
    }

    private fun handleCommand(message: Message): Boolean {
        if (message.hasText()) {
            var command = if (message.text.startsWith(Command.COMMAND_CHAR))
                message.text.substring(1)
            else
                message.text
            command = command.toLowerCase().trim()


            for (item in commands) {
                if (item.key.matches(command)) {
                    item.value.handleMessage(this, message)

                    return true
                }
            }

            if (helpCommand.commandPattern.matches(command)) {
                helpCommand.handleMessage(this, message)
                return true
            }
        }

        return false
    }

    private fun handleCallbackCommand(callbackQuery: CallbackQuery) {
        val command = callbackQuery.data

        for (item in callbackCommands) {
            if (item.key.matches(command)) {
                item.value.handleMessage(this, callbackQuery)

                return
            }
        }
    }

    abstract fun incorrectCommand(message: Message)

    private class EmptyHelpCommand: IHelpBotCommand {

        override val commands: ArrayList<String> = ArrayList()
        override val commandPattern = Regex("")

        override fun helpInfo() = ""

        override fun handleMessage(sender: AbsSender, message: Message) {
        }

    }

}