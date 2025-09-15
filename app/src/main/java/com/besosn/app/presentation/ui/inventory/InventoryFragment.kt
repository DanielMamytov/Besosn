package com.besosn.app.presentation.ui.inventory

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.besosn.app.R
import com.besosn.app.databinding.FragmentInventoryBinding
import com.besosn.app.domain.model.InventoryItem
import com.besosn.app.presentation.viewmodel.InventoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class InventoryFragment : Fragment(R.layout.fragment_inventory) {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryViewModel by viewModels()
    private lateinit var adapter: InventoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentInventoryBinding.bind(view)

        adapter = InventoryAdapter(mutableListOf(), ::onEditItem, ::onDeleteItem)
        binding.rvInventory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInventory.adapter = adapter

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryFragment_to_inventoryEditFragment)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.items.collectLatest { items ->
                adapter.submitList(items)
                binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun onEditItem(item: InventoryItem) {
        val bundle = Bundle().apply { putSerializable("item", item) }
        findNavController().navigate(R.id.action_inventoryFragment_to_inventoryEditFragment, bundle)
    }

    private fun onDeleteItem(item: InventoryItem) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setMessage("Please confirm delete action before continuing")
            .setPositiveButton("Confirm") { _, _ -> viewModel.deleteItem(item) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
