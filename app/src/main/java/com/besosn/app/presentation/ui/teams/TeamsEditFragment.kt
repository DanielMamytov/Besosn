package com.besosn.app.presentation.ui.teams

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentTeamsEditBinding

class TeamsEditFragment : Fragment(R.layout.fragment_teams_edit) {

    private var _binding: FragmentTeamsEditBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTeamsEditBinding.bind(view)

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
        val founded = binding.etFoundedYear.text.toString().toIntOrNull() ?: 0
        val playersCount = binding.etPlayersCount.text.toString().toIntOrNull() ?: 0
        val notes = binding.etNotes.text.toString().trim()

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

        setFragmentResult("add_team_result", bundleOf("team" to team))
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
