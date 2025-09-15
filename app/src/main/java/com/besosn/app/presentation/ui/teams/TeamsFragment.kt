package com.besosn.app.presentation.ui.teams

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import androidx.room.Room

import com.besosn.app.R
import com.besosn.app.data.local.db.AppDatabase
import com.besosn.app.databinding.FragmentTeamsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Screen displaying list of teams. Allows navigating to team details
 * and adding new teams.
 */
class TeamsFragment : Fragment(R.layout.fragment_teams) {

    private var _binding: FragmentTeamsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TeamsAdapter
    private val teams = mutableListOf<TeamModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTeamsBinding.bind(view)

        adapter = TeamsAdapter(teams) { team ->
            val bundle = Bundle().apply { putSerializable("team", team) }

            findNavController().navigate(R.id.action_teamsFragment_to_teamsDetailFragment, bundle)
        }
        binding.rvTeams.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTeams.adapter = adapter

        loadTeams()


        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_teamsFragment_to_teamsEditFragment)
        }

        // Listen for newly added team from edit screen and reload
        setFragmentResultListener("add_team_result") { _, _ ->
            loadTeams()

        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun defaultTeams(): List<TeamModel> {
        val team1 = TeamModel(
            name = "YoungTeam",
            city = "New York",
            foundedYear = 2024,
            notes = "Default team",
            players = listOf(PlayerModel("Alex Finch", "FW", 10)),
            iconRes = R.drawable.ic_users,
            iconUri = null,
            isDefault = true
        )
        val team2 = TeamModel(
            name = "TalentTeam",
            city = "Chicago",
            foundedYear = 2021,
            notes = "Default team",
            players = listOf(PlayerModel("Yacob Sunny", "GK", 1)),
            iconRes = R.drawable.ic_users,
            iconUri = null,
            isDefault = true
        )
        return listOf(team1, team2)
    }

    private fun loadTeams() {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "app_db")
                .fallbackToDestructiveMigration()
                .build()
            val teamDao = db.teamDao()
            val playerDao = db.playerDao()

            val existing = withContext(Dispatchers.IO) { teamDao.getTeams() }
            if (existing.isEmpty()) {
                withContext(Dispatchers.IO) {
                    defaultTeams().forEach { model ->
                        val teamId = teamDao.insertTeam(model.toEntity()).toInt()
                        playerDao.insertPlayers(model.players.map { it.toEntity(teamId) })
                    }
                }
            }

            val teamEntities = withContext(Dispatchers.IO) { teamDao.getTeams() }
            val playerEntities = withContext(Dispatchers.IO) { playerDao.getPlayers() }
            val playersByTeam = playerEntities.groupBy { it.teamId }
            val models = teamEntities.map { entity ->
                entity.toModel(playersByTeam[entity.id].orEmpty())
            }

            teams.clear()
            teams.addAll(models)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
