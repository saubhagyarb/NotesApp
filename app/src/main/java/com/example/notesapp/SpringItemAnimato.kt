package com.example.notesapp

import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class SpringItemAnimator : DefaultItemAnimator() {
    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        val view = holder.itemView
        view.translationY = -view.height * 0.25f  // Initial offset (25% of item height)

        SpringAnimation(view, SpringAnimation.TRANSLATION_Y).apply {
            spring = SpringForce(0f).apply {
                stiffness = SpringForce.STIFFNESS_LOW
                dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            }
            start()
        }

        dispatchAddStarting(holder)
        return false
    }
}