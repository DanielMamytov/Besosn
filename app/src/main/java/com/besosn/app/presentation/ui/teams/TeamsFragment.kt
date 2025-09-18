package com.besosn.app.presentation.ui.teams

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope

import com.besosn.app.R
import com.besosn.app.databinding.FragmentTeamsBinding
import kotlinx.coroutines.launch

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

        
        setFragmentResultListener("add_team_result") { _, _ ->
            loadTeams()

        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun loadTeams() {
        viewLifecycleOwner.lifecycleScope.launch {
            val models = TeamsLocalDataSource.loadTeams(requireContext())
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
