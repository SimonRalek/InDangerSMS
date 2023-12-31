package com.example.indangersms

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class SafetyTip(
    val title: String,
    val details: String
)

class TipsAdapter(private val tipsList: List<SafetyTip>) :
    RecyclerView.Adapter<TipsAdapter.TipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tip, parent, false)
        return TipViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        val tip = tipsList[position]
        holder.bind(tip)
    }

    override fun getItemCount(): Int {
        return tipsList.size
    }

    inner class TipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        private val detailsTextView: TextView = itemView.findViewById(R.id.textViewDetails)
        private val arrowImageView: ImageView = itemView.findViewById(R.id.imageViewArrow)

        private var isExpanded = false

        init {
            itemView.setOnClickListener {
                toggleDetails()
            }
        }

        fun bind(tip: SafetyTip) {
            titleTextView.text = tip.title
            detailsTextView.text = tip.details
            updateArrowIcon()
        }

        private fun toggleDetails() {
            isExpanded = !isExpanded

            if (isExpanded) {
                expandDetails()
            } else {
                collapseDetails()
            }
        }

        private fun expandDetails() {
            detailsTextView.visibility = View.VISIBLE

            val slideDown = ObjectAnimator.ofFloat(detailsTextView, "alpha", 0f, 1f)
            slideDown.duration = 150

            val rotateArrow = ObjectAnimator.ofFloat(arrowImageView, "rotation", 0f, 180f)
            rotateArrow.duration = 150

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(slideDown, rotateArrow)
            animatorSet.start()
        }

        private fun collapseDetails() {
            val slideUp = ObjectAnimator.ofFloat(detailsTextView, "alpha", 1f, 0f)
            slideUp.duration = 150

            val rotateArrow = ObjectAnimator.ofFloat(arrowImageView, "rotation", 180f, 0f)
            rotateArrow.duration = 150

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(slideUp, rotateArrow)
            animatorSet.start()

            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    detailsTextView.visibility = View.GONE
                }
            })
        }

        private fun updateArrowIcon() {
            val rotation = if (isExpanded) 180f else 0f // Rotate arrow based on visibility
            arrowImageView.rotation = rotation
        }
    }
}
