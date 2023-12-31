package com.example.indangersms.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.indangersms.R
import com.example.indangersms.SenderProvider
import com.example.indangersms.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private lateinit var prefManager: SharedPreferences

    private lateinit var senderProvider: SenderProvider

    override fun onResume() {
        super.onResume()
        setSubtitle()
    }

    private fun setSubtitle() {
        binding.textUnder.text = if (!prefManager.getBoolean(
                "test_mode",
                false
            )
        ) "SEND ONLY IN EMERGENCY" else "IN TEST MODE"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        prefManager = PreferenceManager.getDefaultSharedPreferences(requireContext())

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.sendSmsButton.setOnClickListener {
            it.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(150)
                .withEndAction {
                    it.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(150)
                }

            val message = prefManager.getString("message", null) + " - "
            val contactNumber = prefManager.getString("contactNumber", null)
            val isTestMode = prefManager.getBoolean("test_mode", false)

            senderProvider = SenderProvider(requireContext())

            if (senderProvider.checkSettings(contactNumber, message)) {
                if (isTestMode) {
                    prefManager.getString("contactNumber", "unknown")
                        ?.let { it1 ->
                            showSimulatedSMSDialog(it1, message)
                        }
                } else {
                    senderProvider.sendSms(contactNumber, message) {}
                }
            }
        }
        binding.sendSmsButton.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))

        setSubtitle()

        activity?.findViewById<TextView>(R.id.title)?.text = "Emergency Protocol"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showSimulatedSMSDialog(phoneNumber: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Simulated SMS to $phoneNumber")

        var location: String? = ""
        senderProvider.getLocation { result ->
            result?.let {
                location = result

                val spannableString = SpannableString("$message $location")

                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(location))
                        startActivity(intent)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = true
                        ds.color = Color.BLUE
                    }
                }

                val startIndex = message.length + 1
                val endIndex = spannableString.length
                spannableString.setSpan(
                    clickableSpan,
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                builder.setMessage(spannableString)

                builder.setPositiveButton("OK") { dialog, which ->
                }

                val dialog: AlertDialog = builder.create()

                dialog.setMessage(spannableString)
                dialog.show()

                val textView = dialog.findViewById<TextView>(android.R.id.message)
                textView?.movementMethod = LinkMovementMethod.getInstance()
            } ?: run {
                val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
                alertBuilder.setTitle("Location Error")
                    .setMessage("Unable to retrieve location. Make sure location services are enabled.")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                    }
                alertBuilder.create().show()
            }
        }

    }

}