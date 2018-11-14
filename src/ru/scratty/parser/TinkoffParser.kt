package ru.scratty.parser

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import ru.scratty.model.Currency
import ru.scratty.model.ExchangeRate
import ru.scratty.util.HttpUtil
import java.util.*

class TinkoffParser(currencies: List<Currency>): ExchangeRateParser(ExchangeRatesResource.TINKOFF, currencies) {

    companion object {
        const val URL = "https://api.tinkoff.ru/v1/grouped_requests"

        const val NEED_CATEGORY = "CUTransferFrom10To100"
    }

    override fun parse() {
        currencies.forEach {
            val pair = parse(it)
            val exchangeRate = ExchangeRate(company._id, Date().time, it._id, pair.first, pair.second)

            addExchangeRate(it, exchangeRate)
        }
    }

    override fun isSupportedCurrency(currencyAbbreviation: CurrencyAbbreviation): Boolean {
        return when(currencyAbbreviation) {
            CurrencyAbbreviation.USD,
            CurrencyAbbreviation.EUR -> true

            else -> false
        }
    }

    private fun parse(currency: Currency): Pair<Double, Double> {
        val params = ArrayList<NameValuePair>()
        params.add(BasicNameValuePair("requestsData", "[{\"key\":0,\"operation\":\"currency_rates\",\"params\":{\"wuid\":\"e7f21ac23bb544adb1c56e774febb8ec\",\"from\":\"${currency.name}\",\"to\":\"RUB\"}}]"))
        val json = HttpUtil.post(URL, params)
        return parseJson(json)
    }

    private fun parseJson(json: String): Pair<Double, Double> {
        val objectMapper = ObjectMapper()
        val rootNode = objectMapper.readTree(json)
        val ratesNode = rootNode.get("payload").get("0").get("payload").path("rates")

        val ratesIterator = ratesNode.elements()
        ratesIterator.forEach {
            val category = it.get("category").asText()

            if (category == NEED_CATEGORY) {
                val buy = it.get("buy").asDouble()
                val sell = it.get("sell").asDouble()

                return Pair(buy, sell)
            }
        }

        return Pair(0.0, 0.0)
    }
}