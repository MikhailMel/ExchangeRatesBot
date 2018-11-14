package ru.scratty.bot.commands

import org.bson.types.ObjectId
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender
import ru.scratty.bot.extensions.commands.CallbackCommand
import ru.scratty.bot.extensions.commands.Command
import ru.scratty.db.DBHandlerMongo
import ru.scratty.db.IDBHandler
import ru.scratty.model.Currency
import ru.scratty.model.User

class SelectCurrenciesCommand {

    private val db: IDBHandler = DBHandlerMongo.INSTANCE

    val simpleCommand = object: Command(Regex("currency|валюты"), "просмотр и изменение отслеживаемых валют") {

        override fun handleMessage(sender: AbsSender, message: Message) {
            val user = db.getUser(message.from.id)
            val pair = handle(user)

            sendMessage(sender, message.chatId, pair.first, pair.second)
        }
    }

    val callbackCommand = object: CallbackCommand(Regex("^currency_(\\w+)$")) {

        override fun handleMessage(sender: AbsSender, callbackQuery: CallbackQuery) {
            val user = db.getUser(callbackQuery.from.id)

            val values = commandPattern.matchEntire(callbackQuery.data)!!.groupValues
            if (values.size == 2) {
                val id = ObjectId(values[1])

                if (user.activeCurrencies.contains(id)) {
                    user.activeCurrencies.remove(id)
                } else {
                    user.activeCurrencies.add(id)
                }

                db.updateUser(user)
            }

            val pair = handle(user)

            updateMsg(sender, callbackQuery.message, pair.first, pair.second)
        }
    }

    private fun handle(user: User): Pair<String, ReplyKeyboard> {
        val all = db.getAllCurrencies()
        val inactive = all.filter { !user.activeCurrencies.contains(it._id) }
        val active = db.getCurrenciesByIds(user.activeCurrencies)

        var text = if (active.isNotEmpty()) "Выбранные валюты:\n" else "Вы не выбрали ни одной валюты\n"
        active.forEach {
            text += "-${it.name}\n"
        }

        text += "В меню ниже вы можете добавить и удалить отслеживаемые валюты"

        return Pair(text, getCurrenciesKeyboard(active, inactive))
    }

    private fun getCurrenciesKeyboard(activeCurrencies: List<Currency>, inactiveCurrencies: List<Currency>): ReplyKeyboard {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = ArrayList<ArrayList<InlineKeyboardButton>>()

        rows.addCurrencyRows(activeCurrencies, true, 3)
        rows.addCurrencyRows(inactiveCurrencies, false, 3)

        keyboardMarkup.keyboard = rows as List<MutableList<InlineKeyboardButton>>?
        return keyboardMarkup
    }

    private fun getButton(currency: Currency, active: Boolean): InlineKeyboardButton {
        return InlineKeyboardButton((if (active) "-" else "+") + currency.name).setCallbackData("currency_${currency._id}")
    }

    private fun ArrayList<ArrayList<InlineKeyboardButton>>.addCurrencyRows(currencies: List<Currency>, active: Boolean, buttonsInRow: Int) {
        for (i in 0 until (currencies.size + buttonsInRow - 1) / buttonsInRow) {
            val row = ArrayList<InlineKeyboardButton>()

            ff@for (j in 0 until buttonsInRow) {
                if (currencies.size > i * buttonsInRow + j) {
                    row.add(getButton(currencies[i * buttonsInRow + j], active))
                } else {
                    break@ff
                }
            }
            add(row)
        }
    }
}