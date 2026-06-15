package com.michitv.network

import com.michitv.model.User
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.util.concurrent.TimeUnit

object AuthManager {

    private const val USERS_URL = "http://192.241.177.54/amzgenxml.php?key=AH397ZG2051700"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun login(username: String, password: String): LoginResult {
        return try {
            val request = Request.Builder().url(USERS_URL).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return LoginResult.NetworkError

            val xml = response.body?.string() ?: return LoginResult.NetworkError
            val users = parseUsers(xml)

            val user = users.find { 
                it.name.trim().equals(username.trim(), ignoreCase = true) &&
                it.password.trim() == password.trim()
            }

            when {
                user == null -> LoginResult.InvalidCredentials
                !user.enabled -> LoginResult.AccountDisabled
                else -> LoginResult.Success(user)
            }
        } catch (e: Exception) {
            LoginResult.NetworkError
        }
    }

    private fun parseUsers(xml: String): List<User> {
        val users = mutableListOf<User>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(xml.reader())

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "user") {
                    val name = parser.getAttributeValue(null, "name") ?: ""
                    val password = parser.getAttributeValue(null, "password") ?: ""
                    val enabled = parser.getAttributeValue(null, "enabled") == "true"
                    val maxConn = parser.getAttributeValue(null, "max-connections")?.toIntOrNull() ?: 1
                    if (name.isNotEmpty()) {
                        users.add(User(name, password, enabled, maxConn))
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return users
    }
}

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    object InvalidCredentials : LoginResult()
    object AccountDisabled : LoginResult()
    object NetworkError : LoginResult()
}
