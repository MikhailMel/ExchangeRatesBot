package ru.scratty.bot

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.scratty.BotConfig.Companion.BOT_TOKEN
import ru.scratty.BotConfig.Companion.BOT_USERNAME
import ru.scratty.bot.commands.*
import ru.scratty.bot.extensions.CommandsBot

class ExchangeRatesBot: CommandsBot(BOT_USERNAME, BOT_TOKEN) {

    init {
        val selectCurrenciesCommand = SelectCurrenciesCommand()
        val selectCompaniesCommand = SelectCompaniesCommand()

        registerCommand(StartCommand())
        registerCommand(ExchangeRateCommand())
        registerCommand(selectCurrenciesCommand.simpleCommand)
        registerCommand(selectCompaniesCommand.simpleCommand)

        registerHelpCommand(HelpCommand())

        registerCallbackCommand(selectCurrenciesCommand.callbackCommand)
        registerCallbackCommand(selectCompaniesCommand.callbackCommand)
    }

    override fun incorrectCommand(message: Message) {
        val sendMessage = SendMessage()
        sendMessage.enableMarkdown(true)
        sendMessage.chatId = message.chatId.toString()
        sendMessage.text = "Неизвестная команда, введите /help, чтобы узнать доступные команды"

        try {
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }
}