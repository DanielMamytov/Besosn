package com.besosn.app.presentation.ui.matches

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.besosn.app.R
import com.besosn.app.data.local.db.AppDatabase
import com.besosn.app.data.model.MatchEntity
import com.besosn.app.databinding.FragmentMatchesBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MatchesFragment : Fragment(R.layout.fragment_matches) {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MatchesAdapter
    private val matches = mutableListOf<MatchModel>()
    private var currentFilter: MatchFilter = MatchFilter.ALL
    private var savedMatches: List<MatchModel> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchesBinding.bind(view)

        adapter = MatchesAdapter(mutableListOf()) { match ->
            val args = bundleOf(MatchDetailFragment.ARG_MATCH to match)
            findNavController().navigate(R.id.action_matchesFragment_to_matchDetailFragment, args)
        }
        binding.rvMatches.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMatches.adapter = adapter

        setupFilters()
        loadMatches()
        observeSavedMatches()

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Boolean>(MatchEditFragment.RESULT_KEY_MATCHES_UPDATED)
            ?.observe(viewLifecycleOwner, Observer { shouldReload ->
                if (shouldReload == true) {
                    loadMatches()
                    findNavController().currentBackStackEntry?.savedStateHandle
                        ?.remove<Boolean>(MatchEditFragment.RESULT_KEY_MATCHES_UPDATED)
                }
            })

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
        matches.addAll(MatchesLocalDataSource.loadMatches(requireContext()))
        matches.addAll(savedMatches)
        applyFilter(currentFilter)
    }


    private fun setupFilters() {
        binding.rgTabs.setOnCheckedChangeListener { _, checkedId ->
            val filter = when (checkedId) {
                R.id.tabScheduled -> MatchFilter.SCHEDULED
                R.id.tabFinished -> MatchFilter.FINISHED
                else -> MatchFilter.ALL
            }
            currentFilter = filter
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

    private fun observeSavedMatches() {
        viewLifecycleOwner.lifecycleScope.launch {
            val context = requireContext().applicationContext
            val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
                .fallbackToDestructiveMigration()
                .build()
            try {
                db.matchDao().getMatches().collect { entities ->
                    savedMatches = entities.map { it.toModel() }
                    loadMatches()
                }
            } finally {
                db.close()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

private enum class MatchFilter { ALL, SCHEDULED, FINISHED }

private fun MatchEntity.toModel(): MatchModel = MatchModel(
    id = DB_MATCH_ID_OFFSET + id,
    homeTeam = homeTeamName,
    awayTeam = awayTeamName,
    homeIconRes = MatchesLocalDataSource.resolveTeamIcon(homeTeamName),
    awayIconRes = MatchesLocalDataSource.resolveTeamIcon(awayTeamName),
    homeIconUri = homePhotoUri,
    awayIconUri = awayPhotoUri,
    date = date,
    homeScore = homeGoals,
    awayScore = awayGoals,
    city = city,
    notes = notes,
)
