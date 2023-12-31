package com.example.indangersms.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.indangersms.R
import com.example.indangersms.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_help, container, false)

        val disclaimerTextView = view.findViewById<TextView>(R.id.disclaimerTextView)
        disclaimerTextView.text = "DISCLAIMER: This app is not a replacement for emergency services. In case of a life-threatening situation, always contact your local emergency services immediately."

        activity?.findViewById<TextView>(R.id.title)?.text = "Help"
        addQaItem(view, "How do I send an emergency SMS?", "Press the designated emergency button on the main screen to send an SMS to your predefined emergency contact with your current location.")
        addQaItem(view, "Is it possible to cancel an emergency SMS once it's triggered?", "No, once the emergency SMS is sent, it cannot be canceled. Please ensure the emergency button is pressed only when necessary.")
        addQaItem(view, "Can I test the emergency SMS feature without actually sending it to my contact?", "Yes, you can enable a test mode in the app settings to simulate the emergency SMS without sending it to your contact.")
        addQaItem(view, "Why do you need Notifications permission?", "If a message is sent, we aim to notify you through a notification. Additionally, the widget lacks a mechanism to convey whether the message was successfully sent or not.")
        addQaItem(view, "Can I add multiple emergency contacts?", "At present, the app supports a single emergency contact. Future updates may include the option to add multiple contacts for increased flexibility.")
        addQaItem(view, "Do you store my data?", "No, we do no store any data. Settings are stored locally and we have no access to them.")
        addQaItem(view, "Do you sell any data?", "No, we do not have any data on you to sell.")

        return view
    }

    private fun addQaItem(view: View, question: String, answer: String) {
        val qaContainer = view.findViewById<LinearLayout>(R.id.qaContainer)

        val qaItemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_qa, null)

        val questionTextView = qaItemView.findViewById<TextView>(R.id.questionTextView)
        val answerTextView = qaItemView.findViewById<TextView>(R.id.answerTextView)

        questionTextView.text = question
        answerTextView.text = answer

        qaContainer.addView(qaItemView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}