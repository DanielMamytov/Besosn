package com.besosn.app.presentation.ui.teams

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.besosn.app.databinding.PlayerItemBinding

/**
 * Adapter displaying list of players inside team detail screen.
 */
class PlayersAdapter(private val items: List<PlayerModel>) :
    RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = PlayerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class PlayerViewHolder(private val binding: PlayerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(player: PlayerModel) {
            binding.tvPlayerName.text = player.fullName
            binding.tvPlayerNumber.text = player.number.toString()
            binding.tvPlayerPosition.text = player.position
        }
    }
}

