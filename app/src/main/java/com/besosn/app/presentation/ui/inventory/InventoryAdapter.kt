package com.besosn.app.presentation.ui.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.besosn.app.databinding.ItemInventoryBinding
import com.besosn.app.domain.model.InventoryItem

/**
 * Adapter displaying inventory items.
 */
class InventoryAdapter(
    private val items: MutableList<InventoryItem>,
    private val onEdit: (InventoryItem) -> Unit,
    private val onDelete: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class InventoryViewHolder(private val binding: ItemInventoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InventoryItem) {
            binding.tvName.text = item.name
            binding.tvQuantity.text = "Qty: ${item.quantity}"
            binding.tvCategory.text = item.category
            binding.tvBadge.text = item.badge
            binding.btnEdit.setOnClickListener { onEdit(item) }
            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }
}
