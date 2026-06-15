package com.michitv.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.michitv.R
import com.michitv.databinding.ActivityMainBinding
import com.michitv.model.Channel
import com.michitv.util.PreferencesManager

class MainActivity : AppCompatActivity(), ChannelClickListener {

    private lateinit var binding: ActivityMainBinding
    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        username = intent.getStringExtra("username") ?: ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "🛰 MichiTV"
        supportActionBar?.subtitle = "Hola, $username"

        // Default fragment
        loadFragment(ChannelsFragment())

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_channels -> { loadFragment(ChannelsFragment()); true }
                R.id.nav_favorites -> { loadFragment(FavoritesFragment()); true }
                R.id.nav_epg -> { loadFragment(EpgFragment()); true }
                R.id.nav_settings -> { loadFragment(SettingsFragment()); true }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onChannelClick(channel: Channel) {
        if (channel.isAdult && PreferencesManager.isParentalEnabled(this)) {
            val pin = PreferencesManager.getParentalPin(this)
            if (pin != null) {
                showPinDialog { enteredPin ->
                    if (enteredPin == pin) {
                        openPlayer(channel)
                    } else {
                        android.widget.Toast.makeText(this, "PIN incorrecto", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                return
            }
        }
        openPlayer(channel)
    }

    private fun showPinDialog(onCorrect: (String) -> Unit) {
        val input = android.widget.EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
            hint = "Ingresa el PIN"
        }
        AlertDialog.Builder(this)
            .setTitle("🔒 Control Parental")
            .setMessage("Este canal requiere PIN de adultos")
            .setView(input)
            .setPositiveButton("Confirmar") { _, _ -> onCorrect(input.text.toString()) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun openPlayer(channel: Channel) {
        startActivity(Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_STREAM_URL, channel.url)
            putExtra(PlayerActivity.EXTRA_CHANNEL_NAME, channel.name)
            putExtra(PlayerActivity.EXTRA_CHANNEL_GROUP, channel.group)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas salir de MichiTV?")
                .setPositiveButton("Sí") { _, _ ->
                    PreferencesManager.clearSession(this)
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
                .setNegativeButton("No", null)
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

interface ChannelClickListener {
    fun onChannelClick(channel: Channel)
}
