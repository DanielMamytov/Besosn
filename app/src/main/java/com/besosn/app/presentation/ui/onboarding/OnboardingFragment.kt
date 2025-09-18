package com.besosn.app.presentation.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.utils.Constants

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnStart).setOnClickListener {
            val prefs = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(Constants.PREF_HAS_SEEN_ONBOARDING, true).apply()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.onboardingFragment, true)
                .build()
            findNavController().navigate(R.id.action_onboardingFragment_to_homeFragment, null, navOptions)
        }
    }
}
