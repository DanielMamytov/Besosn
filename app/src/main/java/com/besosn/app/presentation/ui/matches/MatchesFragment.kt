package com.besosn.app.presentation.ui.matches

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.besosn.app.R
import com.besosn.app.databinding.FragmentMatchesBinding
import java.util.Calendar

class MatchesFragment : Fragment(R.layout.fragment_matches) {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MatchesAdapter
    private val matches = mutableListOf<MatchModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchesBinding.bind(view)

        adapter = MatchesAdapter(mutableListOf()) {
            findNavController().navigate(R.id.action_matchesFragment_to_matchDetailFragment)
        }
        binding.rvMatches.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMatches.adapter = adapter

        loadMatches()
        setupFilters()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_matchesFragment_to_matchEditFragment)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun loadMatches() {
        matches.clear()
        val now = Calendar.getInstance()
        val past = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        val future = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 3) }

        matches.add(
            MatchModel(
                id = 1,
                homeTeam = "Barcelona",
                awayTeam = "Real Madrid",
                homeIconRes = R.drawable.vdgdsgfds,
                awayIconRes = R.drawable.jkljfsjfls,
                date = past.timeInMillis,
                homeScore = 1,
                awayScore = 2
            )
        )
        matches.add(
            MatchModel(
                id = 2,
                homeTeam = "Arsenal",
                awayTeam = "Chelsea",
                homeIconRes = R.drawable.vdgdsgfds,
                awayIconRes = R.drawable.jkljfsjfls,
                date = future.timeInMillis
            )
        )
        applyFilter(MatchFilter.ALL)
    }

    private fun setupFilters() {
        binding.rgTabs.setOnCheckedChangeListener { _, checkedId ->
            val filter = when (checkedId) {
                R.id.tabScheduled -> MatchFilter.SCHEDULED
                R.id.tabFinished -> MatchFilter.FINISHED
                else -> MatchFilter.ALL
            }
            applyFilter(filter)
        }
    }

    private fun applyFilter(filter: MatchFilter) {
        val filtered = when (filter) {
            MatchFilter.ALL -> matches
            MatchFilter.SCHEDULED -> matches.filter { !it.isFinished }
            MatchFilter.FINISHED -> matches.filter { it.isFinished }
        }.sortedBy { it.date }

        adapter.submitList(filtered)
        val empty = filtered.isEmpty()
        binding.tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
        binding.rvMatches.visibility = if (empty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private enum class MatchFilter { ALL, SCHEDULED, FINISHED }
