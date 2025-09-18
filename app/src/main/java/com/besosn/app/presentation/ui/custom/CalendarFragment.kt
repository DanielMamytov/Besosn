package com.besosn.app.presentation.ui.calendar

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.besosn.app.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private lateinit var monthContainer: View
    private lateinit var weekContainer: View
    private lateinit var yearContainer: View

    private lateinit var tvTabMonth: TextView
    private lateinit var tvTabWeek: TextView
    private lateinit var tvTabYear: TextView
    private lateinit var indMonth: View
    private lateinit var indWeek: View
    private lateinit var indYear: View

    private lateinit var weekHeader: LinearLayout
    private lateinit var gridMonth: GridLayout
    private lateinit var rowWeek: LinearLayout
    private lateinit var gridYear: GridLayout
    private lateinit var tvDateTitle: TextView

    private val cal: Calendar = Calendar.getInstance()
    private val selectedCal: Calendar = Calendar.getInstance()

    private val activeColor = 0xFFFC4F08.toInt()
    private val inactiveColor = 0x9EFFFFFF.toInt()
    private val textBlack = 0xFF000000.toInt()

    private var hasSelection: Boolean = false
    private var onDateSelected: ((Long) -> Unit)? = null
    private var pendingSelectionMillis: Long? = null
    private var pendingSelectionActive: Boolean? = null
    private var isViewReady: Boolean = false

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    private var unbounded: Typeface? = null

    enum class Mode { MONTH, WEEK, YEAR }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isViewReady = true

        monthContainer = view.findViewById(R.id.monthContainer)
        weekContainer = view.findViewById(R.id.weekContainer)
        yearContainer = view.findViewById(R.id.yearContainer)

        tvTabMonth = view.findViewById(R.id.tvTabMonth)
        tvTabWeek = view.findViewById(R.id.tvTabWeek)
        tvTabYear = view.findViewById(R.id.tvTabYear)
        indMonth = view.findViewById(R.id.indMonth)
        indWeek = view.findViewById(R.id.indWeek)
        indYear = view.findViewById(R.id.indYear)

        weekHeader = view.findViewById(R.id.weekHeader)
        gridMonth = view.findViewById(R.id.gridMonth)
        rowWeek = view.findViewById(R.id.rowWeek)
        gridYear = view.findViewById(R.id.gridYear)
        tvDateTitle = view.findViewById(R.id.tvDateTitle)

        unbounded = ResourcesCompat.getFont(requireContext(), R.font.unbounded)

        view.findViewById<View>(R.id.tabMonth).setOnClickListener { select(Mode.MONTH) }
        view.findViewById<View>(R.id.tabWeek).setOnClickListener { select(Mode.WEEK) }
        view.findViewById<View>(R.id.tabYear).setOnClickListener { select(Mode.YEAR) }

        buildWeekHeader()

        val initialMillis = pendingSelectionMillis ?: selectedCal.timeInMillis
        val initialSelectionActive = pendingSelectionActive ?: hasSelection
        applySelection(initialMillis, initialSelectionActive)

        select(Mode.MONTH)

        pendingSelectionMillis = null
        pendingSelectionActive = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewReady = false
    }

    fun setOnDateSelectedListener(listener: (Long) -> Unit) {
        onDateSelected = listener
    }

    fun setInitialDate(timeInMillis: Long, selectionActive: Boolean) {
        if (isViewReady) {
            applySelection(timeInMillis, selectionActive)
            pendingSelectionMillis = null
            pendingSelectionActive = null
        } else {
            pendingSelectionMillis = timeInMillis
            pendingSelectionActive = selectionActive
            cal.timeInMillis = timeInMillis
            selectedCal.timeInMillis = timeInMillis
            hasSelection = selectionActive
        }
    }

    private fun applySelection(timeInMillis: Long, selectionActive: Boolean) {
        cal.timeInMillis = timeInMillis
        selectedCal.timeInMillis = timeInMillis
        hasSelection = selectionActive

        renderMonth()
        renderWeek()
        renderYear()
        updateDateTitle()
    }

    private fun updateDateTitle() {
        if (!this::tvDateTitle.isInitialized) return
        if (hasSelection) {
            tvDateTitle.text = dateFormatter.format(selectedCal.time)
            tvDateTitle.setTextColor(0xFFFFFFFF.toInt())
        } else {
            tvDateTitle.text = getString(R.string.match_edit_select_date)
            tvDateTitle.setTextColor(inactiveColor)
        }
    }

    private fun notifyDateSelected() {
        if (!hasSelection) return

        val millis = Calendar.getInstance().apply {
            timeInMillis = selectedCal.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        onDateSelected?.invoke(millis)
    }

    private fun onDateChosen(year: Int, month: Int, day: Int) {
        hasSelection = true

        selectedCal.set(Calendar.YEAR, year)
        selectedCal.set(Calendar.MONTH, month)
        selectedCal.set(Calendar.DAY_OF_MONTH, day)

        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)

        renderMonth()
        renderWeek()
        renderYear()
        updateDateTitle()
        notifyDateSelected()
    }

    
    private fun select(mode: Mode) {
        monthContainer.visibility = if (mode == Mode.MONTH) View.VISIBLE else View.GONE
        weekContainer.visibility  = if (mode == Mode.WEEK)  View.VISIBLE else View.GONE
        yearContainer.visibility  = if (mode == Mode.YEAR)  View.VISIBLE else View.GONE

        tvTabMonth.setTextColor(if (mode == Mode.MONTH) activeColor else 0xFFFFFFFF.toInt())  
        tvTabWeek.setTextColor (if (mode == Mode.WEEK)  activeColor else 0xFFFFFFFF.toInt())   
        tvTabYear.setTextColor (if (mode == Mode.YEAR)  activeColor else 0xFFFFFFFF.toInt())   

        indMonth.visibility = if (mode == Mode.MONTH) View.VISIBLE else View.GONE
        indWeek.visibility  = if (mode == Mode.WEEK)  View.VISIBLE else View.GONE
        indYear.visibility  = if (mode == Mode.YEAR)  View.VISIBLE else View.GONE
    }

    
    private fun renderMonth() {
        gridMonth.removeAllViews()
        gridMonth.columnCount = 7

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) 

        
        val first = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val firstDow = first.get(Calendar.DAY_OF_WEEK) 
        val offset = (firstDow - Calendar.SUNDAY + 7) % 7

        
        val daysInMonth = first.getActualMaximum(Calendar.DAY_OF_MONTH)

        
        val prev = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.DAY_OF_MONTH, -1)
        }
        val prevLastDay = prev.get(Calendar.DAY_OF_MONTH)

        
        for (i in 0 until 42) {
            val tv = makeDayCell()
            val lp = GridLayout.LayoutParams().apply {
                width = 0
                height = dp(44)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dp(1), dp(1), dp(1), dp(1))  
            }

            when {
                i < offset -> {
                    
                    val day = prevLastDay - (offset - 1 - i)
                    tv.text = day.toString()
                    tv.setTextColor(0xFFFFFFFF.toInt())  
                    tv.alpha = 0.5f
                    tv.setOnClickListener(null)
                }
                i >= offset + daysInMonth -> {
                    
                    val day = i - (offset + daysInMonth) + 1
                    tv.text = day.toString()
                    tv.setTextColor(0xFFFFFFFF.toInt())  
                    tv.alpha = 0.5f
                    tv.setOnClickListener(null)
                }
                else -> {
                    
                    val day = i - offset + 1
                    tv.text = day.toString()
                    tv.setTextColor(0xFFFFFFFF.toInt())  
                    val isSelected = hasSelection && isSameDate(cal, selectedCal, day)
                    tv.setBackgroundResource(
                        if (isSelected) R.drawable.bg_day_cell_selected else R.drawable.bg_day_cell
                    )
                    tv.setOnClickListener { onDateChosen(year, month, day) }
                }
            }
            gridMonth.addView(tv, lp)
        }
    }

    private fun isSameDate(monthCal: Calendar, sel: Calendar, day: Int): Boolean {
        return (monthCal.get(Calendar.YEAR) == sel.get(Calendar.YEAR)
                && monthCal.get(Calendar.MONTH) == sel.get(Calendar.MONTH)
                && day == sel.get(Calendar.DAY_OF_MONTH))
    }

    
    private fun buildWeekHeader() {
        weekHeader.removeAllViews()
        val names = arrayOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")
        for (n in names) {
            val tv = TextView(requireContext()).apply {
                text = n
                gravity = android.view.Gravity.CENTER
                setTextColor(0xFFFFFFFF.toInt())  
                textSize = 12f
                typeface = unbounded
            }
            val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            weekHeader.addView(tv, lp)
        }
    }

    private fun makeDayCell(): TextView =
        TextView(requireContext()).apply {
            gravity = android.view.Gravity.CENTER
            textSize = 14f
            typeface = unbounded
            setBackgroundResource(R.drawable.ripple_calendar)
            setTextColor(0xFFFFFFFF.toInt())  
        }

    
    private fun renderWeek() {
        rowWeek.removeAllViews()

        
        val base = Calendar.getInstance().apply { timeInMillis = selectedCal.timeInMillis }
        val dow = base.get(Calendar.DAY_OF_WEEK) 
        val shift = (dow - Calendar.SUNDAY + 7) % 7
        base.add(Calendar.DAY_OF_MONTH, -shift)

        for (i in 0 until 7) {
            val dayCal = Calendar.getInstance().apply { timeInMillis = base.timeInMillis; add(Calendar.DAY_OF_MONTH, i) }
            val tv = makeDayCell().apply {
                text = dayCal.get(Calendar.DAY_OF_MONTH).toString()
                setTextColor(textBlack)
                val isSelected = hasSelection && sameDay(dayCal, selectedCal)
                background = resources.getDrawable(
                    if (isSelected) R.drawable.bg_day_cell_selected else R.drawable.bg_day_cell,
                    null
                )
                setOnClickListener {
                    onDateChosen(
                        dayCal.get(Calendar.YEAR),
                        dayCal.get(Calendar.MONTH),
                        dayCal.get(Calendar.DAY_OF_MONTH),
                    )
                }
            }
            val lp = LinearLayout.LayoutParams(0, dp(44), 1f).apply {
                setMargins(dp(1), dp(1), dp(1), dp(1))  
            }
            rowWeek.addView(tv, lp)
        }
    }

    private fun sameDay(a: Calendar, b: Calendar): Boolean =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

    private fun makeYearCell(): TextView =
        TextView(requireContext()).apply {
            gravity = android.view.Gravity.CENTER
            textSize = 14f
            typeface = unbounded
            setBackgroundResource(R.drawable.bg_day_cell)
            setTextColor(0xFFFFFFFF.toInt())
        }

    
    
    private fun renderYear() {
        gridYear.removeAllViews()
        gridYear.columnCount = 3

        
        val currentYear = cal.get(Calendar.YEAR)
        val startYear = currentYear - 5   
        val endYear   = startYear + 11    

        for (year in startYear..endYear) {
            val tv = makeYearCell().apply {
                text = year.toString()
                
                setTextColor(if (year == currentYear) activeColor else 0xFFFFFFFF.toInt())
                
                paint.isFakeBoldText = year == currentYear

                setOnClickListener {
                    
                    val month = cal.get(Calendar.MONTH)
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)

                    
                    val max = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    selectedCal.set(Calendar.YEAR, year)
                    selectedCal.set(Calendar.MONTH, month)
                    selectedCal.set(Calendar.DAY_OF_MONTH, minOf(selectedCal.get(Calendar.DAY_OF_MONTH), max))

                    
                    renderYear()
                    renderMonth()
                    renderWeek()
                    updateDateTitle()
                    select(Mode.MONTH)
                    notifyDateSelected()
                }
            }

            val lp = GridLayout.LayoutParams().apply {
                width = 0
                height = dp(44)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dp(6), dp(6), dp(6), dp(6))
            }
            gridYear.addView(tv, lp)
        }
    }

    
    private fun dp(v: Int): Int {
        val d = resources.displayMetrics.density
        return (v * d).roundToInt()
    }
}
