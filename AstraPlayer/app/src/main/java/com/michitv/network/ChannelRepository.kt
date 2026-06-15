package com.michitv.network

import com.michitv.model.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.concurrent.TimeUnit

object ChannelRepository {

    private const val CHANNELS_URL = "http://192.241.177.54/canales_michi_tv.json"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun fetchChannels(): Result<List<Channel>> {
        return try {
            val request = Request.Builder().url(CHANNELS_URL).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return Result.failure(Exception("Error HTTP ${response.code}"))
            val body = response.body?.string() ?: return Result.failure(Exception("Respuesta vacía"))
            val arr = JSONArray(body)
            val channels = mutableListOf<Channel>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val name = obj.optString("name", "Canal ${i+1}")
                val url = obj.optString("url", "")
                val logo = obj.optString("logo", "")
                val group = obj.optString("group", guessGroup(name))
                if (url.isNotEmpty()) {
                    channels.add(Channel(name = name, url = url, logo = logo.ifEmpty { null }, group = group))
                }
            }
            Result.success(channels.sortedBy { it.name.lowercase() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun guessGroup(name: String): String {
        val n = name.lowercase()
        return when {
            listOf("espn", "fox sports", "tyc", "dsports", "deportv", "golf", "tnt sports", "am sports").any { n.contains(it) } -> "Deportes"
            listOf("cartoon", "disney", "nick", "baby tv", "pakapaka", "kidoo", "discovery kids", "tooncast").any { n.contains(it) } -> "Infantil"
            listOf("playboy", "venus", "sextreme", "adult swim", "private", "penthouse").any { n.contains(it) } -> "Adultos"
            listOf("hbo", "cinemax", "star channel", "tnt", "fx", "space", "tcm", "studio", "cinecanal", "axn", "sony", "universal", "warner", "amc", "usa network").any { n.contains(it) } -> "Películas y Series"
            listOf("discovery", "history", "nat geo", "science", "turbo", "world").any { n.contains(it) } -> "Documentales"
            listOf("tn", "c5n", "crónica", "cronica", "cnn", "rt ", "la nacion", "infobae").any { n.contains(it) } -> "Noticias"
            listOf("mtv", "htv", "quiero musica", "vh1", "music").any { n.contains(it) } -> "Música"
            listOf("comedy", "e!", "lifetime", "film", "galicia", "gourmet", "hgtv", "tlc").any { n.contains(it) } -> "Entretenimiento"
            listOf("tv publica", "telefe", "el trece", "canal 4", "el nueve", "canal 13", "america tv", "trece").any { n.contains(it) } -> "Aire Argentina"
            listOf("ecuador", "ecuavisa", "teleamazonas", "gamavision", "tc television", "saeta", "rts", "rec tv").any { n.contains(it) } -> "Ecuador"
            else -> "General"
        }
    }
}
