package com.michitv.model

data class Channel(
    val name: String,
    val url: String,
    val logo: String? = null,
    val group: String? = null,
    var isFavorite: Boolean = false
) {
    val isAdult: Boolean
        get() {
            val adultKeywords = listOf(
                "playboy", "venus", "sextreme", "adult", "xxx",
                "erotic", "private", "penthouse", "hustler", "vivid"
            )
            return adultKeywords.any { name.lowercase().contains(it) }
        }
}
