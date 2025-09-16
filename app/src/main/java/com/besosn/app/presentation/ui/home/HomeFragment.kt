package com.besosn.app.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentHomeBinding
import com.besosn.app.presentation.ui.matches.MatchesLocalDataSource

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.cardTeams.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_teamsFragment)
        }
        binding.cardInventory.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_inventoryFragment)
        }
        binding.cardMatches.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_matchesFragment)
        }
        binding.btnArticles.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_articlesFragment)
        }
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        updateMatchesCount()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            updateMatchesCount()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateMatchesCount() {
        val context = context ?: return
        val totalMatches = MatchesLocalDataSource.loadMatches(context).size
        val countText = resources.getQuantityString(
            R.plurals.home_matches_count,
            totalMatches,
            totalMatches,
        )
        binding.matchesCount.text = countText
    }
}
