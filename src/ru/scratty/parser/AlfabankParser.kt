package ru.scratty.parser

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import ru.scratty.model.Currency
import ru.scratty.model.ExchangeRate
import ru.scratty.util.HttpUtil
import java.util.*

class AlfabankParser(currencies: List<Currency>): ExchangeRateParser(ExchangeRatesResource.ALFABANK, currencies) {

    companion object {
        const val URL = "https://alfabank.ru/ext-json/0.2/exchange/cash?offset=0&limit=1&mode=rest"
    }

    override fun parse() {
        val json = HttpUtil.get(URL)
        parseJson(json, currencies)
    }

    override fun isSupportedCurrency(currencyAbbreviation: CurrencyAbbreviation): Boolean {
        return when(currencyAbbreviation) {
            CurrencyAbbreviation.USD,
            CurrencyAbbreviation.EUR -> true

            else -> false
        }
    }

    private fun parseJson(json: String, currencies: List<Currency>) {
        val objectMapper = ObjectMapper()
        val rootNode = objectMapper.readTree(json)

        currencies.forEach {
            val currencyNode = rootNode.get(it.name.name.toLowerCase())
            if (!currencyNode.isNull) {
                val pair = parseBuySell(currencyNode)

                addExchangeRate(it, ExchangeRate(company._id, Date().time, it._id, pair.first, pair.second))
            }
        }
    }

    private fun parseBuySell(rates: JsonNode): Pair<Double, Double> {
        var buy = 0.0
        var sell = 0.0

        for (i in 0 until 2) {
            val rate = rates[i]
            val value = rate.get("value").asDouble()

            when(rate.get("type").asText()) {
                "buy" -> buy = value
                "sell" -> sell = value
            }
        }

        return Pair(buy, sell)
    }
}