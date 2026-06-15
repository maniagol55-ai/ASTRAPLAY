package com.michitv.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.michitv.databinding.ActivityPlayerBinding
import com.michitv.network.EpgManager

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null

    companion object {
        const val EXTRA_STREAM_URL = "stream_url"
        const val EXTRA_CHANNEL_NAME = "channel_name"
        const val EXTRA_CHANNEL_GROUP = "channel_group"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra(EXTRA_STREAM_URL) ?: run { finish(); return }
        val name = intent.getStringExtra(EXTRA_CHANNEL_NAME) ?: "Canal"
        val group = intent.getStringExtra(EXTRA_CHANNEL_GROUP)

        binding.tvChannelName.text = name
        binding.btnBack.setOnClickListener { finish() }

        // EPG para este canal
        val programs = EpgManager.getEpgForChannel(name, group)
        if (programs.isNotEmpty()) {
            val now = programs.first()
            binding.tvNowPlaying.text = "▶ Ahora: ${now.title} (${now.startTime}-${now.endTime})"
            if (programs.size > 1) {
                val next = programs[1]
                binding.tvNextProgram.text = "⏭ Luego: ${next.title} (${next.startTime})"
            }
        }

        initPlayer(url)
    }

    private fun initPlayer(url: String) {
        binding.progressPlayer.visibility = View.VISIBLE
        player = ExoPlayer.Builder(this).build().also { exo ->
            binding.playerView.player = exo
            exo.setMediaItem(MediaItem.fromUri(url))
            exo.prepare()
            exo.playWhenReady = true

            exo.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_READY -> binding.progressPlayer.visibility = View.GONE
                        Player.STATE_BUFFERING -> binding.progressPlayer.visibility = View.VISIBLE
                        Player.STATE_ENDED -> Toast.makeText(this@PlayerActivity, "Stream finalizado", Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
                override fun onPlayerError(error: PlaybackException) {
                    binding.progressPlayer.visibility = View.GONE
                    Toast.makeText(this@PlayerActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    override fun onPause() { super.onPause(); player?.pause() }
    override fun onResume() { super.onResume(); player?.play() }
    override fun onDestroy() { super.onDestroy(); player?.release(); player = null }
}
