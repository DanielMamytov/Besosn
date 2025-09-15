package com.besosn.app.presentation.ui.inventory

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentInventoryBinding

class InventoryFragment : Fragment(R.layout.fragment_inventory) {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentInventoryBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryFragment_to_inventoryEditFragment)
        }
        binding.rvTeams.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryFragment_to_inventoryDetailFragment)
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
