package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

enum class VibrationType(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val description: String
) {
    SOFT(
        id = "SOFT",
        title = "Мягкий клик",
        subtitle = "15 мс • Низкая амплитуда",
        badge = "Деликатный",
        description = "Тонкий бархатный отклик, почти бесшумный"
    ),
    CRISP(
        id = "CRISP",
        title = "Чёткий щелчок",
        subtitle = "35 мс • Средняя амплитуда",
        badge = "Баланс",
        description = "Классический механический щелчок, универсальный"
    ),
    HEAVY(
        id = "HEAVY",
        title = "Глубокий импульс",
        subtitle = "70 мс • Высокая амплитуда",
        badge = "Сильный",
        description = "Увесистый плотный толчок с максимальной отдачей"
    ),
    DOUBLE_TAP(
        id = "DOUBLE_TAP",
        title = "Двойной тап",
        subtitle = "2 удара • Ритмичный",
        badge = "Ритм",
        description = "Два быстрых акцентированных импульса подряд"
    ),
    TRIPLE_BURST(
        id = "TRIPLE_BURST",
        title = "Тройная дробь",
        subtitle = "3 удара • Скоростная серия",
        badge = "Серия",
        description = "Энергичная трещотка из трёх быстрых микрощелчков"
    ),
    CRESCENDO(
        id = "CRESCENDO",
        title = "Нарастающая волна",
        subtitle = "Плавный разгон • 3 шага",
        badge = "Волна",
        description = "Нарастающая волна от мягкого касания к чёткому пику"
    ),
    DYNAMIC_BUZZ(
        id = "DYNAMIC_BUZZ",
        title = "Энергичный зуммер",
        subtitle = "Фактурная пульсация",
        badge = "Драйв",
        description = "Резонирующий фактурный вибро-паттерн повышенной четкости"
    );

    companion object {
        fun fromId(id: String?): VibrationType {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CRISP
        }
    }
}

class VibrationHelper(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /**
     * Previews the selected vibration type with its signature feeling.
     */
    fun previewPattern(type: VibrationType) {
        if (vibrator?.hasVibrator() != true) return

        when (type) {
            VibrationType.SOFT -> vibrateSimple(18L, 65)
            VibrationType.CRISP -> vibrateSimple(38L, 160)
            VibrationType.HEAVY -> vibrateSimple(75L, 255)
            VibrationType.DOUBLE_TAP -> {
                val timings = longArrayOf(0, 25, 45, 30)
                val amplitudes = intArrayOf(0, 180, 0, 220)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.TRIPLE_BURST -> {
                val timings = longArrayOf(0, 18, 30, 20, 30, 25)
                val amplitudes = intArrayOf(0, 140, 0, 180, 0, 240)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.CRESCENDO -> {
                val timings = longArrayOf(0, 30, 20, 45, 20, 65)
                val amplitudes = intArrayOf(0, 70, 0, 160, 0, 255)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.DYNAMIC_BUZZ -> {
                val timings = longArrayOf(0, 45, 20, 25, 20, 50)
                val amplitudes = intArrayOf(0, 255, 0, 130, 0, 230)
                vibrateWaveform(timings, amplitudes)
            }
        }
    }

    fun tick(type: VibrationType = VibrationType.CRISP) {
        if (vibrator?.hasVibrator() != true) return

        when (type) {
            VibrationType.SOFT -> vibrateSimple(14L, 50)
            VibrationType.CRISP -> vibrateSimple(26L, 120)
            VibrationType.HEAVY -> vibrateSimple(45L, 210)
            VibrationType.DOUBLE_TAP -> {
                val timings = longArrayOf(0, 14, 25, 16)
                val amplitudes = intArrayOf(0, 120, 0, 160)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.TRIPLE_BURST -> {
                val timings = longArrayOf(0, 10, 18, 10, 18, 12)
                val amplitudes = intArrayOf(0, 90, 0, 120, 0, 150)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.CRESCENDO -> {
                val timings = longArrayOf(0, 15, 15, 28)
                val amplitudes = intArrayOf(0, 75, 0, 180)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.DYNAMIC_BUZZ -> {
                val timings = longArrayOf(0, 22, 14, 22)
                val amplitudes = intArrayOf(0, 190, 0, 150)
                vibrateWaveform(timings, amplitudes)
            }
        }
    }

    fun heavyClick(type: VibrationType = VibrationType.CRISP) {
        if (vibrator?.hasVibrator() != true) return

        when (type) {
            VibrationType.SOFT -> vibrateSimple(35L, 100)
            VibrationType.CRISP -> vibrateSimple(70L, 200)
            VibrationType.HEAVY -> vibrateSimple(90L, 255)
            VibrationType.DOUBLE_TAP -> {
                val timings = longArrayOf(0, 30, 40, 40)
                val amplitudes = intArrayOf(0, 200, 0, 255)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.TRIPLE_BURST -> {
                val timings = longArrayOf(0, 20, 25, 25, 25, 30)
                val amplitudes = intArrayOf(0, 160, 0, 200, 0, 255)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.CRESCENDO -> {
                val timings = longArrayOf(0, 35, 20, 50, 20, 75)
                val amplitudes = intArrayOf(0, 100, 0, 190, 0, 255)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.DYNAMIC_BUZZ -> {
                val timings = longArrayOf(0, 50, 20, 30, 20, 60)
                val amplitudes = intArrayOf(0, 255, 0, 160, 0, 240)
                vibrateWaveform(timings, amplitudes)
            }
        }
    }

    fun wheelNotchTick(type: VibrationType = VibrationType.CRISP) {
        if (vibrator?.hasVibrator() != true) return

        when (type) {
            VibrationType.SOFT -> vibrateSimple(12L, 65)
            VibrationType.CRISP -> vibrateSimple(22L, 130)
            VibrationType.HEAVY -> vibrateSimple(38L, 220)
            VibrationType.DOUBLE_TAP -> {
                val timings = longArrayOf(0, 12, 18, 14)
                val amplitudes = intArrayOf(0, 120, 0, 160)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.TRIPLE_BURST -> {
                val timings = longArrayOf(0, 8, 14, 8, 14, 10)
                val amplitudes = intArrayOf(0, 90, 0, 120, 0, 150)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.CRESCENDO -> {
                val timings = longArrayOf(0, 12, 12, 22)
                val amplitudes = intArrayOf(0, 70, 0, 180)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.DYNAMIC_BUZZ -> {
                val timings = longArrayOf(0, 18, 12, 18)
                val amplitudes = intArrayOf(0, 190, 0, 150)
                vibrateWaveform(timings, amplitudes)
            }
        }
    }

    fun coinLandingPattern(type: VibrationType = VibrationType.CRISP) {
        if (vibrator?.hasVibrator() != true) return

        when (type) {
            VibrationType.SOFT -> {
                val timings = longArrayOf(0, 20, 35, 18)
                val amplitudes = intArrayOf(0, 60, 0, 90)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.CRISP -> {
                val timings = longArrayOf(0, 30, 40, 25, 50, 75)
                val amplitudes = intArrayOf(0, 80, 0, 140, 0, 240)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.HEAVY -> {
                val timings = longArrayOf(0, 45, 30, 40, 40, 90)
                val amplitudes = intArrayOf(0, 140, 0, 200, 0, 255)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.DOUBLE_TAP -> {
                val timings = longArrayOf(0, 25, 35, 45)
                val amplitudes = intArrayOf(0, 160, 0, 240)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.TRIPLE_BURST -> {
                val timings = longArrayOf(0, 20, 25, 25, 25, 35)
                val amplitudes = intArrayOf(0, 120, 0, 180, 0, 240)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.CRESCENDO -> {
                val timings = longArrayOf(0, 25, 25, 40, 25, 80)
                val amplitudes = intArrayOf(0, 70, 0, 160, 0, 255)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.DYNAMIC_BUZZ -> {
                val timings = longArrayOf(0, 40, 20, 30, 20, 60)
                val amplitudes = intArrayOf(0, 240, 0, 150, 0, 220)
                vibrateWaveform(timings, amplitudes)
            }
        }
    }

    fun victoryPattern(type: VibrationType = VibrationType.CRISP) {
        if (vibrator?.hasVibrator() != true) return

        when (type) {
            VibrationType.SOFT -> {
                val timings = longArrayOf(0, 35, 45, 35, 45, 60)
                val amplitudes = intArrayOf(0, 60, 0, 90, 0, 130)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.CRISP -> {
                val timings = longArrayOf(0, 50, 60, 50, 60, 120)
                val amplitudes = intArrayOf(0, 100, 0, 160, 0, 255)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.HEAVY -> {
                val timings = longArrayOf(0, 70, 50, 70, 50, 150)
                val amplitudes = intArrayOf(0, 180, 0, 230, 0, 255)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.DOUBLE_TAP -> {
                val timings = longArrayOf(0, 40, 50, 40, 70, 50, 50, 70)
                val amplitudes = intArrayOf(0, 160, 0, 200, 0, 200, 0, 255)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.TRIPLE_BURST -> {
                val timings = longArrayOf(0, 25, 30, 25, 30, 35, 60, 30, 30, 30, 30, 50)
                val amplitudes = intArrayOf(0, 130, 0, 160, 0, 200, 0, 160, 0, 200, 0, 255)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.CRESCENDO -> {
                val timings = longArrayOf(0, 30, 25, 45, 25, 70, 35, 110)
                val amplitudes = intArrayOf(0, 60, 0, 120, 0, 190, 0, 255)
                vibrateWaveform(timings, amplitudes)
            }
            VibrationType.DYNAMIC_BUZZ -> {
                val timings = longArrayOf(0, 60, 30, 40, 30, 50, 30, 90)
                val amplitudes = intArrayOf(0, 255, 0, 140, 0, 220, 0, 255)
                vibrateWaveform(timings, amplitudes)
            }
        }
    }

    private fun vibrateWaveform(timings: LongArray, amplitudes: IntArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(timings, -1)
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
