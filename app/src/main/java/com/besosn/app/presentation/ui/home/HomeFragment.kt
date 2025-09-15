package com.besosn.app.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentHomeBinding

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
