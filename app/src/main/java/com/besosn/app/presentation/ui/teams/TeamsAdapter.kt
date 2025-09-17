package com.besosn.app.presentation.ui.teams

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.besosn.app.databinding.TeamsItemBinding
import java.util.Locale

/**
 * Adapter showing list of teams in [TeamsFragment].
 */
class TeamsAdapter(
    private val items: MutableList<TeamModel>,
    private val onItemClick: (TeamModel) -> Unit
) : RecyclerView.Adapter<TeamsAdapter.TeamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = TeamsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addTeam(team: TeamModel) {
        items.add(team)
        notifyItemInserted(items.size - 1)
    }

    inner class TeamViewHolder(private val binding: TeamsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(team: TeamModel) {
            binding.tvTeamTitle.text = team.name.uppercase(Locale.getDefault())
            binding.tvTeamCity.text = "${team.city} \u2022"
            binding.tvTeamPlayers.text = " ${team.playersCount} players"
            binding.imgTeamLogo.loadTeamImage(team)
            binding.root.setOnClickListener { onItemClick(team) }
        }
    }
}
