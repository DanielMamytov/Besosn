package com.besosn.app.presentation.ui.inventory

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.besosn.app.R
import com.besosn.app.databinding.FragmentInventoryEditBinding

class InventoryEditFragment : Fragment() {

    private var _binding: FragmentInventoryEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var popup: PopupWindow
    private val categories = listOf("Balls", "Cones", "Bibs", "Nets", "Med")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdown()
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun setupDropdown() {
        val anchor = binding.ddCategoryAnchor    // FrameLayout
        val tv = binding.tvCategoryValue         // TextView внутри
        val arrow = binding.ivCategoryArrow      // ImageView стрелка

        anchor.setOnClickListener {
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
                override fun getItemCount() = categories.size
                override fun onBindViewHolder(holder: VH, pos: Int) {
                    holder.txt.text = categories[pos]
                    holder.divider.visibility =
                        if (pos == categories.lastIndex) View.GONE else View.VISIBLE
                    holder.itemView.setOnClickListener {
                        tv.text = categories[pos]
                        tv.setTextColor(Color.WHITE)
                        popup.dismiss()
                    }
                }
            }

            popup = PopupWindow(
                content,
                anchor.width,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            ).apply {
                isOutsideTouchable = true
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                elevation = 16f
                setOnDismissListener { arrow.rotation = 0f }
            }

            arrow.animate().rotation(180f).setDuration(120).start()
            popup.showAsDropDown(anchor, 0, 8)
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
