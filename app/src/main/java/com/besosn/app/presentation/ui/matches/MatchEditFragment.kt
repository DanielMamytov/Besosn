package com.besosn.app.presentation.ui.matches

import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.besosn.app.R
import com.besosn.app.data.local.db.AppDatabase
import com.besosn.app.data.model.MatchEntity
import com.besosn.app.databinding.FragmentMatchEditBinding
import com.besosn.app.presentation.ui.calendar.CalendarFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale


class MatchEditFragment : Fragment() {

    private var _binding: FragmentMatchEditBinding? = null
    private val binding get() = _binding!!

    private var popup: PopupWindow? = null
    private val teamOptions = mutableListOf("Barcelona", "Real Madrid", "Arsenal", "Chelsea")
    private val matchCalendar: Calendar = Calendar.getInstance()
    private var dateSelected = false
    private var homePhotoUri: String? = null
    private var awayPhotoUri: String? = null
    private var editingMatch: MatchModel? = null

    private val pickHomeImage =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            handleImagePicked(uri, isHomeTeam = true)
        }

    private val pickAwayImage =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            handleImagePicked(uri, isHomeTeam = false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { state ->
            homePhotoUri = state.getString(KEY_HOME_PHOTO_URI)
            awayPhotoUri = state.getString(KEY_AWAY_PHOTO_URI)
            val timeMillis = state.getLong(KEY_MATCH_TIME, -1L)
            if (timeMillis > 0) {
                matchCalendar.timeInMillis = timeMillis
            }
            dateSelected = state.getBoolean(KEY_DATE_SELECTED, false)
        }

        editingMatch = arguments?.let { bundle ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getSerializable(ARG_MATCH_TO_EDIT, MatchModel::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getSerializable(ARG_MATCH_TO_EDIT) as? MatchModel
            }
        }

        if (savedInstanceState == null) {
            editingMatch?.let { match ->
                matchCalendar.timeInMillis = match.date
                dateSelected = true
                homePhotoUri = match.homeIconUri?.takeIf { it.isNotBlank() }
                awayPhotoUri = match.awayIconUri?.takeIf { it.isNotBlank() }
            }
        }
    }

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

        if (savedInstanceState == null) {
            val matchToEdit = editingMatch
            if (matchToEdit != null) {
                applyEditingMatch(matchToEdit)
            } else {
                updateTimeViews(
                    matchCalendar.get(Calendar.HOUR_OF_DAY),
                    matchCalendar.get(Calendar.MINUTE),
                )
            }
        } else {
            updateTimeViews(
                matchCalendar.get(Calendar.HOUR_OF_DAY),
                matchCalendar.get(Calendar.MINUTE),
            )
        }

        homePhotoUri?.let { loadSelectedImage(binding.ivAddPhoto, it) }
        awayPhotoUri?.let { loadSelectedImage(binding.ivAddPhotoAwayt, it) }

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

        val calendarFragment = childFragmentManager.findFragmentById(R.id.calendarHost) as? CalendarFragment
        calendarFragment?.setOnDateSelectedListener { selectedMillis ->
            val hour = matchCalendar.get(Calendar.HOUR_OF_DAY)
            val minute = matchCalendar.get(Calendar.MINUTE)
            val second = matchCalendar.get(Calendar.SECOND)
            val millisecond = matchCalendar.get(Calendar.MILLISECOND)

            matchCalendar.timeInMillis = selectedMillis
            matchCalendar.set(Calendar.HOUR_OF_DAY, hour)
            matchCalendar.set(Calendar.MINUTE, minute)
            matchCalendar.set(Calendar.SECOND, second)
            matchCalendar.set(Calendar.MILLISECOND, millisecond)
            dateSelected = true
        }
        calendarFragment?.setInitialDate(matchCalendar.timeInMillis, dateSelected)

        setupDropdown(binding.ddTeam, binding.tvTeam, binding.ivCategoryArrow, teamOptions)
        setupDropdown(binding.ddTeam2, binding.tvTeam2, binding.ivTeamAwayArrow, teamOptions)

        binding.ivAddPhoto.setOnClickListener { pickHomeImage.launch(arrayOf("image/*")) }
        binding.ivAddPhotoAwayt.setOnClickListener { pickAwayImage.launch(arrayOf("image/*")) }

        binding.btnEdit.setOnClickListener { saveMatch() }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_HOME_PHOTO_URI, homePhotoUri)
        outState.putString(KEY_AWAY_PHOTO_URI, awayPhotoUri)
        outState.putLong(KEY_MATCH_TIME, matchCalendar.timeInMillis)
        outState.putBoolean(KEY_DATE_SELECTED, dateSelected)
    }

    private fun handleImagePicked(uri: Uri?, isHomeTeam: Boolean) {
        if (uri == null) return
        val resolver = context?.contentResolver ?: return

        try {
            resolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: SecurityException) {
            // Ignore if the URI does not grant persistable permissions
        }

        val uriString = uri.toString()
        if (isHomeTeam) {
            if (!homePhotoUri.isNullOrBlank() && homePhotoUri != uriString) {
                releasePersistedUri(resolver, homePhotoUri)
            }
            homePhotoUri = uriString
            _binding?.ivAddPhoto?.let { loadSelectedImage(it, uriString) }
        } else {
            if (!awayPhotoUri.isNullOrBlank() && awayPhotoUri != uriString) {
                releasePersistedUri(resolver, awayPhotoUri)
            }
            awayPhotoUri = uriString
            _binding?.ivAddPhotoAwayt?.let { loadSelectedImage(it, uriString) }
        }
    }

    private fun releasePersistedUri(resolver: ContentResolver, uriString: String?) {
        val oldUri = uriString?.takeIf { it.isNotBlank() }?.let { runCatching { Uri.parse(it) }.getOrNull() }
            ?: return
        try {
            resolver.releasePersistableUriPermission(oldUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: SecurityException) {
            // Ignore if we no longer hold the permission
        }
    }

    private fun loadSelectedImage(view: ImageView, uriString: String) {
        val uri = runCatching { Uri.parse(uriString) }.getOrNull() ?: return
        view.setImageURI(uri)
        if (view.drawable != null) {
            view.scaleType = ImageView.ScaleType.CENTER_CROP
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


    private fun applyEditingMatch(match: MatchModel) {
        binding.tvTeam.text = match.homeTeam
        binding.tvTeam.setTextColor(Color.WHITE)
        binding.tvTeam2.text = match.awayTeam
        binding.tvTeam2.setTextColor(Color.WHITE)

        binding.etGoals.setText(match.homeScore?.toString() ?: "")
        binding.etAwayGoals.setText(match.awayScore?.toString() ?: "")
        binding.etCity.setText(match.city.orEmpty())
        binding.etNotes.setText(match.notes.orEmpty())

        matchCalendar.timeInMillis = match.date
        dateSelected = true

        updateTimeViews(
            matchCalendar.get(Calendar.HOUR_OF_DAY),
            matchCalendar.get(Calendar.MINUTE),
        )

        homePhotoUri = match.homeIconUri?.takeIf { it.isNotBlank() }
        awayPhotoUri = match.awayIconUri?.takeIf { it.isNotBlank() }

        binding.ivAddPhoto.loadMatchIcon(match.homeIconRes, match.homeIconUri)
        binding.ivAddPhotoAwayt.loadMatchIcon(match.awayIconRes, match.awayIconUri)
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

        val homeTeam = binding.tvTeam.text.toString()
        val awayTeam = binding.tvTeam2.text.toString()
        val homeGoalsText = binding.etGoals.text.toString()
        val awayGoalsText = binding.etAwayGoals.text.toString()
        val city = binding.etCity.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()
        val homeGoals = homeGoalsText.toIntOrNull()
        val awayGoals = awayGoalsText.toIntOrNull()
        val hasHomeGoalsInput = homeGoalsText.isNotBlank()
        val hasAwayGoalsInput = awayGoalsText.isNotBlank()

        if (homeTeam == teamPlaceholder || awayTeam == teamPlaceholder ||
            city.isBlank() || !dateSelected
        ) {
            Toast.makeText(
                requireContext(),
                getString(R.string.match_edit_fill_all_fields),
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        if (hasHomeGoalsInput != hasAwayGoalsInput) {
            Toast.makeText(
                requireContext(),
                getString(R.string.match_edit_enter_both_scores),
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        val scoresProvided = hasHomeGoalsInput && hasAwayGoalsInput

        if (scoresProvided && (homeGoals == null || awayGoals == null)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.match_edit_enter_valid_scores),
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

        if ((homeGoals != null && homeGoals > 99) || (awayGoals != null && awayGoals > 99)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.match_edit_score_too_high),
                Toast.LENGTH_SHORT,
            ).show()

            return
        }

        val homeGoalsValue = if (scoresProvided) homeGoals else null
        val awayGoalsValue = if (scoresProvided) awayGoals else null
        val homePhoto = homePhotoUri?.takeIf { it.isNotBlank() }
        val awayPhoto = awayPhotoUri?.takeIf { it.isNotBlank() }
        val existingDbId = editingMatch
            ?.takeIf { it.id >= DB_MATCH_ID_OFFSET }
            ?.let { it.id - DB_MATCH_ID_OFFSET }

        val match = MatchEntity(
            id = existingDbId ?: 0,
            homeTeamName = homeTeam,
            awayTeamName = awayTeam,
            date = matchCalendar.timeInMillis,
            city = city,
            notes = notes,
            homeGoals = homeGoalsValue,
            awayGoals = awayGoalsValue,
            homePhotoUri = homePhoto,
            awayPhotoUri = awayPhoto,
        )

        viewLifecycleOwner.lifecycleScope.launch {
            val context = requireContext().applicationContext
            val db = Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
                .fallbackToDestructiveMigration()
                .build()
            try {
                val wasUpdate = existingDbId != null
                withContext(Dispatchers.IO) {
                    val dao = db.matchDao()
                    if (wasUpdate) {
                        dao.updateMatch(match)
                    } else {
                        dao.insertMatch(match)
                    }
                }

                Toast.makeText(
                    requireContext(),
                    getString(R.string.match_edit_saved_message),
                    Toast.LENGTH_SHORT,
                ).show()
                findNavController().popBackStack()
            } catch (_: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.match_edit_save_failed),
                    Toast.LENGTH_SHORT,
                ).show()
            } finally {
                db.close()
            }
        }
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
//            val header = content.findViewById<TextView>(R.id.tvHeader)
            val rv = content.findViewById<RecyclerView>(R.id.rv)

//            header.text = tv.text
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

    companion object {
        const val ARG_MATCH_TO_EDIT = "match_to_edit"
        private const val KEY_HOME_PHOTO_URI = "match_edit_home_photo_uri"
        private const val KEY_AWAY_PHOTO_URI = "match_edit_away_photo_uri"
        private const val KEY_MATCH_TIME = "match_edit_time"
        private const val KEY_DATE_SELECTED = "match_edit_date_selected"
    }

    private class DropdownViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txt: TextView = view.findViewById(R.id.tvText)
        val divider: View = view.findViewById(R.id.divider)
    }
}
