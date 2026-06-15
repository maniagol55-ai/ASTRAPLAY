package com.michitv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.michitv.databinding.FragmentChannelsBinding
import com.michitv.network.ChannelRepository
import com.michitv.model.Channel
import com.michitv.util.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChannelsFragment : Fragment() {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ChannelAdapter
    private val allChannels = mutableListOf<Channel>()
    private val groups = mutableListOf<String>()
    private var selectedGroup = "Todos"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        loadChannels()
    }

    private fun setupRecyclerView() {
        adapter = ChannelAdapter(
            onChannelClick = { channel ->
                (activity as? ChannelClickListener)?.onChannelClick(channel)
            },
            onFavoriteClick = { channel ->
                val added = PreferencesManager.toggleFavorite(requireContext(), channel.url)
                val msg = if (added) "⭐ Añadido a favoritos" else "Eliminado de favoritos"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            },
            isFavorite = { channel -> PreferencesManager.isFavorite(requireContext(), channel.url) }
        )
        binding.rvChannels.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChannels.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) { filterChannels(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.chipGroupCategories.setOnCheckedStateChangeListener { group, _ ->
            val chip = group.findViewById<com.google.android.material.chip.Chip>(group.checkedChipId)
            selectedGroup = chip?.text?.toString() ?: "Todos"
            filterChannels(binding.etSearch.text.toString())
        }
    }

    private fun loadChannels() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) { ChannelRepository.fetchChannels() }
            binding.progressBar.visibility = View.GONE

            result.onSuccess { channels ->
                allChannels.clear()
                allChannels.addAll(channels)
                setupChips(channels)
                filterChannels("")
            }.onFailure {
                binding.tvEmpty.text = "Error al cargar canales: ${it.message}"
                binding.tvEmpty.visibility = View.VISIBLE
            }
        }
    }

    private fun setupChips(channels: List<Channel>) {
        binding.chipGroupCategories.removeAllViews()
        groups.clear()
        groups.add("Todos")
        groups.addAll(channels.mapNotNull { it.group }.distinct().sorted())

        groups.forEach { group ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = group
                isCheckable = true
                isChecked = group == "Todos"
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun filterChannels(query: String) {
        var filtered = allChannels.toList()
        if (selectedGroup != "Todos") filtered = filtered.filter { it.group == selectedGroup }
        if (query.isNotEmpty()) filtered = filtered.filter { it.name.contains(query, ignoreCase = true) }
        adapter.submitList(filtered)
        binding.tvChannelCount.text = "${filtered.size} canales"
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
