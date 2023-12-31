package com.example.indangersms.ui.contactlist

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.indangersms.R
import com.example.indangersms.ui.settings.SettingsFragment

class ContactListFragment : Fragment() {

    private lateinit var listView: ListView

    private lateinit var emptyTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<TextView>(R.id.title)?.text = "Contact List"

        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)

        listView = view.findViewById(R.id.contactsListView)
        emptyTextView = view.findViewById(R.id.emptyTextView)

        loadContacts()

        return view
    }

    private fun loadContacts() {
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor == null || cursor.count == 0) {
            emptyTextView.visibility = View.VISIBLE
            listView.visibility = View.GONE
        } else {
            emptyTextView.visibility = View.GONE
            listView.visibility = View.VISIBLE

            val fromColumns = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            val toViews = intArrayOf(R.id.contactName, R.id.contactPhoneNumber)

            val adapter = SimpleCursorAdapter(
                requireContext(),
                R.layout.contact_list_item,
                cursor,
                fromColumns,
                toViews,
                0
            )

            listView.adapter = adapter

            listView.setOnItemClickListener { _, _, position, _ ->
                cursor.moveToPosition(position)

                val prefManager = PreferenceManager.getDefaultSharedPreferences(requireContext())
                val editor = prefManager.edit()

                val nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val phoneNumberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                var contactName = ""
                if (nameColumnIndex >= 0) {
                    contactName = cursor.getString(nameColumnIndex)
                }

                var contactPhoneNumber = ""
                if (phoneNumberColumnIndex >= 0) {
                    contactPhoneNumber = cursor.getString(phoneNumberColumnIndex)
                }

                val bundle = Bundle().apply {
                    putString("contactName", contactName)
                    putString("contactPhoneNumber", contactPhoneNumber)
                }

                val previousFragment = SettingsFragment()
                previousFragment.arguments = bundle

                editor.apply()
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.settings_container, previousFragment)?.commit()
            }
        }

    }


}
