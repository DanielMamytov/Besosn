package com.besosn.app.presentation.ui.matches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.besosn.app.databinding.MatchItemBinding
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
            binding.imgTeamLogo.setImageResource(match.homeIconRes)
            binding.imgTeamLogo2.setImageResource(match.awayIconRes)
            binding.tvMatchScore.text = if (match.isFinished) {
                "${match.homeScore}:${match.awayScore}"
            } else {
                "Scheduled"
            }
            binding.root.setOnClickListener { onItemClick(match) }
        }
    }
}
