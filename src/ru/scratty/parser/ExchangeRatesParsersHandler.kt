package ru.scratty.parser

import ru.scratty.db.DBHandlerMongo
import ru.scratty.db.IDBHandler
import ru.scratty.model.ExchangeRate
import java.time.Duration
import java.util.*

class ExchangeRatesParsersHandler private constructor() {

    private val db: IDBHandler = DBHandlerMongo.INSTANCE

    private val parsers = HashMap<ExchangeRatesResource, ExchangeRateParser>()

    private var timer = Timer()

    private class Holder {
        val instance = ExchangeRatesParsersHandler()
    }

    companion object {
        val INSTANCE: ExchangeRatesParsersHandler by lazy { Holder().instance }
    }

    init {
        val currencies = CurrencyAbbreviation.values().filter { it != CurrencyAbbreviation.NULL }
        val allCurrencies = db.getCurrenciesByAbbreviation(currencies)

        with(parsers) {
            put(ExchangeRatesResource.TINKOFF, TinkoffParser(allCurrencies))
            put(ExchangeRatesResource.SBERBANK, SberbankParser(allCurrencies))
            put(ExchangeRatesResource.ALFABANK, AlfabankParser(allCurrencies))
        }
    }

    fun startTimer(period: Duration) {
        timer.cancel()

        timer = Timer()
        timer.schedule(object: TimerTask() {
            override fun run() {
                parsers.forEach { it.value.parse() }
            }
        }, 0, period.toMillis())
    }

    fun getExchangeRate(resource: ExchangeRatesResource, currency: CurrencyAbbreviation): ExchangeRate {
        if (!parsers.containsKey(resource)) {
            return ExchangeRate()
        }

        return parsers[resource]!!.getExchangeRate(currency)
    }

    fun getResources(): List<ExchangeRatesResource> = parsers.keys.toList()
}