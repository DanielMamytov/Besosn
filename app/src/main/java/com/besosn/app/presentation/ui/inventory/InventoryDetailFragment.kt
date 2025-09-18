package com.besosn.app.presentation.ui.inventory

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.databinding.FragmentInventoryDetailBinding
import com.besosn.app.domain.model.InventoryItem

class InventoryDetailFragment : Fragment(R.layout.fragment_inventory_detail) {
    private var _binding: FragmentInventoryDetailBinding? = null
    private val binding get() = _binding!!
    private var currentItem: InventoryItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentInventoryDetailBinding.bind(view)

        currentItem = arguments?.getSerializable("item") as? InventoryItem
        currentItem?.let { item ->
            binding.tvItemName.text = item.name
            binding.tvCityValue.text = item.category
            binding.tvFoundedValue.text = item.quantity.toString()
            binding.tvPlayersValue.text = item.badge
            binding.tvAboutTitle.text = getString(R.string.inventory_detail_about_title, item.name)
            binding.tvNotes.text = item.notes.ifBlank { getString(R.string.inventory_detail_no_notes) }

            val parsedUri = item.photoUri
                ?.takeIf { it.isNotBlank() }
                ?.let { runCatching { Uri.parse(it) }.getOrNull() }
            if (parsedUri != null) {
                binding.imageView2.setImageURI(parsedUri)
            } else {
                binding.imageView2.setImageResource(R.drawable.ball)
            }
        }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnEdit.setOnClickListener {
            val bundle = Bundle().apply {
                currentItem?.let { putSerializable("item", it) }
            }
            findNavController().navigate(
                R.id.action_inventoryDetailFragment_to_inventoryEditFragment,
                bundle,
            )
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
