package com.besosn.app.presentation.ui.matches

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.besosn.app.R
import com.besosn.app.databinding.MatchItemBinding
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter displaying list of matches on the [MatchesFragment].
 */
class MatchesAdapter(
    private val items: MutableList<MatchModel>,
    private val onItemClick: (MatchModel) -> Unit
) : RecyclerView.Adapter<MatchesAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = MatchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(matches: List<MatchModel>) {
        items.clear()
        items.addAll(matches)
        notifyDataSetChanged()
    }

    inner class MatchViewHolder(private val binding: MatchItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(match: MatchModel) {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy - HH:mm", Locale.getDefault())
            binding.tvMatchDate.text = dateFormat.format(Date(match.date))
            binding.tvTeam1.text = match.homeTeam
            binding.tvTeam2.text = match.awayTeam
            binding.imgTeamLogo.loadMatchIcon(match.homeIconRes, match.homeIconUri)
            binding.imgTeamLogo2.loadMatchIcon(match.awayIconRes, match.awayIconUri)
            binding.tvMatchScore.text = if (match.isFinished) {
                "${match.homeScore}:${match.awayScore}"
            } else {
                "Scheduled"
            }
            binding.root.setOnClickListener { onItemClick(match) }
        }
    }
}

private fun ImageView.loadMatchIcon(@DrawableRes iconRes: Int, iconUri: String?) {
    if (!iconUri.isNullOrBlank()) {
        val parsed = runCatching { Uri.parse(iconUri) }.getOrNull()
        if (parsed != null) {
            try {
                setImageURI(parsed)
                if (drawable != null) {
                    return
                }
            } catch (_: SecurityException) {
                // Ignore and fall back to resource icon
            } catch (_: FileNotFoundException) {
                // Ignore and fall back to resource icon
            } catch (_: IllegalArgumentException) {
                // Ignore and fall back to resource icon
            }
        }
    }

    if (iconRes != 0) {
        setImageResource(iconRes)
    } else {
        setImageResource(R.drawable.jkljfsjfls)
    }
}
