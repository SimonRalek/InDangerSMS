package com.example.indangersms.ui.tips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.indangersms.R
import com.example.indangersms.SafetyTip
import com.example.indangersms.TipsAdapter
import com.example.indangersms.databinding.FragmentDashboardBinding


class TipsFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var tipsAdapter: TipsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tips, container, false)

        val tipsList = listOf(
            SafetyTip(
                "Stay Aware of Surroundings",
                "Be aware of your surroundings, especially in unfamiliar areas. Pay attention to people, vehicles, and any unusual activities."
            ),
            SafetyTip(
                "Plan Your Route",
                "Inform someone you trust about your plans, especially if you're going to a new place. Share details of your route and expected arrival time."
            ),
            SafetyTip(
                "Keep Your Phone Charged",
                "Ensure your phone is charged, and carry a power bank if needed. A charged phone is crucial for making emergency calls."
            ),
            SafetyTip(
                "Use Well-Lit Paths",
                "Stick to well-lit and populated paths, especially during the night. Avoid shortcuts through dark or isolated areas."
            ),
            SafetyTip(
                "Safe Transportation",
                "Choose reputable transportation services, and share trip details with someone you trust. Verify the identity of drivers and use well-lit and official transportation hubs."
            ),
            SafetyTip(
                "Keep Emergency Contact Updated",
                "Regularly update your emergency contact in the app. Ensure that they are people you trust and can rely on."
            ),
            SafetyTip(
                "Be Mindful of Intoxication",
                "If consuming alcohol, do so responsibly. Avoid situations where you may become vulnerable due to intoxication."
            ),
            SafetyTip(
                "Learn Basic Self-Defense Techniques",
                "Consider taking a self-defense class to learn basic techniques for personal safety."
            )
        )

        recyclerView = view.findViewById(R.id.recyclerViewTips)
        tipsAdapter = TipsAdapter(tipsList)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = tipsAdapter
        activity?.findViewById<TextView>(R.id.title)?.text = "Tips"

        return view
    }

    override fun onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment: Boolean) {
        println(activity?.actionBar?.title);
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}