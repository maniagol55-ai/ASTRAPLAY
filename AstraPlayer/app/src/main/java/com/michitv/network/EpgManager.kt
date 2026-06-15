package com.michitv.network

import com.michitv.model.EpgProgram
import java.text.SimpleDateFormat
import java.util.*

object EpgManager {

    private val genericPrograms = listOf(
        "Noticias" to listOf("Telediario", "Informativo Nacional", "Noticias del Mundo", "Debate en vivo", "Resumen del día"),
        "Deportes" to listOf("Fútbol en vivo", "ESPN SportsCenter", "Resumen deportivo", "Tenis ATP", "Automovilismo"),
        "Películas y Series" to listOf("Película de la tarde", "Serie del momento", "Cine de acción", "Comedia romántica", "Suspenso"),
        "Infantil" to listOf("Peppa Pig", "Paw Patrol", "Doraemon", "Tom y Jerry", "Bob Esponja"),
        "Documentales" to listOf("Planeta Tierra", "Naturaleza salvaje", "Historia del mundo", "Ciencia al día", "Exploradores"),
        "Entretenimiento" to listOf("Reality Show", "Magazine", "Concurso", "Late Night", "Talk Show"),
        "Música" to listOf("Top 40", "Videoclips", "Concierto en vivo", "Clásicos del rock", "Reggaeton Mix"),
        "Adultos" to listOf("Programa adulto"),
        "General" to listOf("Programación general", "Telefilm", "Serie", "Magazín", "Informativo"),
        "Aire Argentina" to listOf("Noticiero", "Telenovela", "Magazine", "Reality", "Cine nacional"),
        "Ecuador" to listOf("Noticiero", "Serie nacional", "Entretenimiento", "Debate", "Cine")
    )

    fun getEpgForChannel(channelName: String, group: String?): List<EpgProgram> {
        val programs = mutableListOf<EpgProgram>()
        val cal = Calendar.getInstance()
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val programList = genericPrograms.find { it.first == group }?.second
            ?: genericPrograms.last().second

        repeat(8) { i ->
            val start = cal.clone() as Calendar
            val startStr = sdf.format(start.time)
            cal.add(Calendar.HOUR_OF_DAY, 1)
            if (i == 0) cal.add(Calendar.MINUTE, 30)
            val endStr = sdf.format(cal.time)
            val title = programList[i % programList.size]
            programs.add(EpgProgram(channelName, title, startStr, endStr))
        }
        return programs
    }
}
