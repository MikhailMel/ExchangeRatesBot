package ru.scratty.bot.commands

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.scratty.bot.extensions.commands.Command
import ru.scratty.db.DBHandlerMongo
import ru.scratty.db.IDBHandler
import ru.scratty.parser.ExchangeRatesParsersHandler
import ru.scratty.parser.ExchangeRatesResource

class ExchangeRateCommand: Command(Regex("rate|курс валют"), "актуальный курс валют") {

    private val db: IDBHandler = DBHandlerMongo.INSTANCE
    private val parsersHandler = ExchangeRatesParsersHandler.INSTANCE

    override fun handleMessage(sender: AbsSender, message: Message) {
        val user = db.getUser(message.from.id)
        val companies = db.getCompaniesByIds(user.activeCompanies)
        val currencies = db.getCurrenciesByIds(user.activeCurrencies)

        if (companies.isEmpty() || currencies.isEmpty()) {
            var text = ""
            if (companies.isEmpty()) {
                text += "У вас не выбрано ни одного источника курса\n"
            }
            if (currencies.isEmpty()) {
                text += "У вас не выбрано ни одной валюты"
            }

            sendMessage(sender, message.chatId, text)
        }

        companies.forEach {res ->
            val resource = ExchangeRatesResource.valueOf(res.name)
            var text = ExchangeRatesResource.valueOf(res.name).resourceName

            currencies.forEach { cur ->
                val rate = parsersHandler.getExchangeRate(resource, cur.name)
                text += "\n${cur.name}: ${rate.buy} | ${rate.sell}"
            }

            sendMessage(sender, message.chatId, text)
        }
    }
}