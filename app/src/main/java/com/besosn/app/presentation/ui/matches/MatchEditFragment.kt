package com.besosn.app.presentation.ui.matches

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.besosn.app.R
import com.besosn.app.databinding.FragmentMatchEditBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar

import java.util.Locale

class MatchEditFragment : Fragment() {

    private lateinit var binding: FragmentMatchEditBinding
    private lateinit var popup: PopupWindow
    private val teams = listOf("Barcelona", "Real Madrid", "Arsenal", "Chelsea")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize current time display
        val now = Calendar.getInstance()
        updateTimeViews(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))


        // time and date pickers
        binding.timePickerContainer.setOnClickListener { showTimePicker() }
        binding.datePickerContainer.setOnClickListener { showDatePicker() }


        // dropdowns for teams
        setupDropdown(binding.ddTeam, binding.tvTeam, binding.ivCategoryArrow, teams)
        setupDropdown(binding.ddTeam2, binding.tvTeam2, binding.ivTeamAwayArrow, teams)

        // limit goals input to two digits
        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            val newValue = dest.toString().substring(0, dstart) +
                    source.subSequence(start, end) + dest.toString().substring(dend)
            if (newValue.length > 2) {
                Toast.makeText(requireContext(),
                    "Score value can’t be more than 99 goals",
                    Toast.LENGTH_SHORT).show()
                ""
            } else null
        }
        binding.etGoals.filters = arrayOf(filter)
        binding.etAwayGoals.filters = arrayOf(filter)

        binding.btnEdit.setOnClickListener { saveMatch() }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selected = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.tvDate.text = fmt.format(selected.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                updateTimeViews(hourOfDay, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
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

        val homeTeam = binding.tvTeam.text.toString()
        val awayTeam = binding.tvTeam2.text.toString()
        val homeGoalsText = binding.etGoals.text.toString()
        val awayGoalsText = binding.etAwayGoals.text.toString()
        val city = binding.etCity.text.toString()
        val date = binding.tvDate.text.toString()
        val notes = binding.etNotes.text.toString()

        if (homeTeam == "Choose category" || awayTeam == "Choose category" ||
            homeGoalsText.isBlank() || awayGoalsText.isBlank() ||
            city.isBlank() || date == "Select date") {
            Toast.makeText(requireContext(), "Fill all fields to add match", Toast.LENGTH_SHORT).show()
            return
        }
        if (homeTeam == awayTeam) {
            Toast.makeText(requireContext(), "One team does not play against each other", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = requireContext().getSharedPreferences("matches_prefs", Context.MODE_PRIVATE)
        val arr = JSONArray(prefs.getString("matches", "[]"))
        val obj = JSONObject().apply {
            put("homeTeam", homeTeam)
            put("awayTeam", awayTeam)
            put("homeGoals", homeGoalsText.toInt())
            put("awayGoals", awayGoalsText.toInt())
            put("notes", notes)
            put("city", city)
            put("date", date)
            put("time", "${binding.tvHour.text}:${binding.tvMinute.text} ${binding.tvAmPm.text}")
        }
        arr.put(obj)
        prefs.edit().putString("matches", arr.toString()).apply()

        findNavController().popBackStack()
    }


    private fun setupDropdown(
        anchorView: FrameLayout,
        tv: TextView,
        arrow: View,
        list: List<String>
    ) {
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
                    holder.divider.visibility = if (pos == list.lastIndex) View.GONE else View.VISIBLE
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
                setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
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
}
