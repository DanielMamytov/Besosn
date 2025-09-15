package com.besosn.app.presentation.ui.teams

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.besosn.app.R
import com.besosn.app.data.local.db.AppDatabase
import com.besosn.app.databinding.FragmentTeamsDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.net.Uri

/**
 * Screen displaying detailed information about a team.
 */
class TeamsDetailFragment : Fragment(R.layout.fragment_teams_detail) {

    private var _binding: FragmentTeamsDetailBinding? = null
    private val binding get() = _binding!!

    private var team: TeamModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTeamsDetailBinding.bind(view)

        team = requireArguments().getSerializable("team") as? TeamModel

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnEdit.setOnClickListener {
            team?.let {
                val bundle = Bundle().apply { putSerializable("team", it) }
                findNavController().navigate(
                    R.id.action_teamsDetailFragment_to_teamsEditFragment,
                    bundle
                )
            }
        }
        binding.btnDelete.setOnClickListener { confirmDelete() }

        setFragmentResultListener("team_updated") { _, bundle ->
            val updated = bundle.getSerializable("team") as? TeamModel
            updated?.let {
                team = it
                bindTeam(it)
            }
        }

        team?.let { bindTeam(it) }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun bindTeam(team: TeamModel) {
        if (team.iconUri != null) {
            binding.imgTeamIcon.setImageURI(Uri.parse(team.iconUri))
        } else {
            binding.imgTeamIcon.setImageResource(
                if (team.iconRes != 0) team.iconRes else R.drawable.ic_users
            )
        }
        binding.tvTeamName.text = team.name
        binding.tvCityValue.text = team.city
        binding.tvFoundedValue.text = team.foundedYear.toString()
        binding.tvPlayersValue.text = team.playersCount.toString()
        binding.tvNotes.text = team.notes

        binding.rvPlayers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlayers.adapter = PlayersAdapter(team.players)

        if (team.isDefault) {
            binding.btnEdit.isEnabled = false
            binding.btnDelete.isEnabled = false
            binding.btnDelete.visibility = View.GONE
        } else {
            binding.btnEdit.isEnabled = true
            binding.btnDelete.isEnabled = true
            binding.btnDelete.visibility = View.VISIBLE
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(requireContext())
            .setMessage("Please confirm delete action before continuing")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Confirm") { _, _ -> team?.let { deleteTeam(it) } }
            .show()
    }

    private fun deleteTeam(team: TeamModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "app_db")
                .fallbackToDestructiveMigration()
                .build()
            withContext(Dispatchers.IO) {
                db.playerDao().deletePlayersByTeam(team.id)
                db.teamDao().deleteTeam(team.toEntity())
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

