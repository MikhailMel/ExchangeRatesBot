package ru.scratty.parser

import com.fasterxml.jackson.databind.ObjectMapper
import ru.scratty.model.Currency
import ru.scratty.model.ExchangeRate
import ru.scratty.util.HttpUtil
import java.util.*

class SberbankParser(currencies: List<Currency>): ExchangeRateParser(ExchangeRatesResource.SBERBANK, currencies) {

    companion object {
        const val URL = "https://www.sberbank.ru/portalserver/proxy/?pipe=shortCachePipe" +
                "&url=http%3A%2F%2Flocalhost%2Frates-web%2FrateService%2Frate%2Fcurrent%3FregionId%3D77%26rateCategory%3Dbeznal%26currencyCode%3D"
    }

    override fun parse() {
        currencies.forEach {
            val pair = parse(it)
            val exchangeRate = ExchangeRate(company._id, Date().time, it._id, pair.first, pair.second)

            addExchangeRate(it, exchangeRate)
        }
    }

    override fun isSupportedCurrency(currencyAbbreviation: CurrencyAbbreviation): Boolean {
        return when (currencyAbbreviation) {
            CurrencyAbbreviation.USD,
            CurrencyAbbreviation.EUR -> true

            else -> false
        }
    }

    private fun parse(currency: Currency): Pair<Double, Double> {
        val currencyNumber = getCurrencyNumber(currency.name)
        val json = HttpUtil.get(URL + currencyNumber)
        return parseJson(json, currencyNumber)
    }

    private fun parseJson(json: String, currencyNumber: Int): Pair<Double, Double> {
        val objectMapper = ObjectMapper()
        val rootNode = objectMapper.readTree(json)
        val rateNode = rootNode.get("beznal").get(currencyNumber.toString()).get("0")

        val buy = rateNode.get("buyValue").asDouble()
        val sell = rateNode.get("sellValue").asDouble()
        return Pair(buy, sell)
    }

    private fun getCurrencyNumber(currencyAbbreviation: CurrencyAbbreviation): Int {
        return when (currencyAbbreviation) {
            CurrencyAbbreviation.USD -> 840
            CurrencyAbbreviation.EUR -> 978
            else -> 0
        }
    }
}