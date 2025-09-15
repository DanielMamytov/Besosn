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
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    // UI
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

    // state
    private val cal: Calendar = Calendar.getInstance()
    private var selectedCal: Calendar = Calendar.getInstance()

    private val activeColor = 0xFFFC4F08.toInt()
    private val inactiveColor = 0x9EFFFFFF.toInt()
    private val textBlack = 0xFF000000.toInt()
    private val textDim = 0x80000000.toInt()

    private var unbounded: Typeface? = null

    enum class Mode { MONTH, WEEK, YEAR }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        unbounded = ResourcesCompat.getFont(requireContext(), R.font.unbounded)

        // tabs
        view.findViewById<View>(R.id.tabMonth).setOnClickListener { select(Mode.MONTH) }
        view.findViewById<View>(R.id.tabWeek).setOnClickListener  { select(Mode.WEEK) }
        view.findViewById<View>(R.id.tabYear).setOnClickListener  { select(Mode.YEAR) }

        // headers Sun..Sat
        buildWeekHeader()

        // init content
        renderMonth()
        renderWeek()
        renderYear()

        select(Mode.MONTH)
    }

    /* ---------------- Tabs ----------------- */
    private fun select(mode: Mode) {
        monthContainer.visibility = if (mode == Mode.MONTH) View.VISIBLE else View.GONE
        weekContainer.visibility  = if (mode == Mode.WEEK)  View.VISIBLE else View.GONE
        yearContainer.visibility  = if (mode == Mode.YEAR)  View.VISIBLE else View.GONE

        tvTabMonth.setTextColor(if (mode == Mode.MONTH) activeColor else 0xFFFFFFFF.toInt())  // Белый для неактивных
        tvTabWeek.setTextColor (if (mode == Mode.WEEK)  activeColor else 0xFFFFFFFF.toInt())   // Белый для неактивных
        tvTabYear.setTextColor (if (mode == Mode.YEAR)  activeColor else 0xFFFFFFFF.toInt())   // Белый для неактивных

        indMonth.visibility = if (mode == Mode.MONTH) View.VISIBLE else View.GONE
        indWeek.visibility  = if (mode == Mode.WEEK)  View.VISIBLE else View.GONE
        indYear.visibility  = if (mode == Mode.YEAR)  View.VISIBLE else View.GONE
    }


    /* -------------- Month view -------------- */
    private fun renderMonth() {
        gridMonth.removeAllViews()
        gridMonth.columnCount = 7

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) // 0..11

        // 1) offset (Sun..Sat)
        val first = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val firstDow = first.get(Calendar.DAY_OF_WEEK) // 1=Sun .. 7=Sat
        val offset = (firstDow - Calendar.SUNDAY + 7) % 7

        // 2) days in month
        val daysInMonth = first.getActualMaximum(Calendar.DAY_OF_MONTH)

        // 3) prev month last day
        val prev = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.DAY_OF_MONTH, -1)
        }
        val prevLastDay = prev.get(Calendar.DAY_OF_MONTH)

        // 6 rows x 7 cols = 42
        var cellIndex = 0
        for (i in 0 until 42) {
            val tv = makeDayCell()
            val lp = GridLayout.LayoutParams().apply {
                width = 0
                height = dp(44)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dp(1), dp(1), dp(1), dp(1))  // минимальные отступы 1dp
            }

            when {
                i < offset -> {
                    // previous month
                    val day = prevLastDay - (offset - 1 - i)
                    tv.text = day.toString()
                    tv.setTextColor(0xFFFFFFFF.toInt())  // Белый цвет для чисел
                    tv.alpha = 0.5f
                    tv.setOnClickListener(null)
                }
                i >= offset + daysInMonth -> {
                    // next month
                    val day = i - (offset + daysInMonth) + 1
                    tv.text = day.toString()
                    tv.setTextColor(0xFFFFFFFF.toInt())  // Белый цвет для чисел
                    tv.alpha = 0.5f
                    tv.setOnClickListener(null)
                }
                else -> {
                    // current month
                    val day = i - offset + 1
                    tv.text = day.toString()
                    tv.setTextColor(0xFFFFFFFF.toInt())  // Белый цвет для чисел
                    tv.setBackgroundResource(
                        if (isSameDate(cal, selectedCal, day)) R.drawable.bg_day_cell_selected
                        else R.drawable.bg_day_cell
                    )
                    tv.setOnClickListener {
                        selectedCal = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, day)
                        }
                        renderMonth()
                        renderWeek()
                    }
                }
            }
            gridMonth.addView(tv, lp)
            cellIndex++
        }
    }


    private fun isSameDate(monthCal: Calendar, sel: Calendar, day: Int): Boolean {
        return (monthCal.get(Calendar.YEAR) == sel.get(Calendar.YEAR)
                && monthCal.get(Calendar.MONTH) == sel.get(Calendar.MONTH)
                && day == sel.get(Calendar.DAY_OF_MONTH))
    }

    /* -------------- Week header -------------- */
    private fun buildWeekHeader() {
        weekHeader.removeAllViews()
        val names = arrayOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")
        for (n in names) {
            val tv = TextView(requireContext()).apply {
                text = n
                gravity = android.view.Gravity.CENTER
                setTextColor(0xFFFFFFFF.toInt())  // Белый цвет текста
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
            setTextColor(0xFFFFFFFF.toInt())  // Белый цвет текста
        }


    /* --------------- Week view ---------------- */
    private fun renderWeek() {
        rowWeek.removeAllViews()

        // старт от Sunday недели выбранной даты
        val base = Calendar.getInstance().apply { timeInMillis = selectedCal.timeInMillis }
        val dow = base.get(Calendar.DAY_OF_WEEK) // 1..7 (Sun..Sat)
        val shift = (dow - Calendar.SUNDAY + 7) % 7
        base.add(Calendar.DAY_OF_MONTH, -shift)

        for (i in 0 until 7) {
            val dayCal = Calendar.getInstance().apply { timeInMillis = base.timeInMillis; add(Calendar.DAY_OF_MONTH, i) }
            val tv = makeDayCell().apply {
                text = dayCal.get(Calendar.DAY_OF_MONTH).toString()
                setTextColor(textBlack)
                background = resources.getDrawable(
                    if (sameDay(dayCal, selectedCal)) R.drawable.bg_day_cell_selected else R.drawable.bg_day_cell,
                    null
                )
                setOnClickListener {
                    selectedCal.timeInMillis = dayCal.timeInMillis
                    // если месяц другой — обновим вид месяца
                    cal.set(Calendar.YEAR, dayCal.get(Calendar.YEAR))
                    cal.set(Calendar.MONTH, dayCal.get(Calendar.MONTH))
                    renderMonth()
                    renderWeek()
                }
            }
            val lp = LinearLayout.LayoutParams(0, dp(44), 1f).apply {
                setMargins(dp(1), dp(1), dp(1), dp(1))  // минимальные отступы 1dp
            }
            rowWeek.addView(tv, lp)
        }
    }

    private fun sameDay(a: Calendar, b: Calendar): Boolean =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

    /* ---------------- Year view ---------------- */
    private fun renderYear() {
        gridYear.removeAllViews()
        gridYear.columnCount = 3

        val months = (0..11).toList()
        for (m in months) {
            val tv = TextView(requireContext()).apply {
                val name = Calendar.getInstance().apply { set(Calendar.MONTH, m) }
                    .getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) ?: (m + 1).toString()
                text = name.uppercase(Locale.getDefault())
                gravity = android.view.Gravity.CENTER
                textSize = 14f
                typeface = unbounded
                setTextColor(if (m == cal.get(Calendar.MONTH)) activeColor else 0xFFFFFFFF.toInt())
                setBackgroundResource(R.drawable.bg_day_cell)
                setOnClickListener {
                    cal.set(Calendar.MONTH, m)
                    // ставим выбранный день в рамках месяца
                    val max = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    if (selectedCal.get(Calendar.MONTH) != m ||
                        selectedCal.get(Calendar.YEAR) != cal.get(Calendar.YEAR)) {
                        selectedCal.set(Calendar.DAY_OF_MONTH, minOf(selectedCal.get(Calendar.DAY_OF_MONTH), max))
                        selectedCal.set(Calendar.MONTH, m)
                        selectedCal.set(Calendar.YEAR, cal.get(Calendar.YEAR))
                    }
                    renderYear()
                    renderMonth()
                    select(Mode.MONTH)
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

    /* ----------------- Utils ------------------ */
    private fun dp(v: Int): Int {
        val d = resources.displayMetrics.density
        return (v * d).roundToInt()
    }
}
