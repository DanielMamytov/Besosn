package com.besosn.app.presentation.ui.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.besosn.app.R

class InventoryDropdown @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : FrameLayout(ctx, attrs) {

    private val view = inflate(ctx, R.layout.view_inventory_dropdown, this)
    private val tv = view.findViewById<TextView>(R.id.tvHintOrValue)
    private val arrow = view.findViewById<ImageView>(R.id.ivArrow)

    private var popup: PopupWindow? = null
    private var items: List<String> = emptyList()
    var onSelected: ((String, Int) -> Unit)? = null

    init {
        isClickable = true
        isFocusable = true
        setOnClickListener { toggle() }
    }

    fun setHint(text: String) { tv.text = text; tv.setTextColor(Color.parseColor("#B3FFFFFF")) }
    fun setItems(list: List<String>) { items = list }

    private fun toggle() {
        if (popup?.isShowing == true) { popup?.dismiss(); return }
        showPopup()
    }

    private fun showPopup() {
        val content = LayoutInflater.from(context).inflate(R.layout.popup_dropdown, null, false)
        val rv = content.findViewById<RecyclerView>(R.id.rv)
        content.findViewById<TextView>(R.id.tvHeader).text = tv.text

        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = object : RecyclerView.Adapter<VH>() {
            override fun onCreateViewHolder(p: ViewGroup, t: Int) =
                VH(LayoutInflater.from(p.context).inflate(R.layout.item_dropdown_row, p, false))
            override fun getItemCount() = items.size
            override fun onBindViewHolder(h: VH, pos: Int) {
                h.txt.text = items[pos]
                h.divider.visibility = if (pos == items.lastIndex) View.GONE else View.VISIBLE
                h.itemView.setOnClickListener {
                    select(items[pos], pos)
                    popup?.dismiss()
                }
            }
        }

        val pw = PopupWindow(content, width, WindowManager.LayoutParams.WRAP_CONTENT, true).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            elevation = 12f
            setOnDismissListener { arrow.animate().rotation(0f).setDuration(120).start() }
        }
        popup = pw
        arrow.animate().rotation(180f).setDuration(120).start()
        pw.showAsDropDown(this, 0, 8)
    }

    private fun select(value: String, index: Int) {
        tv.text = value
        tv.setTextColor(Color.WHITE)
        onSelected?.invoke(value, index)
    }

    private class VH(v: View) : RecyclerView.ViewHolder(v) {
        val txt: TextView = v.findViewById(R.id.tvText)
        val divider: View = v.findViewById(R.id.divider)
    }
}
