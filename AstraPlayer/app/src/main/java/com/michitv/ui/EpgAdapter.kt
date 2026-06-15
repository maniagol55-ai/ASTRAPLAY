package com.michitv.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michitv.databinding.ItemEpgBinding
import com.michitv.model.EpgProgram

class EpgAdapter : ListAdapter<EpgProgram, EpgAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEpgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class ViewHolder(private val binding: ItemEpgBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(program: EpgProgram) {
            binding.tvChannelEpg.text = program.channelName
            binding.tvProgramTitle.text = program.title
            binding.tvProgramTime.text = "${program.startTime} - ${program.endTime}"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<EpgProgram>() {
        override fun areItemsTheSame(a: EpgProgram, b: EpgProgram) =
            a.channelName == b.channelName && a.startTime == b.startTime
        override fun areContentsTheSame(a: EpgProgram, b: EpgProgram) = a == b
    }
}
