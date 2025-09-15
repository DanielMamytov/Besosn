package com.besosn.app.presentation.ui.splash

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.besosn.app.R
import com.besosn.app.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dots = listOf(
            R.id.dot0,
            R.id.dot1,
            R.id.dot2,
            R.id.dot3
        )

        val anims = listOf(
            R.anim.dot_pulse_0,
            R.anim.dot_pulse_1,
            R.anim.dot_pulse_2,
            R.anim.dot_pulse_3
        )

        dots.zip(anims).forEach { (dotId, animId) ->
            view.findViewById<ImageView>(dotId)
                .startAnimation(AnimationUtils.loadAnimation(requireContext(), animId))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            delay(2500)
            val prefs = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
            val hasSeenOnboarding = prefs.getBoolean(Constants.PREF_HAS_SEEN_ONBOARDING, false)
            if (hasSeenOnboarding) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
            }
        }
    }
}
