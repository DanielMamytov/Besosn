package com.besosn.app.presentation.ui.inventory

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.besosn.app.R
import com.besosn.app.databinding.FragmentInventoryEditBinding
import com.besosn.app.domain.model.InventoryItem
import com.besosn.app.presentation.viewmodel.InventoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryEditFragment : Fragment() {

    private var _binding: FragmentInventoryEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryViewModel by viewModels()
    private var currentItem: InventoryItem? = null

    private lateinit var popup: PopupWindow
    private val categories = listOf("Balls", "Cones", "Bibs", "Nets", "Med")
    private val badges = listOf("New", "Used", "Damaged")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentInventoryEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentItem = arguments?.getSerializable("item") as? InventoryItem

        setupDropdown(binding.ddCategory, binding.tvCategory, binding.ivCategoryArrow, categories)
        setupDropdown(binding.ddCategoryAnchor, binding.tvCategoryValue, binding.ivCategoryArrow1, badges)

        if (currentItem != null) {
            binding.etTeamName.setText(currentItem!!.name)
            binding.etCity.setText(currentItem!!.quantity.toString())
            binding.tvCategory.text = currentItem!!.category
            binding.tvCategory.setTextColor(Color.WHITE)
            binding.tvCategoryValue.text = currentItem!!.badge
            binding.tvCategoryValue.setTextColor(Color.WHITE)
            binding.etNotes.setText(currentItem!!.notes)
        } else {
            binding.btnDelete.visibility = View.GONE
        }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnEdit.setOnClickListener { saveItem() }
        binding.btnDelete.setOnClickListener { confirmDelete() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun saveItem() {
        val name = binding.etTeamName.text.toString()
        val quantity = binding.etCity.text.toString().toIntOrNull() ?: 0
        val category = binding.tvCategory.text.toString()
        val badge = binding.tvCategoryValue.text.toString()
        val notes = binding.etNotes.text.toString()

        val item = InventoryItem(
            id = currentItem?.id ?: 0,
            name = name,
            quantity = quantity,
            category = category,
            badge = badge,
            notes = notes
        )
        viewModel.addItem(item)
        findNavController().popBackStack()
    }

    private fun confirmDelete() {
        val item = currentItem ?: return
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setMessage("Please confirm delete action before continuing")
            .setPositiveButton("Confirm") { _, _ ->
                viewModel.deleteItem(item)
                findNavController().popBackStack()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupDropdown(anchorView: FrameLayout, tv: TextView, arrow: View, list: List<String>) {
        anchorView.setOnClickListener {
            if (::popup.isInitialized && popup.isShowing) {
                popup.dismiss()
                return@setOnClickListener
            }

            val content = layoutInflater.inflate(R.layout.popup_dropdown, null, false)
            val header = content.findViewById<TextView>(R.id.tvHeader)
            val rv = content.findViewById<RecyclerView>(R.id.rv)

            header.text = tv.text
            rv.layoutManager = LinearLayoutManager(requireContext())
            rv.adapter = object : RecyclerView.Adapter<VH>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                    VH(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_dropdown_row, parent, false))
                override fun getItemCount() = list.size
                override fun onBindViewHolder(holder: VH, pos: Int) {
                    holder.txt.text = list[pos]
                    holder.divider.visibility =
                        if (pos == list.lastIndex) View.GONE else View.VISIBLE
                    holder.itemView.setOnClickListener {
                        tv.text = list[pos]
                        tv.setTextColor(Color.WHITE)
                        popup.dismiss()
                    }
                }
            }

            popup = PopupWindow(
                content,
                anchorView.width,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            ).apply {
                isOutsideTouchable = true
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                elevation = 16f
                setOnDismissListener { arrow.rotation = 0f }
            }

            arrow.animate().rotation(180f).setDuration(120).start()
            popup.showAsDropDown(anchorView, 0, 8)
        }
    }

    private class VH(view: View) : RecyclerView.ViewHolder(view) {
        val txt: TextView = view.findViewById(R.id.tvText)
        val divider: View = view.findViewById(R.id.divider)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

