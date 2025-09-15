package com.besosn.app.presentation.ui.matches

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.besosn.app.R
import com.besosn.app.databinding.FragmentMatchEditBinding
import java.util.Calendar

class MatchEditFragment : Fragment() {

    private lateinit var binding: FragmentMatchEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Обработчик для нажатия на LinearLayout или Button
        binding.timePickerContainer.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                // Определяем AM/PM
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                // Преобразуем в 12-часовой формат
                val hour12 = if (selectedHour % 12 == 0) 12 else selectedHour % 12

                // Устанавливаем значения в TextView
                binding.tvHour.text = String.format("%02d", hour12)
                binding.tvMinute.text = String.format("%02d", selectedMinute)
                binding.tvAmPm.text = amPm
            },
            hour,
            minute,
            false // Используем 12-часовой формат
        )

        timePicker.show()
    }
}
