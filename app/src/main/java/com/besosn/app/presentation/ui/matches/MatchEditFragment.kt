package com.besosn.app.presentation.ui.matches

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.besosn.app.R
import com.besosn.app.data.local.db.AppDatabase
import com.besosn.app.databinding.FragmentMatchEditBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import java.util.Locale

class MatchEditFragment : Fragment() {

    private var _binding: FragmentMatchEditBinding? = null
    private val binding get() = _binding!!

    private var popup: PopupWindow? = null
    private val teamOptions = mutableListOf("Barcelona", "Real Madrid", "Arsenal", "Chelsea")
    private val matchCalendar: Calendar = Calendar.getInstance()
    private var dateSelected = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMatchEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadTeamOptions()

        updateTimeViews(
            matchCalendar.get(Calendar.HOUR_OF_DAY),
            matchCalendar.get(Calendar.MINUTE),
        )

        val scoreTooHighMessage = getString(R.string.match_edit_score_too_high)
        val goalsFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val newValue = dest.toString().substring(0, dstart) +
                source.subSequence(start, end) + dest.toString().substring(dend)
            if (newValue.length > 2) {
                Toast.makeText(requireContext(), scoreTooHighMessage, Toast.LENGTH_SHORT).show()
                ""
            } else {
                null
            }
        }

        binding.etGoals.filters = arrayOf(goalsFilter)
        binding.etAwayGoals.filters = arrayOf(goalsFilter)

        binding.timePickerContainer.setOnClickListener { showTimePicker() }
        binding.datePickerContainer.setOnClickListener { showDatePicker() }

        setupDropdown(binding.ddTeam, binding.tvTeam, binding.ivCategoryArrow, teamOptions)
        setupDropdown(binding.ddTeam2, binding.tvTeam2, binding.ivTeamAwayArrow, teamOptions)

        binding.btnEdit.setOnClickListener { saveMatch() }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun loadTeamOptions() {
        viewLifecycleOwner.lifecycleScope.launch {
            val context = requireContext().applicationContext
            val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
                .fallbackToDestructiveMigration()
                .build()

            val names = withContext(Dispatchers.IO) {
                try {
                    db.teamDao().getTeams().map { it.name }
                } catch (_: Exception) {
                    emptyList()
                } finally {
                    db.close()
                }
            }

            if (names.isNotEmpty()) {
                teamOptions.clear()
                teamOptions.addAll(names)
            }
        }
    }


    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                matchCalendar.set(Calendar.YEAR, year)
                matchCalendar.set(Calendar.MONTH, month)
                matchCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                dateSelected = true

                val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.tvDate.text = fmt.format(matchCalendar.time)
                binding.tvDate.setTextColor(Color.WHITE)
            },
            matchCalendar.get(Calendar.YEAR),
            matchCalendar.get(Calendar.MONTH),
            matchCalendar.get(Calendar.DAY_OF_MONTH),
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                matchCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                matchCalendar.set(Calendar.MINUTE, minute)
                matchCalendar.set(Calendar.SECOND, 0)
                matchCalendar.set(Calendar.MILLISECOND, 0)
                updateTimeViews(hourOfDay, minute)
            },
            matchCalendar.get(Calendar.HOUR_OF_DAY),
            matchCalendar.get(Calendar.MINUTE),
            false,
        ).show()
    }

    private fun updateTimeViews(hourOfDay: Int, minute: Int) {
        val amPm = if (hourOfDay >= 12) "PM" else "AM"
        val hour12 = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
        binding.tvHour.text = String.format(Locale.getDefault(), "%02d", hour12)
        binding.tvMinute.text = String.format(Locale.getDefault(), "%02d", minute)
        binding.tvAmPm.text = amPm
    }

    private fun saveMatch() {
        val teamPlaceholder = getString(R.string.match_edit_choose_team)
        val datePlaceholder = getString(R.string.match_edit_select_date)

        val homeTeam = binding.tvTeam.text.toString()
        val awayTeam = binding.tvTeam2.text.toString()
        val homeGoalsText = binding.etGoals.text.toString()
        val awayGoalsText = binding.etAwayGoals.text.toString()
        val city = binding.etCity.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()
        val dateText = binding.tvDate.text.toString()

        val homeGoals = homeGoalsText.toIntOrNull()
        val awayGoals = awayGoalsText.toIntOrNull()

        if (homeTeam == teamPlaceholder || awayTeam == teamPlaceholder ||
            homeGoals == null || awayGoals == null ||
            city.isBlank() || !dateSelected || dateText == datePlaceholder
        ) {
            Toast.makeText(
                requireContext(),
                getString(R.string.match_edit_fill_all_fields),
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        if (homeTeam == awayTeam) {
            Toast.makeText(
                requireContext(),
                getString(R.string.match_edit_same_team_error),
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        if (homeGoals > 99 || awayGoals > 99) {
            Toast.makeText(
                requireContext(),
                getString(R.string.match_edit_score_too_high),
                Toast.LENGTH_SHORT,
            ).show()

            return
        }

        val prefs = requireContext().getSharedPreferences("matches_prefs", Context.MODE_PRIVATE)
        val arr = JSONArray(prefs.getString("matches", "[]"))
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val obj = JSONObject().apply {
            put("homeTeam", homeTeam)
            put("awayTeam", awayTeam)
            put("homeGoals", homeGoals)
            put("awayGoals", awayGoals)
            put("notes", notes)
            put("city", city)
            put("date", dateFormat.format(matchCalendar.time))
            put("time", timeFormat.format(matchCalendar.time))
            put("timestamp", matchCalendar.timeInMillis)

        }
        arr.put(obj)
        prefs.edit().putString("matches", arr.toString()).apply()

        Toast.makeText(
            requireContext(),
            getString(R.string.match_edit_saved_message),
            Toast.LENGTH_SHORT,
        ).show()

        findNavController().popBackStack()
    }

    private fun setupDropdown(
        anchorView: FrameLayout,
        tv: TextView,
        arrow: View,
        list: List<String>,
    ) {
        anchorView.setOnClickListener {
            popup?.let {
                if (it.isShowing) {
                    it.dismiss()
                    return@setOnClickListener
                }

            }

            val content = layoutInflater.inflate(R.layout.popup_dropdown, null, false)
            val header = content.findViewById<TextView>(R.id.tvHeader)
            val rv = content.findViewById<RecyclerView>(R.id.rv)

            header.text = tv.text
            rv.layoutManager = LinearLayoutManager(requireContext())

            val popupWindow = PopupWindow(
                content,
                anchorView.width,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true,
            ).apply {
                isOutsideTouchable = true
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                elevation = 16f
                setOnDismissListener {
                    arrow.rotation = 0f
                    if (popup === this) {
                        popup = null
                    }
                }
            }

            rv.adapter = object : RecyclerView.Adapter<DropdownViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropdownViewHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_dropdown_row, parent, false)
                    return DropdownViewHolder(itemView)
                }

                override fun getItemCount() = list.size

                override fun onBindViewHolder(holder: DropdownViewHolder, position: Int) {
                    holder.txt.text = list[position]
                    holder.divider.visibility = if (position == list.lastIndex) View.GONE else View.VISIBLE
                    holder.itemView.setOnClickListener {
                        tv.text = list[position]
                        tv.setTextColor(Color.WHITE)
                        popupWindow.dismiss()
                    }
                }
            }

            popup = popupWindow
            arrow.animate().rotation(180f).setDuration(120).start()
            popupWindow.showAsDropDown(anchorView, 0, 8)
        }
    }

    override fun onDestroyView() {
        popup?.dismiss()
        popup = null
        super.onDestroyView()
        _binding = null
    }

    private class DropdownViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txt: TextView = view.findViewById(R.id.tvText)
        val divider: View = view.findViewById(R.id.divider)
    }
}
