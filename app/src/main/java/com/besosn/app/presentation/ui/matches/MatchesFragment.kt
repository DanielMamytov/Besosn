package com.besosn.app.presentation.ui.matches

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
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
import java.util.Calendar
import java.util.Locale

class MatchesFragment : Fragment(R.layout.fragment_matches) {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MatchesAdapter
    private val matches = mutableListOf<MatchModel>()
    private var currentFilter: MatchFilter = MatchFilter.ALL
    private val defaultMatches: List<MatchModel> by lazy { getDefaultMatches() }
    private var savedMatches: List<MatchModel> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchesBinding.bind(view)

        adapter = MatchesAdapter(mutableListOf()) {
            findNavController().navigate(R.id.action_matchesFragment_to_matchDetailFragment)
        }
        binding.rvMatches.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMatches.adapter = adapter

        setupFilters()
        loadMatches()
        observeSavedMatches()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_matchesFragment_to_matchEditFragment)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
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

    private fun loadMatches() {
        matches.clear()
        matches.addAll(defaultMatches)
        matches.addAll(savedMatches)
        applyFilter(currentFilter)
    }

    private fun getDefaultMatches(): List<MatchModel> {
        val now = Calendar.getInstance()
        val past = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        val future = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 3) }

        return listOf(
            MatchModel(
                id = 1,
                homeTeam = "Barcelona",
                awayTeam = "Real Madrid",
                homeIconRes = R.drawable.vdgdsgfds,
                awayIconRes = R.drawable.jkljfsjfls,
                date = past.timeInMillis,
                homeScore = 1,
                awayScore = 2,
            ),
            MatchModel(
                id = 2,
                homeTeam = "Arsenal",
                awayTeam = "Chelsea",
                homeIconRes = R.drawable.vdgdsgfds,
                awayIconRes = R.drawable.jkljfsjfls,
                date = future.timeInMillis,
            ),
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun MatchEntity.toModel(): MatchModel {
        val homeUri = homePhotoUri?.takeIf { it.isNotBlank() }
        val awayUri = awayPhotoUri?.takeIf { it.isNotBlank() }
        return MatchModel(
            id = SAVED_MATCH_ID_OFFSET + id,
            homeTeam = homeTeamName,
            awayTeam = awayTeamName,
            homeIconRes = if (homeUri == null) resolveTeamIcon(homeTeamName) else 0,
            awayIconRes = if (awayUri == null) resolveTeamIcon(awayTeamName) else 0,
            homeIconUri = homeUri,
            awayIconUri = awayUri,
            date = date,
            homeScore = homeGoals,
            awayScore = awayGoals,
            city = city,
            notes = notes,
        )
    }

    @DrawableRes
    private fun resolveTeamIcon(teamName: String): Int {
        return when (teamName.trim().lowercase(Locale.getDefault())) {
            "barcelona" -> R.drawable.vdgdsgfds
            "real madrid" -> R.drawable.jkljfsjfls
            "arsenal" -> R.drawable.vdgdsgfds
            "chelsea" -> R.drawable.jkljfsjfls
            else -> R.drawable.ic_users
        }
    }

    private companion object {
        private const val SAVED_MATCH_ID_OFFSET = 1000
    }
}

private enum class MatchFilter { ALL, SCHEDULED, FINISHED }
