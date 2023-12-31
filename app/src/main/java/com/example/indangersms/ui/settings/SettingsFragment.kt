package com.example.indangersms.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.example.indangersms.PermissionManager
import com.example.indangersms.R
import com.example.indangersms.Settings
import com.example.indangersms.ui.contactlist.ContactListFragment

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val prefManager = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val contactPicker = findPreference<Preference>("contact_picker_preference")
        val msg = findPreference<EditTextPreference>("text_message")
        val themeSwitch = findPreference<SwitchPreferenceCompat>("theme_preference")
        val testMode = findPreference<SwitchPreferenceCompat>("test_mode")

        if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            if (themeSwitch != null) {
                themeSwitch.isChecked = true
            }
        }

        val name = prefManager.getString("contactName", null)
        contactPicker?.summary = name ?: "Click to pick contact"

        if (msg != null) {
            msg.setOnPreferenceChangeListener { preference, newValue ->
                val editor = prefManager.edit()

                editor.putString("message", newValue as String)

                editor.apply()
                true
            }
        }

        themeSwitch?.setOnPreferenceChangeListener { preference, newValue ->
            val isChecked = newValue as? Boolean

            val editor = prefManager.edit()
            if (isChecked == true) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putString("theme_key", "dark")
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putString("theme_key", "light")
            }

            editor.apply()
            true
        }

        testMode?.setOnPreferenceChangeListener { preference, newValue ->
            val isChecked = newValue as? Boolean

            val editor = prefManager.edit()
            if (isChecked == true) {
                editor.putBoolean("test_mode", true)
            } else editor.putBoolean("test_mode", false)

            editor.apply()
            true
        }

        val bundle = arguments
        if (bundle != null) {
            val contactName = bundle.getString("contactName")
            if (contactPicker != null) {
                val prefManager = PreferenceManager.getDefaultSharedPreferences(requireContext())
                val editor = prefManager.edit()

                contactPicker.summary = contactName

                editor.putString("contactName", contactName)
                editor.putString("contactNumber", bundle.getString("contactPhoneNumber"))
                editor.apply()
            }
        }

        contactPicker?.setOnPreferenceClickListener {
            openContactPicker()
            true
        }

    }

    private fun openContactPicker() {
        val permManager = PermissionManager(activity as Settings)
        if (permManager.checkOutsidePermission(
                android.Manifest.permission.READ_CONTACTS,
                "Missing Contact Permission",
                "To select a contact you need to permit contact permission"
            )
        ) {

            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            val contactListFragment = ContactListFragment()

            fragmentTransaction.replace(R.id.settings_container, contactListFragment, tag)
            fragmentTransaction.addToBackStack(tag)
            fragmentTransaction.commit()
        }
    }

}