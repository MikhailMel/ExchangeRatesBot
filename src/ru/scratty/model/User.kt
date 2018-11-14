package ru.scratty.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.telegram.telegrambots.meta.api.objects.User

data class User(@BsonId val _id: Int = 0,
                val fName: String = "",
                val lName: String = "",
                val username: String = "",
                val activeCurrencies: ArrayList<ObjectId> = ArrayList(),
                val activeCompanies: ArrayList<ObjectId> = ArrayList()) {

    constructor(user: User): this(user.id, user.firstName, user.lastName, user.userName)
}