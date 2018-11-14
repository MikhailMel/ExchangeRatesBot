package ru.scratty.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class ExchangeRate(@BsonId val _id: ObjectId = ObjectId(),
                        val companyId: ObjectId? = null,
                        val time: Long = 0,
                        val currencyId: ObjectId? = null,
                        val buy: Double = 0.0,
                        val sell: Double = 0.0) {

    constructor(companyId: ObjectId,
                time: Long,
                currencyId: ObjectId,
                buy: Double,
                sell: Double): this(ObjectId(), companyId, time, currencyId, buy, sell)
}