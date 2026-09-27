package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * SensorEventListener to detect phone shake events.
 */
class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastAcceleration = SensorManager.GRAVITY_EARTH
    private var currentAcceleration = SensorManager.GRAVITY_EARTH
    private var shakeThreshold = 12.5f // sensitivity threshold
    private var lastShakeTimestamp = 0L

    var isEnabled: Boolean = true

    fun start() {
        if (accelerometer != null) {
            sensorManager?.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isEnabled || event == null) return

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
