package com.michitv.util

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {

    private const val PREFS_NAME = "michitv_prefs"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_PARENTAL_PIN = "parental_pin"
    private const val KEY_PARENTAL_ENABLED = "parental_enabled"
    private const val KEY_LOGGED_USER = "logged_user"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // --- Favoritos ---
    fun getFavorites(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()

    fun toggleFavorite(context: Context, channelUrl: String): Boolean {
        val favs = getFavorites(context).toMutableSet()
        val added = if (favs.contains(channelUrl)) {
            favs.remove(channelUrl); false
        } else {
            favs.add(channelUrl); true
        }
        prefs(context).edit().putStringSet(KEY_FAVORITES, favs).apply()
        return added
    }

    fun isFavorite(context: Context, channelUrl: String): Boolean =
        getFavorites(context).contains(channelUrl)

    // --- Control parental ---
    fun getParentalPin(context: Context): String? =
        prefs(context).getString(KEY_PARENTAL_PIN, null)

    fun setParentalPin(context: Context, pin: String) =
        prefs(context).edit().putString(KEY_PARENTAL_PIN, pin).apply()

    fun isParentalEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_PARENTAL_ENABLED, false)

    fun setParentalEnabled(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_PARENTAL_ENABLED, enabled).apply()

    // --- Sesión ---
    fun saveLoggedUser(context: Context, username: String) =
        prefs(context).edit().putString(KEY_LOGGED_USER, username).apply()

    fun getLoggedUser(context: Context): String? =
        prefs(context).getString(KEY_LOGGED_USER, null)

    fun clearSession(context: Context) =
        prefs(context).edit().remove(KEY_LOGGED_USER).apply()
}
