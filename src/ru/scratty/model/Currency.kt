package ru.scratty.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import ru.scratty.parser.CurrencyAbbreviation

data class Currency(@BsonId val _id: ObjectId = ObjectId(),
                    val name: CurrencyAbbreviation = CurrencyAbbreviation.NULL) {

    constructor(name: CurrencyAbbreviation): this(ObjectId(), name)
}