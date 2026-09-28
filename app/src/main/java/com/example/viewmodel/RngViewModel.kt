package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PreferencesManager
import com.example.data.SavedRangePreset
import com.example.data.WheelSectorData
import com.example.data.defaultPresets
import com.example.data.defaultWheelSectors
import com.example.sensor.ShakeDetector
import com.example.ui.theme.GradientTheme
import com.example.util.VibrationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.SecureRandom
import java.util.UUID
import kotlin.random.Random

enum class AppTab(val title: String) {
    NUMBERS("Числа"),
    COIN("Монетка"),
    WHEEL("Колесо")
}

enum class CoinSide(val title: String) {
    HEADS("Орёл"),
    TAILS("Решка")
}

data class GenerationHistory(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class MainUiState(
    val currentTab: AppTab = AppTab.NUMBERS,
    val gradientTheme: GradientTheme = GradientTheme.CYAN_NEON,
    val hapticsEnabled: Boolean = true,
    val shakeEnabled: Boolean = true,

    // Number generator (Bounded)
    val minInput: String = "1",
    val maxInput: String = "100",
    val batchCount: Int = 1,
    val uniqueOnly: Boolean = true,
    val numberResults: List<String> = listOf("42"),
    val displayNumbers: List<String> = listOf("42"),
    val isNumberRolling: Boolean = false,
    val numberErrorMessage: String? = null,
    val savedPresets: List<SavedRangePreset> = emptyList(),
    val showEasterEgg67: Boolean = false,

    // Coin flipper
    val coinResult: CoinSide = CoinSide.HEADS,
    val isCoinFlipping: Boolean = false,
    val headsCount: Int = 0,
    val tailsCount: Int = 0,

    // Wheel of fortune
    val wheelSectors: List<WheelSectorData> = defaultWheelSectors(),
    val isWheelSpinning: Boolean = false,
    val targetWheelRotation: Float = 0f,
    val winningSector: WheelSectorData? = null,

    // History
    val history: List<GenerationHistory> = emptyList()
)

sealed interface UiEvent {
    data class ShowToast(val message: String) : UiEvent
    data object TriggerTick : UiEvent
    data object TriggerCoinLanding : UiEvent
    data object TriggerWheelNotch : UiEvent
    data object TriggerVictory : UiEvent
}

class RngViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val vibrationHelper = VibrationHelper(application)
    private val secureRandom = SecureRandom()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    private var rollJob: Job? = null
    private var coinJob: Job? = null
    private var wheelJob: Job? = null

    private val shakeDetector = ShakeDetector(application) {
        onDeviceShaken()
    }

    init {
        shakeDetector.start()

        viewModelScope.launch {
            preferencesManager.appSettingsFlow.collect { settings ->
                _uiState.update { current ->
                    current.copy(
                        gradientTheme = settings.gradientTheme,
                        hapticsEnabled = settings.hapticsEnabled,
                        shakeEnabled = settings.shakeToGenerateEnabled,
                        minInput = settings.minValue,
                        maxInput = settings.maxValue,
                        batchCount = settings.batchCount,
                        uniqueOnly = settings.uniqueOnly,
                        savedPresets = settings.savedPresets,
                        wheelSectors = settings.wheelSectors
                    )
                }
                shakeDetector.isEnabled = settings.shakeToGenerateEnabled
                if (!settings.isPresetsInitialized) {
                    preferencesManager.savePresets(defaultPresets())
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        shakeDetector.stop()
    }

    private fun onDeviceShaken() {
        if (!_uiState.value.shakeEnabled) return

        when (_uiState.value.currentTab) {
            AppTab.NUMBERS -> generateNumbers(animate = true)
            AppTab.COIN -> flipCoin()
            AppTab.WHEEL -> spinWheel()
        }
    }

    fun setTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    // Number generator limits apply immediately
    fun updateMinInput(min: String) {
        _uiState.update { it.copy(minInput = min.trim(), numberErrorMessage = null) }
        saveNumberSettings()
    }

    fun updateMaxInput(max: String) {
        _uiState.update { it.copy(maxInput = max.trim(), numberErrorMessage = null) }
        saveNumberSettings()
    }

    fun updateBatchCount(batch: Int) {
        _uiState.update { it.copy(batchCount = batch.coerceIn(1, 10), numberErrorMessage = null) }
        saveNumberSettings()
    }

    fun toggleUniqueOnly(unique: Boolean) {
        _uiState.update { it.copy(uniqueOnly = unique, numberErrorMessage = null) }
        saveNumberSettings()
    }

    // Save and load presets
    fun saveCurrentRangeAsPreset(name: String) {
        val state = _uiState.value
        val cleanName = name.trim().ifEmpty { "Диапазон ${state.minInput} - ${state.maxInput}" }
        val newPreset = SavedRangePreset(
            id = UUID.randomUUID().toString(),
            name = cleanName,
            min = state.minInput,
            max = state.maxInput,
            batchCount = state.batchCount,
            uniqueOnly = state.uniqueOnly
        )

        val updatedList = listOf(newPreset) + state.savedPresets
        _uiState.update { it.copy(savedPresets = updatedList) }
        viewModelScope.launch {
            preferencesManager.savePresets(updatedList)
            _uiEvents.emit(UiEvent.ShowToast("Пресет «$cleanName» сохранён!"))
        }
    }

    fun loadPreset(preset: SavedRangePreset) {
        _uiState.update {
            it.copy(
                minInput = preset.min,
                maxInput = preset.max,
                batchCount = preset.batchCount,
                uniqueOnly = preset.uniqueOnly,
                numberErrorMessage = null
            )
        }
        saveNumberSettings()
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.ShowToast("Загружен пресет: ${preset.name}"))
        }
    }

    fun deletePreset(presetId: String) {
        val updated = _uiState.value.savedPresets.filterNot { it.id == presetId }
        _uiState.update { it.copy(savedPresets = updated) }
        viewModelScope.launch {
            preferencesManager.savePresets(updated)
        }
    }

    fun restoreDefaultPresets() {
        viewModelScope.launch {
            preferencesManager.restoreDefaultPresets()
            _uiEvents.emit(UiEvent.ShowToast("Стандартные пресеты восстановлены"))
        }
    }

    private fun saveNumberSettings() {
        val state = _uiState.value
        viewModelScope.launch {
            preferencesManager.saveNumberSettings(
                min = state.minInput,
                max = state.maxInput,
                batch = state.batchCount,
                unique = state.uniqueOnly
            )
        }
    }

    fun generateNumbers(animate: Boolean = true) {
        if (_uiState.value.isNumberRolling) return

        val state = _uiState.value
        val minBig: BigInteger
        val maxBig: BigInteger

        try {
            minBig = BigInteger(state.minInput.trim())
        } catch (e: Exception) {
            _uiState.update { it.copy(numberErrorMessage = "Неверное значение «От (Min)»") }
            return
        }

        try {
            maxBig = BigInteger(state.maxInput.trim())
        } catch (e: Exception) {
            _uiState.update { it.copy(numberErrorMessage = "Неверное значение «До (Max)»") }
            return
        }

        if (minBig > maxBig) {
            _uiState.update { it.copy(numberErrorMessage = "«От» не может быть больше «До»") }
            return
        }

        val range = maxBig.subtract(minBig).add(BigInteger.ONE)
        val batch = state.batchCount
        val unique = state.uniqueOnly

        if (unique && batch > 1 && range < BigInteger.valueOf(batch.toLong())) {
            _uiState.update {
                it.copy(numberErrorMessage = "Диапазон меньше запрошенного числа уникальных значений!")
            }
            return
        }

        val results = mutableListOf<String>()
        val uniqueSet = mutableSetOf<BigInteger>()

        while (results.size < batch) {
            val candidate = randomBigIntegerInRange(minBig, maxBig, range)
            if (unique) {
                if (uniqueSet.add(candidate)) results.add(candidate.toString())
            } else {
                results.add(candidate.toString())
            }
        }

        _uiState.update { it.copy(numberErrorMessage = null) }

        if (!animate) {
            _uiState.update {
                it.copy(
                    numberResults = results,
                    displayNumbers = results,
                    isNumberRolling = false
                )
            }
            return
        }

        rollJob?.cancel()
        rollJob = viewModelScope.launch {
            _uiState.update { it.copy(isNumberRolling = true) }

            val rollTicks = 9
            val targetLengths = results.map { it.length }

            for (tick in 0 until rollTicks) {
                val scrambled = targetLengths.map { len ->
                    val displayLen = len.coerceAtMost(16)
                    buildString {
                        for (i in 0 until displayLen) append(secureRandom.nextInt(10))
                    }
                }
                _uiState.update { it.copy(displayNumbers = scrambled) }
                triggerVibrationTick()
                delay(35L + tick * 8L)
            }

            val histItem = GenerationHistory(
                title = "Числа [$minBig … $maxBig]",
                result = results.joinToString(", ")
            )

            val has67 = results.contains("67")
            _uiState.update { current ->
                current.copy(
                    numberResults = results,
                    displayNumbers = results,
                    isNumberRolling = false,
                    history = (listOf(histItem) + current.history).take(40),
                    showEasterEgg67 = has67
                )
            }

            triggerVictoryHaptic()
        }
    }

    fun dismissEasterEgg67() {
        _uiState.update { it.copy(showEasterEgg67 = false) }
    }

    fun pauseSensors() {
        shakeDetector.pause()
    }

    fun resumeSensors() {
        shakeDetector.resume()
    }

    private fun randomBigIntegerInRange(min: BigInteger, max: BigInteger, range: BigInteger): BigInteger {
        if (range == BigInteger.ONE) return min
        val bitLength = range.bitLength()
        while (true) {
            val candidate = BigInteger(bitLength, secureRandom)
            if (candidate < range) return min.add(candidate)
        }
    }

    // Clean coin flipping logic
    fun flipCoin() {
        if (_uiState.value.isCoinFlipping) return

        coinJob?.cancel()
        coinJob = viewModelScope.launch {
            val chosenSide = if (secureRandom.nextBoolean()) CoinSide.HEADS else CoinSide.TAILS

            _uiState.update {
                it.copy(isCoinFlipping = true)
            }

            // Quick vibration ticks during flight
            for (i in 0 until 7) {
                triggerVibrationTick()
                delay(85L)
            }

            delay(200L)

            val newHeads = _uiState.value.headsCount + if (chosenSide == CoinSide.HEADS) 1 else 0
            val newTails = _uiState.value.tailsCount + if (chosenSide == CoinSide.TAILS) 1 else 0

            val histItem = GenerationHistory(
                title = "Монетка",
                result = chosenSide.title
            )

            _uiState.update {
                it.copy(
                    coinResult = chosenSide,
                    isCoinFlipping = false,
                    headsCount = newHeads,
                    tailsCount = newTails,
                    history = (listOf(histItem) + it.history).take(40)
                )
            }

            triggerCoinLandingHaptic()
        }
    }

    fun resetCoinStats() {
        _uiState.update { it.copy(headsCount = 0, tailsCount = 0) }
    }

    // Wheel of Fortune: User full control of sections and count
    fun addWheelSector(text: String) {
        val sectors = _uiState.value.wheelSectors
        if (sectors.size >= 24) {
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.ShowToast("Максимум 24 сектора на колесе"))
            }
            return
        }

        val colors = listOf(
            0xFF00F0FF, 0xFFF43F5E, 0xFFA855F7, 0xFF10B981,
            0xFFF59E0B, 0xFF3B82F6, 0xFF06B6D4, 0xFFEC4899,
            0xFF8B5CF6, 0xFF14B8A6, 0xFFE11D48, 0xFFEAB308
        )
        val nextId = (sectors.maxOfOrNull { it.id } ?: 0) + 1
        val colorHex = colors[sectors.size % colors.size]

        val updated = sectors + WheelSectorData(nextId, text.trim().ifEmpty { "Сектор $nextId" }, colorHex)
        _uiState.update { it.copy(wheelSectors = updated) }
        viewModelScope.launch {
            preferencesManager.saveWheelSectors(updated)
        }
    }

    fun removeWheelSector(sectorId: Int) {
        val sectors = _uiState.value.wheelSectors
        if (sectors.size <= 2) {
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.ShowToast("Минимум 2 сектора на колесе"))
            }
            return
        }
        val updated = sectors.filterNot { it.id == sectorId }
        _uiState.update { it.copy(wheelSectors = updated) }
        viewModelScope.launch {
            preferencesManager.saveWheelSectors(updated)
        }
    }

    fun resetWheelSectorsToDefault() {
        val defaultList = defaultWheelSectors()
        _uiState.update { it.copy(wheelSectors = defaultList) }
        viewModelScope.launch {
            preferencesManager.saveWheelSectors(defaultList)
            _uiEvents.emit(UiEvent.ShowToast("Колесо сброшено к стандартным секторам"))
        }
    }

    fun setWheelPreset(presetName: String) {
        val preset = when (presetName) {
            "YES_NO" -> listOf(
                WheelSectorData(1, "Да", 0xFF10B981),
                WheelSectorData(2, "Нет", 0xFFF43F5E)
            )
            "DICE_6" -> (1..6).map {
                val colors = listOf(0xFF00F0FF, 0xFFA855F7, 0xFFF59E0B, 0xFF10B981, 0xFF3B82F6, 0xFFEC4899)
                WheelSectorData(it, "$it", colors[it - 1])
            }
            "PRIZES" -> listOf(
                WheelSectorData(1, "Суперприз", 0xFFF59E0B),
                WheelSectorData(2, "Пропуск", 0xFF64748B),
                WheelSectorData(3, "+100 Очков", 0xFF10B981),
                WheelSectorData(4, "Сектор Банкрот", 0xFFF43F5E),
                WheelSectorData(5, "+500 Очков", 0xFF00F0FF),
                WheelSectorData(6, "Ещё попытка", 0xFFA855F7)
            )
            else -> defaultWheelSectors()
        }

        _uiState.update { it.copy(wheelSectors = preset) }
        viewModelScope.launch {
            preferencesManager.saveWheelSectors(preset)
        }
    }

    fun spinWheel() {
        if (_uiState.value.isWheelSpinning) return

        val sectors = _uiState.value.wheelSectors
        if (sectors.isEmpty()) return

        wheelJob?.cancel()
        wheelJob = viewModelScope.launch {
            _uiState.update { it.copy(isWheelSpinning = true, winningSector = null) }

            val sectorCount = sectors.size
            val sectorAngle = 360f / sectorCount
            val winningIndex = Random.nextInt(sectorCount)
            val selectedSector = sectors[winningIndex]

            // Calculate precise rotation so sector center is exactly at 270° (top)
            val sectorCenterAngle = (winningIndex + 0.5f) * sectorAngle
            val targetAngle = (270f - sectorCenterAngle + 3600f) % 360f

            val currentRot = _uiState.value.targetWheelRotation
            val currentMod = (currentRot % 360f + 360f) % 360f
            val diff = (targetAngle - currentMod + 360f) % 360f
            val extraSpins = (5 + Random.nextInt(3)) * 360f
            val finalTarget = currentRot + extraSpins + diff

            _uiState.update { it.copy(targetWheelRotation = finalTarget) }

            val totalSpinMs = 3000L
            val intervals = 24
            for (i in 0 until intervals) {
                triggerWheelNotchHaptic()
                delay(60L + i * 8L)
            }

            val histItem = GenerationHistory(
                title = "Колесо (${sectors.size} секторов)",
                result = selectedSector.text
            )

            _uiState.update {
                it.copy(
                    isWheelSpinning = false,
                    winningSector = selectedSector,
                    history = (listOf(histItem) + it.history).take(40)
                )
            }

            triggerVictoryHaptic()
        }
    }

    // Vibration triggers with vibration helper
    private fun triggerVibrationTick() {
        if (_uiState.value.hapticsEnabled) {
            vibrationHelper.tick()
        }
    }

    private fun triggerCoinLandingHaptic() {
        if (_uiState.value.hapticsEnabled) {
            vibrationHelper.coinLandingPattern()
        }
    }

    private fun triggerWheelNotchHaptic() {
        if (_uiState.value.hapticsEnabled) {
            vibrationHelper.wheelNotchTick()
        }
    }

    private fun triggerVictoryHaptic() {
        if (_uiState.value.hapticsEnabled) {
            vibrationHelper.victoryPattern()
        }
    }

    fun setGradientTheme(theme: GradientTheme) {
        viewModelScope.launch {
            preferencesManager.setGradientTheme(theme)
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setHapticsEnabled(enabled)
        }
    }

    fun setShakeEnabled(enabled: Boolean) {
        shakeDetector.isEnabled = enabled
        viewModelScope.launch {
            preferencesManager.setShakeEnabled(enabled)
        }
    }

    fun copyToClipboard(text: String, label: String = "Результат") {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        viewModelScope.launch {
            _uiEvents.emit(UiEvent.ShowToast("Скопировано: $text"))
        }
    }

    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }
}
