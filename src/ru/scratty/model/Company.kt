package ru.scratty.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Company(@BsonId val _id: ObjectId = ObjectId(),
                   val name: String = "") {

    constructor(name: String): this(ObjectId(), name)
}