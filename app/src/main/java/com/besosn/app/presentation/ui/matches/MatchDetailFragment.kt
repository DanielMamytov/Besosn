package com.besosn.app.presentation.ui.matches

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentMatchDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    }

    companion object {
        const val ARG_MATCH = "arg_match"
    }
}
