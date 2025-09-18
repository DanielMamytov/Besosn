package com.besosn.app.presentation.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.besosn.app.R
import com.besosn.app.data.local.db.AppDatabase
import com.besosn.app.databinding.FragmentSettingsBinding
import com.besosn.app.presentation.ui.teams.TeamsLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.shareAdd.setOnClickListener { shareApp() }
        binding.rateUs.setOnClickListener { rateApp() }
        binding.privacyPolicy.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_privacyPolicyFragment)
        }
        binding.termsOfUse.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_termsOfUseFragment)
        }
        binding.btnArticles.setOnClickListener { confirmClearData() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun shareApp() {
        val packageName = requireContext().packageName
        val shareText = getString(R.string.settings_share_text, packageName)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        runCatching {
            startActivity(Intent.createChooser(intent, getString(R.string.settings_share_chooser_title)))
        }.onFailure {
            if (isAdded) {
                Toast.makeText(requireContext(), R.string.settings_share_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun rateApp() {
        val packageName = requireContext().packageName
        val playUri = Uri.parse("market://details?id=$packageName")
        val marketIntent = Intent(Intent.ACTION_VIEW, playUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        }

        try {
            startActivity(marketIntent)
        } catch (_: ActivityNotFoundException) {
            val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            runCatching { startActivity(Intent(Intent.ACTION_VIEW, webUri)) }
                .onFailure {
                    if (isAdded) {
                        Toast.makeText(requireContext(), R.string.settings_rate_error, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun confirmClearData() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_clear_data_title)
            .setMessage(R.string.settings_clear_data_message)
            .setPositiveButton(R.string.settings_clear_data_confirm) { _, _ -> clearAppData() }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun clearAppData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val context = requireContext().applicationContext
            val success = withContext(Dispatchers.IO) {
                try {
                    val db = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()

                    try {
                        db.playerDao().deleteAllPlayers()
                        db.teamDao().deleteAllTeams()
                        db.inventoryDao().deleteAllItems()
                        db.matchDao().deleteAllMatches()
                    } finally {
                        db.close()
                    }

                    context.getSharedPreferences(PREFS_NAME_MATCHES, Context.MODE_PRIVATE)
                        .edit()
                        .remove(PREFS_KEY_MATCHES)
                        .apply()

                    TeamsLocalDataSource.seedDefaultTeamsIfNeeded(context)
                    true
                } catch (_: Exception) {
                    false
                }
            }

            if (!isAdded) return@launch

            val messageRes = if (success) {
                R.string.settings_clear_data_success
            } else {
                R.string.settings_clear_data_error
            }
            Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DATABASE_NAME = "app_db"
        private const val PREFS_NAME_MATCHES = "matches_prefs"
        private const val PREFS_KEY_MATCHES = "matches"
    }
}
