package com.besosn.app.presentation.ui.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.besosn.app.databinding.InventoryItemBinding
import com.besosn.app.databinding.ItemInventoryBinding
import com.besosn.app.domain.model.InventoryItem


class InventoryAdapter(
    private val items: MutableList<InventoryItem>,
    private val onEdit: (InventoryItem) -> Unit,
    private val onDelete: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = InventoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<InventoryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class InventoryViewHolder(private val binding: InventoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InventoryItem) {
            binding.tvItemTitle.text = item.name
            binding.tvItemQuantity.text = "Qty: ${item.quantity}"
            binding.tvItemCategory.text = item.category
            binding.tvItemStatus.text = item.badge
            binding.btnNext.setOnClickListener { onEdit(item) }
//            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }
}
