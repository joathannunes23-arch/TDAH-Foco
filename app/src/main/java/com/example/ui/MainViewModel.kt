package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.audio.NoiseSynthesizer
import com.example.data.model.MindMap
import com.example.data.model.NoiseSession
import com.example.data.model.NoiseType
import com.example.data.model.PomodoroMode
import com.example.data.model.RoutineTask
import com.example.data.model.SubscriptionInfo
import com.example.data.model.SubscriptionPlanStatus
import com.example.data.repository.AdhdRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppNavigationTab(val label: String, val iconName: String) {
    DASHBOARD("Início", "dashboard"),
    MIND_MAPS("Mapas", "mindmap"),
    NOISE_PLAYER("Ruídos", "noise"),
    ROUTINE("Rotina", "routine"),
    SETTINGS("Ajustes", "settings"),
    SUBSCRIPTION("Assinatura", "subscription")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AdhdRepository()
    private val noiseSynthesizer = NoiseSynthesizer()

    // Navigation
    private val _currentTab = MutableStateFlow(AppNavigationTab.DASHBOARD)
    val currentTab: StateFlow<AppNavigationTab> = _currentTab.asStateFlow()

    fun navigateTo(tab: AppNavigationTab) {
        _currentTab.value = tab
    }

    // Repository flows
    val userName: StateFlow<String> = repository.userName
    val activeMindMap: StateFlow<MindMap> = repository.activeMindMap
    val libraryMaps: StateFlow<List<MindMap>> = repository.libraryMaps
    val routineTasks: StateFlow<List<RoutineTask>> = repository.routineTasks
    val historySessions: StateFlow<List<NoiseSession>> = repository.historySessions
    val autoNoisePomodoro: StateFlow<Boolean> = repository.autoNoisePomodoro
    val selectedPomodoroNoise: StateFlow<NoiseType> = repository.selectedPomodoroNoise

    // Audio synthesizer flows
    val currentNoise: StateFlow<NoiseType> = noiseSynthesizer.currentNoise
    val isNoisePlaying: StateFlow<Boolean> = noiseSynthesizer.isPlaying
    val noiseVolume: StateFlow<Float> = noiseSynthesizer.volume

    // Subscription Pro state (Freemium: 7-day trial, R$ 9,90/mês)
    private val _subscriptionInfo = MutableStateFlow(
        SubscriptionInfo(
            isPro = true,
            status = SubscriptionPlanStatus.FREE_TRIAL,
            daysLeft = 7,
            priceFormatted = "R$ 9,90",
            period = "mês"
        )
    )
    val subscriptionInfo: StateFlow<SubscriptionInfo> = _subscriptionInfo.asStateFlow()

    // Feedback Toast / Banner message
    private val _feedbackMessage = MutableStateFlow<String?>("Bem-vindo ao TDAH Foco! Seu mapa e ruídos estão prontos.")
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    fun clearFeedbackMessage() {
        _feedbackMessage.value = null
    }

    fun showFeedback(message: String) {
        _feedbackMessage.value = message
    }

    // Pomodoro state
    private val _pomodoroMode = MutableStateFlow(PomodoroMode.FOCUS)
    val pomodoroMode: StateFlow<PomodoroMode> = _pomodoroMode.asStateFlow()

    private val _workDurationMinutes = MutableStateFlow(25)
    val workDurationMinutes: StateFlow<Int> = _workDurationMinutes.asStateFlow()

    private val _shortBreakDurationMinutes = MutableStateFlow(5)
    val shortBreakDurationMinutes: StateFlow<Int> = _shortBreakDurationMinutes.asStateFlow()

    private val _longBreakDurationMinutes = MutableStateFlow(15)
    val longBreakDurationMinutes: StateFlow<Int> = _longBreakDurationMinutes.asStateFlow()

    private val _pomodoroTimeLeftSeconds = MutableStateFlow(25 * 60)
    val pomodoroTimeLeftSeconds: StateFlow<Int> = _pomodoroTimeLeftSeconds.asStateFlow()

    private val _pomodoroTotalSeconds = MutableStateFlow(25 * 60)
    val pomodoroTotalSeconds: StateFlow<Int> = _pomodoroTotalSeconds.asStateFlow()

    private val _isPomodoroRunning = MutableStateFlow(false)
    val isPomodoroRunning: StateFlow<Boolean> = _isPomodoroRunning.asStateFlow()

    private val _pomodoroCompletedCycles = MutableStateFlow(0)
    val pomodoroCompletedCycles: StateFlow<Int> = _pomodoroCompletedCycles.asStateFlow()

    private var pomodoroJob: Job? = null
    private var sessionElapsedSeconds = 0

    // Theme Mode
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // Noise actions
    fun playNoise(type: NoiseType) {
        if (noiseSynthesizer.isPlaying.value && noiseSynthesizer.currentNoise.value == type) {
            noiseSynthesizer.stop()
            showFeedback("Ruído pausado.")
        } else {
            noiseSynthesizer.start(type)
            showFeedback("${type.displayName} ativado para regulação auditiva.")
        }
    }

    fun toggleNoisePlayback() {
        if (noiseSynthesizer.isPlaying.value) {
            noiseSynthesizer.stop()
            showFeedback("Ruído pausado.")
        } else {
            val typeToPlay = if (noiseSynthesizer.currentNoise.value != NoiseType.NONE) {
                noiseSynthesizer.currentNoise.value
            } else {
                NoiseType.BROWN
            }
            noiseSynthesizer.start(typeToPlay)
            showFeedback("${typeToPlay.displayName} em reprodução.")
        }
    }

    fun setNoiseVolume(vol: Float) {
        noiseSynthesizer.setVolume(vol)
    }

    fun applyMixedNoisePreset() {
        noiseSynthesizer.start(NoiseType.MIXED)
        showFeedback("Preset ativado: 70% Ruído Rosa + 30% Ruído Marrom.")
    }

    // Pomodoro actions
    fun startPomodoro() {
        if (_isPomodoroRunning.value) return
        _isPomodoroRunning.value = true

        if (repository.autoNoisePomodoro.value) {
            noiseSynthesizer.start(repository.selectedPomodoroNoise.value)
        }

        showFeedback("Sessão Pomodoro iniciada! Bom foco neurodivergente.")

        pomodoroJob = viewModelScope.launch {
            while (isActive && _isPomodoroRunning.value && _pomodoroTimeLeftSeconds.value > 0) {
                delay(1000)
                _pomodoroTimeLeftSeconds.value -= 1
                sessionElapsedSeconds += 1
            }

            if (_pomodoroTimeLeftSeconds.value <= 0) {
                // Completed
                _isPomodoroRunning.value = false
                val minutesSpent = sessionElapsedSeconds / 60
                repository.recordSession(
                    noiseType = noiseSynthesizer.currentNoise.value,
                    minutes = if (minutesSpent > 0) minutesSpent else 1,
                    wasPomodoro = true
                )
                sessionElapsedSeconds = 0

                if (_pomodoroMode.value == PomodoroMode.FOCUS) {
                    _pomodoroCompletedCycles.value += 1
                    val isLongBreak = _pomodoroCompletedCycles.value % 4 == 0
                    if (isLongBreak) {
                        val breakMin = _longBreakDurationMinutes.value
                        showFeedback("🎉 Excelente! 4 blocos de foco completos! Pausa longa de $breakMin min.")
                        setPomodoroMode(PomodoroMode.LONG_BREAK)
                    } else {
                        val breakMin = _shortBreakDurationMinutes.value
                        showFeedback("🎉 Bloco de foco concluído! Pausa curta de $breakMin min.")
                        setPomodoroMode(PomodoroMode.SHORT_BREAK)
                    }
                } else {
                    val workMin = _workDurationMinutes.value
                    showFeedback("Pausa finalizada! Pronto para o próximo foco ($workMin min).")
                    setPomodoroMode(PomodoroMode.FOCUS)
                }
            }
        }
    }

    fun skipPomodoroSession() {
        pausePomodoro()
        if (_pomodoroMode.value == PomodoroMode.FOCUS) {
            _pomodoroCompletedCycles.value += 1
            val isLongBreak = _pomodoroCompletedCycles.value % 4 == 0
            val nextMode = if (isLongBreak) PomodoroMode.LONG_BREAK else PomodoroMode.SHORT_BREAK
            setPomodoroMode(nextMode)
            showFeedback("Avançado para ${nextMode.label}.")
        } else {
            setPomodoroMode(PomodoroMode.FOCUS)
            showFeedback("Avançado para Foco Total.")
        }
    }

    fun pausePomodoro() {
        _isPomodoroRunning.value = false
        pomodoroJob?.cancel()
        pomodoroJob = null
        if (noiseSynthesizer.isPlaying.value) {
            noiseSynthesizer.stop()
        }
        showFeedback("Pomodoro pausado.")
    }

    fun getDurationForMode(mode: PomodoroMode): Int {
        return when (mode) {
            PomodoroMode.FOCUS -> _workDurationMinutes.value
            PomodoroMode.SHORT_BREAK -> _shortBreakDurationMinutes.value
            PomodoroMode.LONG_BREAK -> _longBreakDurationMinutes.value
        }
    }

    fun resetPomodoro() {
        pausePomodoro()
        val defaultSec = getDurationForMode(_pomodoroMode.value) * 60
        _pomodoroTimeLeftSeconds.value = defaultSec
        _pomodoroTotalSeconds.value = defaultSec
        sessionElapsedSeconds = 0
    }

    fun setPomodoroMode(mode: PomodoroMode) {
        pausePomodoro()
        _pomodoroMode.value = mode
        val sec = getDurationForMode(mode) * 60
        _pomodoroTimeLeftSeconds.value = sec
        _pomodoroTotalSeconds.value = sec
        sessionElapsedSeconds = 0
    }

    fun setWorkDuration(minutes: Int) {
        val clamped = minutes.coerceIn(1, 120)
        _workDurationMinutes.value = clamped
        if (_pomodoroMode.value == PomodoroMode.FOCUS && !_isPomodoroRunning.value) {
            val sec = clamped * 60
            _pomodoroTimeLeftSeconds.value = sec
            _pomodoroTotalSeconds.value = sec
        }
        showFeedback("Intervalo de foco ajustado para $clamped min.")
    }

    fun setShortBreakDuration(minutes: Int) {
        val clamped = minutes.coerceIn(1, 60)
        _shortBreakDurationMinutes.value = clamped
        if (_pomodoroMode.value == PomodoroMode.SHORT_BREAK && !_isPomodoroRunning.value) {
            val sec = clamped * 60
            _pomodoroTimeLeftSeconds.value = sec
            _pomodoroTotalSeconds.value = sec
        }
        showFeedback("Intervalo de pausa curta ajustado para $clamped min.")
    }

    fun setLongBreakDuration(minutes: Int) {
        val clamped = minutes.coerceIn(1, 60)
        _longBreakDurationMinutes.value = clamped
        if (_pomodoroMode.value == PomodoroMode.LONG_BREAK && !_isPomodoroRunning.value) {
            val sec = clamped * 60
            _pomodoroTimeLeftSeconds.value = sec
            _pomodoroTotalSeconds.value = sec
        }
        showFeedback("Intervalo de pausa longa ajustado para $clamped min.")
    }

    // Mind map actions
    fun selectMindMap(mapId: String) {
        repository.selectActiveMap(mapId)
        showFeedback("Mapa carregado com evidência científica.")
    }

    fun toggleNodeCompletion(nodeId: String) {
        repository.toggleNodeCompletion(nodeId)
    }

    fun addNodeToActiveMap(text: String, icon: String = "💡", tag: String = "Passo") {
        if (text.isNotBlank()) {
            repository.addNodeToActiveMap(text, icon, tag)
            showFeedback("Caixa adicionada ao mapa mental.")
        }
    }

    fun removeNodeFromActiveMap(nodeId: String) {
        repository.removeNodeFromActiveMap(nodeId)
        showFeedback("Caixa removida do mapa.")
    }

    fun updateMapBackgroundColor(colorHex: String) {
        repository.updateActiveMapBackground(colorHex)
    }

    fun toggleFavorite(mapId: String) {
        repository.toggleFavorite(mapId)
    }

    fun generateScientificMindMap(theme: String) {
        val newMap = repository.generateScientificMindMap(theme)
        showFeedback("Mapa gerado com sucesso baseado em estudos de neurociência de 2024!")
    }

    // Routine actions
    fun toggleRoutineTask(taskId: String) {
        repository.toggleTask(taskId)
    }

    fun addRoutineTask(title: String, period: String = "Manhã", icon: String = "⚡") {
        if (title.isNotBlank()) {
            repository.addTask(title, period, icon)
            showFeedback("Item adicionado à rotina diária.")
        }
    }

    fun deleteRoutineTask(taskId: String) {
        repository.deleteTask(taskId)
    }

    // Settings actions
    fun updateUserName(name: String) {
        repository.setUserName(name)
        showFeedback("Nome atualizado com sucesso!")
    }

    fun setAutoNoiseWithPomodoro(enabled: Boolean) {
        repository.setAutoNoisePomodoro(enabled)
    }

    fun setSelectedPomodoroNoise(noiseType: NoiseType) {
        repository.setSelectedPomodoroNoise(noiseType)
    }

    // Subscription actions
    fun activatePro() {
        _subscriptionInfo.value = _subscriptionInfo.value.copy(
            isPro = true,
            status = SubscriptionPlanStatus.PRO_ACTIVE,
            daysLeft = 30
        )
        showFeedback("Parabéns! Assinatura Pro ativada com sucesso.")
    }

    fun startFreeTrial() {
        _subscriptionInfo.value = _subscriptionInfo.value.copy(
            isPro = true,
            status = SubscriptionPlanStatus.FREE_TRIAL,
            daysLeft = 7
        )
        showFeedback("Teste grátis de 7 dias ativado! Aproveite todos os recursos Pro.")
    }

    fun cancelSubscription() {
        _subscriptionInfo.value = _subscriptionInfo.value.copy(
            isPro = false,
            status = SubscriptionPlanStatus.EXPIRED,
            daysLeft = 0
        )
        showFeedback("Assinatura cancelada. Você agora está no plano gratuito.")
    }

    fun openSubscriptionScreen() {
        navigateTo(AppNavigationTab.SUBSCRIPTION)
    }

    override fun onCleared() {
        super.onCleared()
        noiseSynthesizer.stop()
        pomodoroJob?.cancel()
    }
}
