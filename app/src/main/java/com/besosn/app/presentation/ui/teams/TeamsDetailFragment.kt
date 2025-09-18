package com.besosn.app.presentation.ui.teams

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.besosn.app.R
import com.besosn.app.data.local.db.AppDatabase
import com.besosn.app.databinding.FragmentTeamsDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeamsDetailFragment : Fragment(R.layout.fragment_teams_detail) {

    private var _binding: FragmentTeamsDetailBinding? = null
    private val binding get() = _binding!!

    private var team: TeamModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTeamsDetailBinding.bind(view)

        team = requireArguments().getSerializable("team") as? TeamModel

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnEdit.setOnClickListener {
            val currentTeam = team ?: return@setOnClickListener
            if (currentTeam.isDefault) {
                showImmutableTeamTooltip()
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch {
                if (shouldBlockTeamModification()) {
                    showMinimumTeamsToast()
                } else {
                    val bundle = Bundle().apply { putSerializable("team", currentTeam) }
                    findNavController().navigate(
                        R.id.action_teamsDetailFragment_to_teamsEditFragment,
                        bundle
                    )
                }
            }
        }
        binding.btnDelete.setOnClickListener {
            val currentTeam = team ?: return@setOnClickListener
            viewLifecycleOwner.lifecycleScope.launch {
                if (shouldBlockTeamModification()) {
                    showMinimumTeamsToast()
                } else {
                    confirmDelete(currentTeam)
                }
            }
        }

        setFragmentResultListener("team_updated") { _, bundle ->
            val updated = bundle.getSerializable("team") as? TeamModel
            updated?.let {
                team = it
                bindTeam(it)
            }
        }

        team?.let { bindTeam(it) }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun bindTeam(team: TeamModel) {
        binding.imgTeamIcon.loadTeamImage(team)

        binding.tvTeamName.text = team.name
        binding.tvCityValue.text = team.city
        binding.tvFoundedValue.text = team.foundedYear.toString()
        binding.tvPlayersValue.text = team.playersCount.toString()
        binding.tvNotes.text = team.notes

        binding.rvPlayers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPlayers.adapter = PlayersAdapter(team.players)

        binding.btnEdit.isEnabled = true
        binding.btnEdit.alpha = if (team.isDefault) 0.6f else 1f
        binding.btnDelete.isEnabled = !team.isDefault
        binding.btnDelete.visibility = if (team.isDefault) View.GONE else View.VISIBLE
    }

    private suspend fun shouldBlockTeamModification(): Boolean {
        val teamsCount = TeamsLocalDataSource.countTeams(requireContext())
        return teamsCount <= MIN_TEAMS_COUNT
    }

    private fun showMinimumTeamsToast() {
        Toast.makeText(
            requireContext(),
            R.string.teams_minimum_edit_delete_warning,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showImmutableTeamTooltip() {
        Toast.makeText(
            requireContext(),
            R.string.teams_default_edit_tooltip,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun confirmDelete(team: TeamModel) {
        AlertDialog.Builder(requireContext())
            .setMessage("Please confirm delete action before continuing")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Confirm") { _, _ -> deleteTeam(team) }
            .show()
    }

    private fun deleteTeam(team: TeamModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "app_db")
                .fallbackToDestructiveMigration()
                .build()
            withContext(Dispatchers.IO) {
                db.playerDao().deletePlayersByTeam(team.id)
                db.teamDao().deleteTeam(team.toEntity())
            }
            setFragmentResult("add_team_result", Bundle())
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        private const val MIN_TEAMS_COUNT = 2
    }
}

