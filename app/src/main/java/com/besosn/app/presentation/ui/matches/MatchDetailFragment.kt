package com.besosn.app.presentation.ui.matches

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentMatchDetailBinding

class MatchDetailFragment : Fragment(R.layout.fragment_match_detail) {

    private var _binding: FragmentMatchDetailBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMatchDetailBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_matchDetailFragment_to_matchEditFragment)
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
