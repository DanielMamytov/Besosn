package com.besosn.app.presentation.ui.teams

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.data.local.db.AppDatabase
import com.besosn.app.databinding.FragmentTeamsEditBinding
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TeamsEditFragment : Fragment(R.layout.fragment_teams_edit) {

    private var _binding: FragmentTeamsEditBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTeamsEditBinding.bind(view)

        val lettersOnly = InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[a-zA-Z ]*"))) source else ""
        }
        binding.etTeamName.filters = arrayOf(lettersOnly)
        binding.etCity.filters = arrayOf(lettersOnly)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnEdit.setOnClickListener { saveTeam() }
        binding.btnDelete.visibility = View.GONE // no deletion when creating

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun saveTeam() {
        val name = binding.etTeamName.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val foundedStr = binding.etFoundedYear.text.toString().trim()
        val playersStr = binding.etPlayersCount.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        when {
            name.isEmpty() -> {
                binding.etTeamName.error = "Required"
                return
            }
            city.isEmpty() -> {
                binding.etCity.error = "Required"
                return
            }
            foundedStr.isEmpty() -> {
                binding.etFoundedYear.error = "Required"
                return
            }
            playersStr.isEmpty() -> {
                binding.etPlayersCount.error = "Required"
                return
            }
        }

        val founded = foundedStr.toIntOrNull() ?: 0
        val playersCount = playersStr.toIntOrNull() ?: 0

        if (founded <= 0) {
            binding.etFoundedYear.error = "Invalid"
            return
        }
        if (playersCount <= 0) {
            binding.etPlayersCount.error = "Invalid"
            return
        }

        val players = if (playersCount > 0) {
            List(playersCount) { index ->
                PlayerModel("Player ${index + 1}", "", index + 1)
            }
        } else emptyList()

        val team = TeamModel(
            name = name,
            city = city,
            foundedYear = founded,
            notes = notes,
            players = players,
            iconRes = R.drawable.ic_users
        )

        viewLifecycleOwner.lifecycleScope.launch {
            val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "app_db")
                .fallbackToDestructiveMigration()
                .build()
            val teamId = withContext(Dispatchers.IO) { db.teamDao().insertTeam(team.toEntity()).toInt() }
            val playerEntities = players.map { it.toEntity(teamId) }
            if (playerEntities.isNotEmpty()) {
                withContext(Dispatchers.IO) { db.playerDao().insertPlayers(playerEntities) }
            }
            setFragmentResult("add_team_result", Bundle())
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
