package com.besosn.app.presentation.ui.matches

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.besosn.app.R
import com.besosn.app.data.local.db.AppDatabase
import com.besosn.app.data.model.MatchEntity
import com.besosn.app.databinding.FragmentMatchDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MatchDetailFragment : Fragment(R.layout.fragment_match_detail) {

    private var _binding: FragmentMatchDetailBinding? = null
    private val binding get() = _binding!!
    private var match: MatchModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        match = arguments?.let { bundle ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getSerializable(ARG_MATCH, MatchModel::class.java)
            } else {
                @Suppress("DEPRECATION")
                (bundle.getSerializable(ARG_MATCH) as? MatchModel)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchDetailBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnEdit.setOnClickListener {
            val matchToEdit = match ?: return@setOnClickListener
            if (matchToEdit.isImmutable) {
                Toast.makeText(
                    requireContext(),
                    R.string.match_default_edit_toast,
                    Toast.LENGTH_SHORT,
                ).show()
                return@setOnClickListener
            }
            val args = bundleOf(MatchEditFragment.ARG_MATCH_TO_EDIT to matchToEdit)
            findNavController().navigate(
                R.id.action_matchDetailFragment_to_matchEditFragment,
                args,
            )
        }
        binding.btnDelete.setOnClickListener {
            val matchToDelete = match ?: return@setOnClickListener
            when {
                matchToDelete.isImmutable -> showDeleteNotAllowedToast()
                matchToDelete.isLegacyMatch() -> deleteLegacyMatch(matchToDelete)
                matchToDelete.isDatabaseMatch() -> deleteDatabaseMatch(matchToDelete)
                else -> showDeleteNotAllowedToast()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        match?.let { bindMatch(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindMatch(match: MatchModel) {
        binding.firstTeam.text = match.homeTeam
        binding.secondTeam.text = match.awayTeam

        binding.team1Logo.loadMatchIcon(match.homeIconRes, match.homeIconUri)
        binding.team2Logo.loadMatchIcon(match.awayIconRes, match.awayIconUri)

        if (match.isFinished) {
            binding.team1Score.text = match.homeScore?.toString()
                ?: getString(R.string.match_detail_score_placeholder)
            binding.team2Score.text = match.awayScore?.toString()
                ?: getString(R.string.match_detail_score_placeholder)
        } else {
            val placeholder = getString(R.string.match_detail_score_placeholder)
            binding.team1Score.text = placeholder
            binding.team2Score.text = placeholder
        }

        val city = match.city?.takeIf { it.isNotBlank() }
            ?: getString(R.string.match_detail_unknown_city)
        binding.tvCityValue.text = city

        val date = Date(match.date)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.tvFoundedValue.text = getString(
            R.string.match_detail_date_time,
            dateFormat.format(date),
            timeFormat.format(date),
        )

        val notes = match.notes?.takeIf { it.isNotBlank() }
            ?: getString(R.string.match_detail_no_notes)
        binding.tvNotes.text = notes

        binding.btnEdit.alpha = if (match.isImmutable) 0.6f else 1f
        binding.btnDelete.alpha = if (match.canBeDeleted()) 1f else 0.6f
    }

    private fun deleteLegacyMatch(match: MatchModel) {
        val index = match.id - LEGACY_MATCH_ID_OFFSET
        if (index < 0) {
            showDeleteFailedToast()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val context = requireContext().applicationContext
            val deleted = withContext(Dispatchers.IO) {
                MatchesLocalDataSource.deleteSavedMatch(context, index)
            }

            if (deleted) {
                onMatchDeleted()
            } else {
                showDeleteFailedToast()
            }
        }
    }

    private fun deleteDatabaseMatch(match: MatchModel) {
        val entity = match.toEntityForDatabase()
        if (entity == null) {
            showDeleteFailedToast()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val context = requireContext().applicationContext
            val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
                .fallbackToDestructiveMigration()
                .build()
            try {
                withContext(Dispatchers.IO) {
                    db.matchDao().deleteMatch(entity)
                }
                onMatchDeleted()
            } catch (_: Exception) {
                showDeleteFailedToast()
            } finally {
                db.close()
            }
        }
    }

    private fun onMatchDeleted() {
        Toast.makeText(
            requireContext(),
            R.string.match_delete_success,
            Toast.LENGTH_SHORT,
        ).show()
        notifyMatchesChanged()
        findNavController().popBackStack()
    }

    private fun showDeleteFailedToast() {
        Toast.makeText(
            requireContext(),
            R.string.match_delete_failed,
            Toast.LENGTH_SHORT,
        ).show()
    }

    private fun showDeleteNotAllowedToast() {
        Toast.makeText(
            requireContext(),
            R.string.match_default_delete_toast,
            Toast.LENGTH_SHORT,
        ).show()
    }

    private fun notifyMatchesChanged() {
        runCatching {
            val entry = findNavController().getBackStackEntry(R.id.matchesFragment)
            entry.savedStateHandle[MatchEditFragment.RESULT_KEY_MATCHES_UPDATED] = true
        }
    }

    companion object {
        const val ARG_MATCH = "arg_match"
    }
}

private fun MatchModel.canBeDeleted(): Boolean {
    if (isImmutable) return false
    return isLegacyMatch() || isDatabaseMatch()
}

private fun MatchModel.isLegacyMatch(): Boolean =
    id in LEGACY_MATCH_ID_OFFSET until DB_MATCH_ID_OFFSET

private fun MatchModel.isDatabaseMatch(): Boolean = id >= DB_MATCH_ID_OFFSET

private fun MatchModel.toEntityForDatabase(): MatchEntity? {
    if (!isDatabaseMatch()) return null
    return MatchEntity(
        id = id - DB_MATCH_ID_OFFSET,
        homeTeamName = homeTeam,
        awayTeamName = awayTeam,
        date = date,
        city = city.orEmpty(),
        notes = notes.orEmpty(),
        homeGoals = homeScore,
        awayGoals = awayScore,
        homePhotoUri = homeIconUri,
        awayPhotoUri = awayIconUri,
    )
}
