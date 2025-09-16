package com.besosn.app.presentation.ui.inventory

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.besosn.app.R
import com.besosn.app.databinding.DialogInventoryConfirmDeleteBinding
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
    private val categories = listOf("Balls", "Cones", "Bibs", "Nets", "Med", "Other")
    private val badges = listOf("OK", "Needs", "Fix", "Lost")

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
        }

        binding.etCity.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            val newValue = dest.toString().substring(0, dstart) +
                source.subSequence(start, end) + dest.toString().substring(dend)
            if (newValue.length > 3) {
                Toast.makeText(requireContext(), "You can't add more than 999 to items of one type to inventory", Toast.LENGTH_SHORT).show()
                ""
            } else null
        })

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnEdit.setOnClickListener { saveItem() }
        binding.btnCancel.setOnClickListener {
            val itemToDelete = currentItem
            if (itemToDelete != null) {
                showDeleteConfirmationDialog(itemToDelete)
            } else {
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun saveItem() {
        val name = binding.etTeamName.text.toString().trim()
        val quantityText = binding.etCity.text.toString()
        val category = binding.tvCategory.text.toString()
        val badge = binding.tvCategoryValue.text.toString()
        val notes = binding.etNotes.text.toString().trim()

        if (name.isBlank() || quantityText.isBlank() ||
            category == "Choose category" || badge == "Choose badge" || notes.isBlank()
        ) {
            Toast.makeText(requireContext(), "Fill all fields to add item", Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = quantityText.toInt()

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

    private fun showDeleteConfirmationDialog(item: InventoryItem) {
        val dialogBinding = DialogInventoryConfirmDeleteBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val deleteAction = {
            viewModel.deleteItem(item)
            dialog.dismiss()
            findNavController().popBackStack()
        }

        dialogBinding.btnConfirm.setOnClickListener { deleteAction() }
        dialogBinding.btnDelete.setOnClickListener { deleteAction() }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setupDropdown(anchorView: FrameLayout, tv: TextView, arrow: View, list: List<String>) {
        anchorView.setOnClickListener {
            if (::popup.isInitialized && popup.isShowing) {
                popup.dismiss()
                return@setOnClickListener
            }

            val content = layoutInflater.inflate(R.layout.popup_dropdown, null, false)
            val rv = content.findViewById<RecyclerView>(R.id.rv)

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

