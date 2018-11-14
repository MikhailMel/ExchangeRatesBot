package ru.scratty.util

import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.HttpClientBuilder

object HttpUtil {

    fun post(url: String, parameters: List<NameValuePair>): String {
        val httpClient = HttpClientBuilder.create().build()
        val post = HttpPost(url)
        post.entity = UrlEncodedFormEntity(parameters)

        return httpClient.execute(post, BasicResponseHandler()) as String
    }

    fun get(url: String): String {
        val httpClient = HttpClientBuilder.create().build()
        val get = HttpGet(url)

        return httpClient.execute(get, BasicResponseHandler()) as String
    }

}