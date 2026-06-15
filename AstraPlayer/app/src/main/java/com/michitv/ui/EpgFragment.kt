package com.michitv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.michitv.databinding.FragmentEpgBinding
import com.michitv.network.ChannelRepository
import com.michitv.network.EpgManager
import com.michitv.model.EpgProgram
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EpgFragment : Fragment() {

    private var _binding: FragmentEpgBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EpgAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEpgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = EpgAdapter()
        binding.rvEpg.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEpg.adapter = adapter
        loadEpg()
    }

    private fun loadEpg() {
        binding.progressEpg.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) { ChannelRepository.fetchChannels() }
            binding.progressEpg.visibility = View.GONE
            result.onSuccess { channels ->
                val epgList = mutableListOf<EpgProgram>()
                channels.take(30).forEach { channel ->
                    epgList.addAll(EpgManager.getEpgForChannel(channel.name, channel.group))
                }
                adapter.submitList(epgList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
