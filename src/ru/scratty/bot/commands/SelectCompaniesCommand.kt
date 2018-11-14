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
import ru.scratty.model.Company
import ru.scratty.model.User
import ru.scratty.parser.ExchangeRatesResource

class SelectCompaniesCommand {

    private val db: IDBHandler = DBHandlerMongo.INSTANCE

    val simpleCommand = object : Command(Regex("company|источники"), "просмотр и изменение источников курса валют") {

        override fun handleMessage(sender: AbsSender, message: Message) {
            val user = db.getUser(message.from.id)
            val pair = handle(user)

            sendMessage(sender, message.chatId, pair.first, pair.second)
        }
    }

    val callbackCommand = object : CallbackCommand(Regex("^company_(\\w+)$")) {

        override fun handleMessage(sender: AbsSender, callbackQuery: CallbackQuery) {
            val user = db.getUser(callbackQuery.from.id)

            val values = commandPattern.matchEntire(callbackQuery.data)!!.groupValues
            if (values.size == 2) {
                val id = ObjectId(values[1])

                if (user.activeCompanies.contains(id)) {
                    user.activeCompanies.remove(id)
                } else {
                    user.activeCompanies.add(id)
                }

                db.updateUser(user)
            }

            val pair = handle(user)

            updateMsg(sender, callbackQuery.message, pair.first, pair.second)
        }
    }

    private fun handle(user: User): Pair<String, ReplyKeyboard> {
        val all = db.getAllCompanies()
        val inactive = all.filter { !user.activeCompanies.contains(it._id) }
        val active = db.getCompaniesByIds(user.activeCompanies)

        var text = if (active.isNotEmpty()) "Отслеживаемые источники:\n" else "Вы не выбрали ни один источник\n"
        active.forEach {
            text += "-${ExchangeRatesResource.valueOf(it.name).resourceName}\n"
        }

        text += "В меню ниже вы можете добавить и удалить отслеживаемые источники"

        return Pair(text, getCompaniesKeyboard(active, inactive))
    }

    private fun getCompaniesKeyboard(activeCompanies: List<Company>, inactiveCompanies: List<Company>): ReplyKeyboard {
        val keyboardMarkup = InlineKeyboardMarkup()
        val rows = ArrayList<ArrayList<InlineKeyboardButton>>()

        rows.addCurrencyRows(activeCompanies, true, 3)
        rows.addCurrencyRows(inactiveCompanies, false, 3)

        keyboardMarkup.keyboard = rows as List<MutableList<InlineKeyboardButton>>?
        return keyboardMarkup
    }

    private fun ArrayList<ArrayList<InlineKeyboardButton>>.addCurrencyRows(companies: List<Company>, active: Boolean, buttonsInRow: Int) {
        for (i in 0 until (companies.size + buttonsInRow - 1) / buttonsInRow) {
            val row = ArrayList<InlineKeyboardButton>()

            ff@ for (j in 0 until buttonsInRow) {
                if (companies.size > i * buttonsInRow + j) {
                    row.add(getButton(companies[i * buttonsInRow + j], active))
                } else {
                    break@ff
                }
            }
            add(row)
        }
    }

    private fun getButton(company: Company, active: Boolean): InlineKeyboardButton {
        return InlineKeyboardButton((if (active) "-" else "+") + ExchangeRatesResource.valueOf(company.name).resourceName).setCallbackData("company_${company._id}")
    }
}