package ru.scratty.parser

import ru.scratty.db.DBHandlerMongo
import ru.scratty.db.IDBHandler
import ru.scratty.model.Company
import ru.scratty.model.Currency
import ru.scratty.model.ExchangeRate
import java.util.*

abstract class ExchangeRateParser(resource: ExchangeRatesResource, allCurrencies: List<Currency>) {

    private val db: IDBHandler = DBHandlerMongo.INSTANCE

    protected val company: Company
    protected val currencies: List<Currency>

    private val map = HashMap<CurrencyAbbreviation, ExchangeRate>()

    init {
        company = db.getCompany(resource.name)
        currencies = allCurrencies.filter { isSupportedCurrency(it.name) }
    }

    fun containsCurrency(currency: CurrencyAbbreviation) = map.containsKey(currency)

    fun getExchangeRate(currency: CurrencyAbbreviation) = map.getOrDefault(currency, ExchangeRate())

    protected fun addExchangeRate(currency: Currency, exchangeRate: ExchangeRate) {
        if (db.addExchangeRate(exchangeRate)) {
            map[currency.name] = exchangeRate
        }
    }

    abstract fun parse()

    protected abstract fun isSupportedCurrency(currencyAbbreviation: CurrencyAbbreviation): Boolean
}