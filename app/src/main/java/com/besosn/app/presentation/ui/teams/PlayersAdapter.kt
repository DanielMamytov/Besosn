package com.besosn.app.presentation.ui.teams

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import java.io.FileNotFoundException
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
            binding.tvNumber.text = player.number.toString()
            binding.tvPosition.text = player.position
            val photoUri = player.photoUri
            if (!photoUri.isNullOrBlank()) {
                val parsed = runCatching { Uri.parse(photoUri) }.getOrNull()
                if (parsed != null) {
                    try {
                        binding.imgPlayer.setImageURI(parsed)
                        if (binding.imgPlayer.drawable != null) {
                            return
                        }
                    } catch (_: SecurityException) {
                        // The app no longer has permission to read the selected photo.
                    } catch (_: FileNotFoundException) {
                        // Image removed from the device, so we show the fallback icon instead.
                    } catch (_: IllegalArgumentException) {
                        // Invalid URI format, fall back to the default icon below.
                    }
                }
            }

            binding.imgPlayer.setImageResource(com.besosn.app.R.drawable.ic_users)
        }
    }
}

