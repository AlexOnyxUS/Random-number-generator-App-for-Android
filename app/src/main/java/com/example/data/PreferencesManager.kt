package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.GradientTheme
import com.example.util.VibrationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "liquid_rng_settings")

data class SavedRangePreset(
    val id: String,
    val name: String,
    val min: String,
    val max: String,
    val batchCount: Int = 1,
    val uniqueOnly: Boolean = true
)

data class WheelSectorData(
    val id: Int,
    val text: String,
    val colorHex: Long
)

data class AppSettings(
    val gradientTheme: GradientTheme = GradientTheme.CYAN_NEON,
    val hapticsEnabled: Boolean = true,
    val vibrationType: VibrationType = VibrationType.CRISP,
    val shakeToGenerateEnabled: Boolean = true,
    val minValue: String = "1",
    val maxValue: String = "100",
    val batchCount: Int = 1,
    val uniqueOnly: Boolean = true,
    val savedPresets: List<SavedRangePreset> = emptyList(),
    val wheelSectors: List<WheelSectorData> = defaultWheelSectors(),
    val isPresetsInitialized: Boolean = false
)

fun defaultPresets(): List<SavedRangePreset> = listOf(
    SavedRangePreset("preset_1", "Игральный кубик", "1", "6"),
    SavedRangePreset("preset_2", "D&D D20", "1", "20"),
    SavedRangePreset("preset_3", "Проценты", "1", "100"),
    SavedRangePreset("preset_4", "Лотерея 6 из 49", "1", "49", batchCount = 6, uniqueOnly = true)
)

fun defaultWheelSectors(): List<WheelSectorData> = listOf(
    WheelSectorData(1, "Да", 0xFF00F0FF),
    WheelSectorData(2, "Нет", 0xFFF43F5E),
    WheelSectorData(3, "Возможно", 0xFFA855F7),
    WheelSectorData(4, "Точно да", 0xFF10B981),
    WheelSectorData(5, "Попробуй снова", 0xFFF59E0B),
    WheelSectorData(6, "Не сейчас", 0xFF3B82F6),
    WheelSectorData(7, "100%", 0xFF06B6D4),
    WheelSectorData(8, "Рискни!", 0xFFEC4899)
)

class PreferencesManager(private val context: Context) {

    private object Keys {
        val GRADIENT_THEME = stringPreferencesKey("gradient_theme")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val VIBRATION_TYPE = stringPreferencesKey("vibration_type")
        val SHAKE_ENABLED = booleanPreferencesKey("shake_enabled")
        val MIN_VALUE = stringPreferencesKey("min_value")
        val MAX_VALUE = stringPreferencesKey("max_value")
        val BATCH_COUNT = intPreferencesKey("batch_count")
        val UNIQUE_ONLY = booleanPreferencesKey("unique_only")
        val PRESETS_INITIALIZED = booleanPreferencesKey("presets_initialized")
        val SAVED_PRESETS_JSON = stringPreferencesKey("saved_presets_json")
        val WHEEL_SECTORS_JSON = stringPreferencesKey("wheel_sectors_json")
    }

    val appSettingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val themeName = prefs[Keys.GRADIENT_THEME] ?: GradientTheme.CYAN_NEON.name
        val theme = runCatching { GradientTheme.valueOf(themeName) }.getOrDefault(GradientTheme.CYAN_NEON)

        val vibrationName = prefs[Keys.VIBRATION_TYPE] ?: VibrationType.CRISP.name
        val vibrationType = VibrationType.fromId(vibrationName)

        val isInitialized = prefs[Keys.PRESETS_INITIALIZED] ?: false
        val presetsJson = prefs[Keys.SAVED_PRESETS_JSON]
        val presets = if (isInitialized) {
            if (!presetsJson.isNullOrBlank()) {
                parsePresetsJson(presetsJson)
            } else {
                emptyList()
            }
        } else {
            // First time launch: default presets
            defaultPresets()
        }

        val sectorsJson = prefs[Keys.WHEEL_SECTORS_JSON]
        val sectors = if (!sectorsJson.isNullOrBlank()) {
            parseSectorsJson(sectorsJson)
        } else {
            defaultWheelSectors()
        }

        AppSettings(
            gradientTheme = theme,
            hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
            vibrationType = vibrationType,
            shakeToGenerateEnabled = prefs[Keys.SHAKE_ENABLED] ?: true,
            minValue = prefs[Keys.MIN_VALUE] ?: "1",
            maxValue = prefs[Keys.MAX_VALUE] ?: "100",
            batchCount = prefs[Keys.BATCH_COUNT] ?: 1,
            uniqueOnly = prefs[Keys.UNIQUE_ONLY] ?: true,
            savedPresets = presets,
            wheelSectors = sectors,
            isPresetsInitialized = isInitialized
        )
    }

    suspend fun setGradientTheme(theme: GradientTheme) {
        context.dataStore.edit { it[Keys.GRADIENT_THEME] = theme.name }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun setVibrationType(type: VibrationType) {
        context.dataStore.edit { it[Keys.VIBRATION_TYPE] = type.name }
    }

    suspend fun setShakeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SHAKE_ENABLED] = enabled }
    }

    suspend fun saveNumberSettings(min: String, max: String, batch: Int, unique: Boolean) {
        context.dataStore.edit {
            it[Keys.MIN_VALUE] = min
            it[Keys.MAX_VALUE] = max
            it[Keys.BATCH_COUNT] = batch
            it[Keys.UNIQUE_ONLY] = unique
        }
    }

    suspend fun savePresets(presets: List<SavedRangePreset>) {
        val array = JSONArray()
        presets.forEach { preset ->
            val obj = JSONObject()
            obj.put("id", preset.id)
            obj.put("name", preset.name)
            obj.put("min", preset.min)
            obj.put("max", preset.max)
            obj.put("batch", preset.batchCount)
            obj.put("unique", preset.uniqueOnly)
            array.put(obj)
        }
        context.dataStore.edit {
            it[Keys.SAVED_PRESETS_JSON] = array.toString()
            it[Keys.PRESETS_INITIALIZED] = true
        }
    }

    suspend fun restoreDefaultPresets() {
        savePresets(defaultPresets())
    }

    suspend fun saveWheelSectors(sectors: List<WheelSectorData>) {
        val array = JSONArray()
        sectors.forEach { sector ->
            val obj = JSONObject()
            obj.put("id", sector.id)
            obj.put("text", sector.text)
            obj.put("color", sector.colorHex)
            array.put(obj)
        }
        context.dataStore.edit { it[Keys.WHEEL_SECTORS_JSON] = array.toString() }
    }

    private fun parsePresetsJson(json: String): List<SavedRangePreset> {
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<SavedRangePreset>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    SavedRangePreset(
                        id = obj.optString("id", "preset_$i"),
                        name = obj.optString("name", "Пресет"),
                        min = obj.optString("min", "1"),
                        max = obj.optString("max", "100"),
                        batchCount = obj.optInt("batch", 1),
                        uniqueOnly = obj.optBoolean("unique", true)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseSectorsJson(json: String): List<WheelSectorData> {
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<WheelSectorData>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    WheelSectorData(
                        id = obj.optInt("id", i + 1),
                        text = obj.optString("text", "Сектор ${i + 1}"),
                        colorHex = obj.optLong("color", 0xFF00F0FF)
                    )
                )
            }
            if (list.size < 2) defaultWheelSectors() else list
        } catch (e: Exception) {
            defaultWheelSectors()
        }
    }
}
