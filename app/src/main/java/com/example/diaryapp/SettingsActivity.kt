package com.example.diaryapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val appLock: SwitchPreferenceCompat = findPreference("app_lock")!!
            val setupPin: Preference = findPreference("app_pin")!!
            setupPin.isEnabled = appLock.isChecked
            appLock.setOnPreferenceChangeListener { preference, newValue ->
                setupPin.isEnabled = newValue as Boolean
                return@setOnPreferenceChangeListener true
            }

            setupPin.setOnPreferenceClickListener {
                val intent = Intent(requireContext(), SetPassActivity::class.java)
                startActivity(intent)
                return@setOnPreferenceClickListener true
            }

        }
    }


}