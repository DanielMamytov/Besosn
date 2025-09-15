package com.besosn.app.presentation.ui.teams

import android.view.LayoutInflater
import android.view.ViewGroup
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.besosn.app.databinding.ItemPlayerBinding

/**
 * Adapter displaying list of players inside team detail screen.
 */
class PlayersAdapter(private val items: List<PlayerModel>) :
    RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class PlayerViewHolder(private val binding: ItemPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(player: PlayerModel) {
            binding.tvPlayerName.text = player.fullName
            binding.tvPlayerNumber.text = player.number.toString()
            binding.tvPlayerPosition.text = player.position
            if (player.photoUri != null) {
                binding.ivPlayerPhoto.setImageURI(Uri.parse(player.photoUri))
            } else {
                binding.ivPlayerPhoto.setImageResource(com.besosn.app.R.drawable.ic_users)
            }
        }
    }
}

