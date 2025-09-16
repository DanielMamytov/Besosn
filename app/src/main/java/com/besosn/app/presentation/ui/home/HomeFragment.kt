package com.besosn.app.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentHomeBinding
import com.besosn.app.presentation.ui.inventory.InventoryLocalDataSource
import com.besosn.app.presentation.ui.matches.MatchesLocalDataSource
import com.besosn.app.presentation.ui.teams.TeamsLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var statsJob: Job? = null

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

        refreshHomeStats()

    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            refreshHomeStats()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        statsJob?.cancel()
        statsJob = null
        _binding = null
    }

    private fun refreshHomeStats() {
        val ctx = context?.applicationContext ?: return
        statsJob?.cancel()
        statsJob = viewLifecycleOwner.lifecycleScope.launch {
            val matchesDeferred = async(Dispatchers.IO) {
                MatchesLocalDataSource.loadMatches(ctx).size
            }
            val teamsDeferred = async(Dispatchers.IO) {
                TeamsLocalDataSource.countTeams(ctx)
            }
            val inventoryDeferred = async(Dispatchers.IO) {
                InventoryLocalDataSource.countItems(ctx)
            }

            val totalMatches = matchesDeferred.await()
            val totalTeams = teamsDeferred.await()
            val totalInventoryItems = inventoryDeferred.await()

            _binding?.let { binding ->
                binding.matchesCount.text = formatCount(
                    totalMatches,
                    R.string.home_matches_empty,
                    R.plurals.home_matches_count,
                )
                binding.teamsCount.text = formatCount(
                    totalTeams,
                    R.string.home_teams_empty,
                    R.plurals.home_teams_count,
                )
                binding.inventoryCount.text = formatCount(
                    totalInventoryItems,
                    R.string.home_inventory_empty,
                    R.plurals.home_inventory_count,
                )
            }
        }
    }

    private fun formatCount(
        count: Int,
        @StringRes emptyResId: Int,
        @PluralsRes pluralResId: Int,
    ): String {
        return if (count == 0) {
            getString(emptyResId)
        } else {
            resources.getQuantityString(pluralResId, count, count)
        }

    }
}
