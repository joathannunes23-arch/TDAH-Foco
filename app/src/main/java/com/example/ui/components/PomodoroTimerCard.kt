package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.NoiseType
import com.example.data.model.PomodoroMode

/**
 * Controlled PomodoroTimerCard wrapping the core PomodoroTimer component.
 */
@Composable
fun PomodoroTimerCard(
    timeLeftSeconds: Int,
    totalSeconds: Int,
    isRunning: Boolean,
    mode: PomodoroMode,
    workDurationMinutes: Int = 25,
    shortBreakDurationMinutes: Int = 5,
    longBreakDurationMinutes: Int = 15,
    autoNoiseActive: Boolean = false,
    selectedNoise: NoiseType = NoiseType.BROWN,
    completedCycles: Int = 0,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSelectMode: (PomodoroMode) -> Unit,
    onSetWorkDuration: (Int) -> Unit = {},
    onSetShortBreakDuration: (Int) -> Unit = {},
    onSetLongBreakDuration: (Int) -> Unit = {},
    onSkipSession: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    PomodoroTimer(
        timeLeftSeconds = timeLeftSeconds,
        totalSeconds = totalSeconds,
        isRunning = isRunning,
        mode = mode,
        workDurationMinutes = workDurationMinutes,
        shortBreakDurationMinutes = shortBreakDurationMinutes,
        longBreakDurationMinutes = longBreakDurationMinutes,
        autoNoiseActive = autoNoiseActive,
        selectedNoise = selectedNoise,
        completedCycles = completedCycles,
        onStart = onStart,
        onPause = onPause,
        onReset = onReset,
        onSelectMode = onSelectMode,
        onSetWorkDuration = onSetWorkDuration,
        onSetShortBreakDuration = onSetShortBreakDuration,
        onSetLongBreakDuration = onSetLongBreakDuration,
        onSkipSession = onSkipSession,
        modifier = modifier
    )
}

/**
 * Standalone PomodoroTimerCard with self-contained interval and timer state.
 */
@Composable
fun PomodoroTimerCard(
    modifier: Modifier = Modifier,
    initialWorkDurationMinutes: Int = 25,
    initialShortBreakDurationMinutes: Int = 5,
    initialLongBreakDurationMinutes: Int = 15,
    autoNoiseActive: Boolean = false,
    selectedNoise: NoiseType = NoiseType.BROWN,
    onSessionCompleted: ((PomodoroMode) -> Unit)? = null
) {
    PomodoroTimer(
        modifier = modifier,
        initialWorkDurationMinutes = initialWorkDurationMinutes,
        initialShortBreakDurationMinutes = initialShortBreakDurationMinutes,
        initialLongBreakDurationMinutes = initialLongBreakDurationMinutes,
        autoNoiseActive = autoNoiseActive,
        selectedNoise = selectedNoise,
        onSessionCompleted = onSessionCompleted
    )
}
