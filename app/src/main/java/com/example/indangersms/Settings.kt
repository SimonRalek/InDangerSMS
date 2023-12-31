package com.example.indangersms

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.indangersms.ui.contactlist.ContactListFragment
import com.example.indangersms.ui.settings.SettingsFragment

class Settings : BaseActivity() {

    private lateinit var goBack: ImageView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        goBack = findViewById(R.id.arrow_back)
        goBack.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.settings_container)

            if (currentFragment is ContactListFragment) {
                supportFragmentManager.popBackStack()
                findViewById<TextView>(R.id.title)?.text = "Settings"
            } else {
                val intent = Intent(this@Settings, MainActivity::class.java)
                startActivity(intent)
            }

        }

        val settingsFragment = SettingsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_container, settingsFragment)
            .commit()

    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.settings_container)

        if (currentFragment is ContactListFragment) {
            supportFragmentManager.popBackStack()
            findViewById<TextView>(R.id.title)?.text = "Settings"
        } else {
            val intent = Intent(this@Settings, MainActivity::class.java)
            startActivity(intent)
        }
    }
}