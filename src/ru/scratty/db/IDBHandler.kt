package ru.scratty.db

import org.bson.types.ObjectId
import ru.scratty.model.Company
import ru.scratty.model.Currency
import ru.scratty.model.ExchangeRate
import ru.scratty.model.User
import ru.scratty.parser.CurrencyAbbreviation

interface IDBHandler {

    fun isRegistered(id: Int): Boolean
    fun registrationUser(user: User)
    fun getUser(id: Int): User
    fun updateUser(user: User)

    fun getAllCompanies(): List<Company>
    fun getCompany(id: ObjectId): Company
    fun getCompany(name: String): Company
    fun getCompaniesByIds(list: List<ObjectId>): List<Company>

    fun getAllCurrencies(): List<Currency>
    fun getCurrency(id: ObjectId): Currency
    fun getCurrency(currencyAbbreviation: CurrencyAbbreviation): Currency
    fun getCurrenciesByAbbreviation(list: List<CurrencyAbbreviation>): List<Currency>
    fun getCurrenciesByIds(list: List<ObjectId>): List<Currency>

    fun addExchangeRate(rate: ExchangeRate): Boolean
}