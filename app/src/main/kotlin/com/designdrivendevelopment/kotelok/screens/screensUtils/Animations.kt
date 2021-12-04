package com.designdrivendevelopment.kotelok.screens.screensUtils

import android.animation.ObjectAnimator
import android.util.Property
import android.view.View

fun objectAnimation(
    target: View,
    property: Property<View, Float>,
    startValue: Float? = null,
    endValue: Float,
    animationDuration: Long
): ObjectAnimator {
    return if (startValue != null) {
        ObjectAnimator.ofFloat(target, property, startValue, endValue).apply {
            duration = animationDuration
        }
    } else {
        ObjectAnimator.ofFloat(target, property, endValue).apply {
            duration = animationDuration
        }
    }
}

fun hideViewAnimation(target: View, duration: Long): ObjectAnimator {
    return objectAnimation(
        target = target,
        property = View.ALPHA,
        startValue = 1f,
        endValue = 0f,
        animationDuration = duration
    )
}

fun showViewAnimation(target: View, duration: Long): ObjectAnimator {
    return objectAnimation(
        target = target,
        property = View.ALPHA,
        startValue = 0f,
        endValue = 1f,
        animationDuration = duration
    )
}
