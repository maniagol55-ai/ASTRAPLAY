package com.michitv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.michitv.databinding.FragmentFavoritesBinding
import com.michitv.model.Channel
import com.michitv.network.ChannelRepository
import com.michitv.util.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ChannelAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChannelAdapter(
            onChannelClick = { channel ->
                (activity as? ChannelClickListener)?.onChannelClick(channel)
            },
            onFavoriteClick = { channel ->
                PreferencesManager.toggleFavorite(requireContext(), channel.url)
                Toast.makeText(requireContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                loadFavorites()
            },
            isFavorite = { true }
        )
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter
        loadFavorites()
    }

    private fun loadFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            val favUrls = PreferencesManager.getFavorites(requireContext())
            val result = withContext(Dispatchers.IO) { ChannelRepository.fetchChannels() }
            result.onSuccess { channels ->
                val favChannels = channels.filter { favUrls.contains(it.url) }
                adapter.submitList(favChannels)
                binding.tvEmpty.visibility = if (favChannels.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
