package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Battery-optimized SensorEventListener to detect phone shake events.
 * Automatically unregisters hardware listener when disabled or paused.
 */
class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastAcceleration = SensorManager.GRAVITY_EARTH
    private var currentAcceleration = SensorManager.GRAVITY_EARTH
    private val shakeThreshold = 12.5f
    private var lastShakeTimestamp = 0L

    private var isListening = false
    private var isPaused = false

    var isEnabled: Boolean = true
        set(value) {
            field = value
            updateSensorState()
        }

    fun start() {
        isPaused = false
        updateSensorState()
    }

    fun stop() {
        if (isListening) {
            sensorManager?.unregisterListener(this)
            isListening = false
        }
    }

    fun pause() {
        isPaused = true
        updateSensorState()
    }

    fun resume() {
        isPaused = false
        updateSensorState()
    }

    private fun updateSensorState() {
        if (isEnabled && !isPaused) {
            if (!isListening && accelerometer != null) {
                sensorManager?.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_UI
                )
                isListening = true
            }
        } else {
            stop()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isEnabled || isPaused || event == null) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        lastAcceleration = currentAcceleration
        currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta = currentAcceleration - lastAcceleration

        val now = System.currentTimeMillis()
        if (delta > shakeThreshold && (now - lastShakeTimestamp > 800L)) {
            lastShakeTimestamp = now
            onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
