package com.besosn.app.presentation.ui.teams

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentTeamsDetailBinding

class TeamsDetailFragment : Fragment(R.layout.fragment_teams_detail) {

    private var _binding: FragmentTeamsDetailBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTeamsDetailBinding.bind(view)

        val team = requireArguments().getSerializable("team") as? TeamModel

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_teamsDetailFragment_to_teamsEditFragment)
        }

        team?.let { bindTeam(it) }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun bindTeam(team: TeamModel) {
        binding.imageView2.setImageResource(team.iconRes)
        binding.tvTeamName.text = team.name
        binding.tvCityValue.text = team.city
        binding.tvFoundedValue.text = team.foundedYear.toString()
        binding.tvPlayersValue.text = team.playersCount.toString()

        if (team.isDefault) {
            binding.btnEdit.isEnabled = false
            binding.btnDelete.isEnabled = false
            binding.btnDelete.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
