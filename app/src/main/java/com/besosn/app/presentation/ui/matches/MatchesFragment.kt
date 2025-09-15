package com.besosn.app.presentation.ui.matches

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentMatchesBinding

class MatchesFragment : Fragment(R.layout.fragment_matches) {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchesBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_matchesFragment_to_matchEditFragment)
        }
        binding.rvMatches.setOnClickListener {
            findNavController().navigate(R.id.action_matchesFragment_to_matchDetailFragment)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
