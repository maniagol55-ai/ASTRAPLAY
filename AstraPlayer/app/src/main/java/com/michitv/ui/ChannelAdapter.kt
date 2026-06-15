package com.michitv.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michitv.R
import com.michitv.databinding.ItemChannelBinding
import com.michitv.model.Channel

class ChannelAdapter(
    private val onChannelClick: (Channel) -> Unit,
    private val onFavoriteClick: (Channel) -> Unit,
    private val isFavorite: (Channel) -> Boolean
) : ListAdapter<Channel, ChannelAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemChannelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(channel: Channel) {
            binding.tvChannelName.text = channel.name
            binding.tvGroup.text = channel.group ?: ""

            // Ícono adulto
            if (channel.isAdult) {
                binding.tvAdultBadge.visibility = android.view.View.VISIBLE
            } else {
                binding.tvAdultBadge.visibility = android.view.View.GONE
            }

            // Favorito
            val favIcon = if (isFavorite(channel)) R.drawable.ic_star_filled else R.drawable.ic_star_outline
            binding.btnFavorite.setImageResource(favIcon)
            binding.btnFavorite.setOnClickListener { onFavoriteClick(channel) }

            binding.root.setOnClickListener { onChannelClick(channel) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Channel>() {
        override fun areItemsTheSame(a: Channel, b: Channel) = a.url == b.url
        override fun areContentsTheSame(a: Channel, b: Channel) = a == b
    }
}
