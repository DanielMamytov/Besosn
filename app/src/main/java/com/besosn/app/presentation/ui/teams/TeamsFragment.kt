package com.besosn.app.presentation.ui.teams

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.besosn.app.R
import com.besosn.app.databinding.FragmentTeamsBinding

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
            val bundle = bundleOf("team" to team)
            findNavController().navigate(R.id.action_teamsFragment_to_teamsDetailFragment, bundle)
        }
        binding.rvTeams.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTeams.adapter = adapter

        // load default teams
        teams.addAll(defaultTeams())
        adapter.notifyDataSetChanged()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_teamsFragment_to_teamsEditFragment)
        }

        // Listen for newly added team from edit screen
        setFragmentResultListener("add_team_result") { _, bundle ->
            val team = bundle.getSerializable("team") as? TeamModel ?: return@setFragmentResultListener
            adapter.addTeam(team)
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
            isDefault = true
        )
        val team2 = TeamModel(
            name = "TalentTeam",
            city = "Chicago",
            foundedYear = 2021,
            notes = "Default team",
            players = listOf(PlayerModel("Yacob Sunny", "GK", 1)),
            iconRes = R.drawable.ic_users,
            isDefault = true
        )
        return listOf(team1, team2)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
