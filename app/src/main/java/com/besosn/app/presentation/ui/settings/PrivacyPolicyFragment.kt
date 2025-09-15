package com.besosn.app.presentation.ui.settings

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentPrivacyPolicyBinding

class PrivacyPolicyFragment : Fragment(R.layout.fragment_privacy_policy) {
    private var _binding: FragmentPrivacyPolicyBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPrivacyPolicyBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
