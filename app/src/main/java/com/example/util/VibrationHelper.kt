package com.example.util

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class VibrationHelper(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun tick() {
        vibrateSimple(30L, 80)
    }

    fun heavyClick() {
        vibrateSimple(70L, 200)
    }

    fun wheelNotchTick() {
        vibrateSimple(22L, 120)
    }

    fun coinLandingPattern() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 30, 40, 25, 50, 75)
            val amplitudes = intArrayOf(0, 80, 0, 140, 0, 240)
            vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(longArrayOf(0, 40, 40, 80), -1)
        }
    }

    fun victoryPattern() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 50, 60, 50, 60, 120)
            val amplitudes = intArrayOf(0, 100, 0, 160, 0, 255)
            vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(longArrayOf(0, 60, 60, 100), -1)
        }
    }

    private fun vibrateSimple(durationMs: Long, amplitude: Int) {
        if (vibrator?.hasVibrator() == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255))
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        }
    }
}
