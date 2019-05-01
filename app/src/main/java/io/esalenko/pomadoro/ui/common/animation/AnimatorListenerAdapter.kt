package io.esalenko.pomadoro.ui.common.animation

import android.animation.Animator


interface AnimatorListenerAdapter : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {

    }

    override fun onAnimationEnd(animation: Animator?) {

    }

    override fun onAnimationCancel(animation: Animator?) {

    }

    override fun onAnimationStart(animation: Animator?) {

    }
}