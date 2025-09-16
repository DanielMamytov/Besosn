package com.besosn.app.presentation.ui.matches

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.besosn.app.R
import com.besosn.app.databinding.FragmentMatchesBinding
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MatchesFragment : Fragment(R.layout.fragment_matches) {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MatchesAdapter
    private val matches = mutableListOf<MatchModel>()
    private var currentFilter: MatchFilter = MatchFilter.ALL

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

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_matchesFragment_to_matchEditFragment)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            loadMatches()
        }
    }

    private fun loadMatches() {
        matches.clear()
        matches.addAll(getDefaultMatches())
        matches.addAll(loadSavedMatches())
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
                awayScore = 2
            ),
            MatchModel(
                id = 2,
                homeTeam = "Arsenal",
                awayTeam = "Chelsea",
                homeIconRes = R.drawable.vdgdsgfds,
                awayIconRes = R.drawable.jkljfsjfls,
                date = future.timeInMillis
            ),
        )
    }

    private fun loadSavedMatches(): List<MatchModel> {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(PREFS_KEY_MATCHES, null) ?: return emptyList()

        return try {
            val array = JSONArray(raw)
            val result = mutableListOf<MatchModel>()
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val homeTeam = obj.optString("homeTeam")
                val awayTeam = obj.optString("awayTeam")
                if (homeTeam.isBlank() || awayTeam.isBlank()) continue

                val timestamp = obj.optLong("timestamp", -1L)
                val dateMillis = if (timestamp > 0L) {
                    timestamp
                } else {
                    parseDateTime(obj.optString("date"), obj.optString("time")) ?: continue
                }

                val homeScore = (obj.opt("homeGoals") as? Number)?.toInt()
                val awayScore = (obj.opt("awayGoals") as? Number)?.toInt()

                result += MatchModel(
                    id = SAVED_MATCH_ID_OFFSET + i,
                    homeTeam = homeTeam,
                    awayTeam = awayTeam,
                    homeIconRes = resolveTeamIcon(homeTeam),
                    awayIconRes = resolveTeamIcon(awayTeam),
                    date = dateMillis,
                    homeScore = homeScore,
                    awayScore = awayScore,
                )
            }
            result
        } catch (_: JSONException) {
            emptyList()
        }
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

    private fun parseDateTime(date: String?, time: String?): Long? {
        if (date.isNullOrBlank() || time.isNullOrBlank()) return null
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
            format.parse("$date $time")?.time
        } catch (_: Exception) {
            null
        }
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
        private const val PREFS_NAME = "matches_prefs"
        private const val PREFS_KEY_MATCHES = "matches"
    }
}

private enum class MatchFilter { ALL, SCHEDULED, FINISHED }
