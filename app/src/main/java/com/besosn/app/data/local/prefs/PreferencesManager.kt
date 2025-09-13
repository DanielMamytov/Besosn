package com.besosn.app.data.local.prefs

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun isOnboardingCompleted(): Boolean =
        prefs.getBoolean(KEY_ONBOARDING, false)

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING, completed).apply()
    }

    companion object {
        private const val KEY_ONBOARDING = "onboarding_completed"
    }
}

