package ru.scratty.db

import com.mongodb.ServerAddress
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.ReplaceOptions
import org.bson.types.ObjectId
import org.litote.kmongo.*
import ru.scratty.BotConfig.Companion.COL_COMPANY
import ru.scratty.BotConfig.Companion.COL_CURRENCY
import ru.scratty.BotConfig.Companion.COL_EXCHANGE_RATE
import ru.scratty.BotConfig.Companion.COL_USER
import ru.scratty.BotConfig.Companion.DB_DATABASE
import ru.scratty.BotConfig.Companion.DB_HOST
import ru.scratty.BotConfig.Companion.DB_PORT
import ru.scratty.model.Company
import ru.scratty.model.Currency
import ru.scratty.model.ExchangeRate
import ru.scratty.model.User
import ru.scratty.parser.CurrencyAbbreviation

class DBHandlerMongo private constructor(): IDBHandler {

    private val users: MongoCollection<User>
    private val companies: MongoCollection<Company>
    private val currencies: MongoCollection<Currency>
    private val exchangeRates: MongoCollection<ExchangeRate>

    private class Holder {
        val instance = DBHandlerMongo()
    }

    companion object {
        val INSTANCE: DBHandlerMongo by lazy { Holder().instance }
    }

    init {
        val client = KMongo.createClient(ServerAddress(DB_HOST, DB_PORT))
//                listOf(MongoCredential.createCredential(DB_USERNAME, "admin", DB_PASSWORD.toCharArray())))
        val db = client.getDatabase(DB_DATABASE)

        users = db.getCollection<User>(COL_USER)
        companies = db.getCollection<Company>(COL_COMPANY)
        currencies = db.getCollection<Currency>(COL_CURRENCY)
        exchangeRates = db.getCollection<ExchangeRate>(COL_EXCHANGE_RATE)
    }

    override fun isRegistered(id: Int) = users.countDocuments(User::_id eq id) > 0

    override fun registrationUser(user: User) {
        users.insertOne(user)
    }

    override fun getUser(id: Int): User {
        val user = users.findOneById(id)
        return user ?: User()
    }

    override fun updateUser(user: User) {
        users.updateOneById(user._id, user)
    }

    override fun getAllCompanies() = companies.find().toMutableList()

    override fun getCompany(id: ObjectId) = companies.findOneById(id) ?: Company()

    override fun getCompany(name: String): Company {
        val company = findCompanyByName(name)

        if (company == null) {
            addCompany(Company(name = name))

            return findCompanyByName(name)!!
        }

        return company
    }

    override fun getCompaniesByIds(list: List<ObjectId>) = companies.find(Company::_id `in` list).toMutableList()

    override fun getAllCurrencies() = currencies.find().toMutableList()

    override fun getCurrency(id: ObjectId) = currencies.findOneById(id) ?: Currency()

    override fun getCurrency(currencyAbbreviation: CurrencyAbbreviation): Currency {
        val currency = findCurrency(currencyAbbreviation)

        if (currency == null) {
            addCurrency(Currency(name = currencyAbbreviation))

            return findCurrency(currencyAbbreviation)!!
        }

        return currency
    }

    override fun getCurrenciesByAbbreviation(list: List<CurrencyAbbreviation>): List<Currency> {
        var dbList = findCurrencies(list).toMutableList()

        if (list.size != dbList.size) {
            val dbListEnums = dbList.map { it.name }
            list.stream()
                    .filter { !dbListEnums.contains(it) }
                    .forEach { addCurrency(Currency(it)) }

            dbList = findCurrencies(list).toMutableList()
        }

        return dbList
    }

    override fun getCurrenciesByIds(list: List<ObjectId>) = currencies.find(Currency::_id `in` list).toMutableList()

    override fun addExchangeRate(rate: ExchangeRate): Boolean {
        val lastRateFindIterable = exchangeRates.find(ExchangeRate::currencyId eq rate.currencyId)
                .sort(ExchangeRate::time eq -1)
                .limit(1)

        if (lastRateFindIterable.count() > 0) {
            val lastRate = lastRateFindIterable.first()!!
            if (lastRate.buy == rate.buy && lastRate.sell == rate.sell) {
                return false
            }
        }
        exchangeRates.insertOne(rate)

        return true
    }

    private fun findCompanyByName(name: String) = companies.findOne(Company::name eq name)

    private fun addCompany(company: Company) {
        companies.replaceOne(Company::name eq company.name, company, ReplaceOptions.createReplaceOptions(upsert()))
    }

    private fun findCurrency(currencyAbbreviation: CurrencyAbbreviation) = currencies.findOne(Currency::name eq currencyAbbreviation)

    private fun findCurrencies(list: List<CurrencyAbbreviation>) = currencies.find(Currency::name `in` list)

    private fun addCurrency(currency: Currency) {
        currencies.replaceOne(Currency::name eq currency.name, currency, ReplaceOptions.createReplaceOptions(upsert()))
    }
}