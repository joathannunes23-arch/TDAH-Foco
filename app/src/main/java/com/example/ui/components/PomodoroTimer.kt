package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoiseType
import com.example.data.model.PomodoroMode
import com.example.ui.theme.AquaPrimary
import com.example.ui.theme.CalmMint
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.WarmAmber
import com.example.ui.theme.WarmCoral
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Quick preset for ADHD-friendly intervals.
 */
data class PomodoroIntervalPreset(
    val name: String,
    val icon: String,
    val subtitle: String,
    val workMinutes: Int,
    val shortBreakMinutes: Int,
    val longBreakMinutes: Int
)

val ADHD_POMODORO_PRESETS = listOf(
    PomodoroIntervalPreset(
        name = "Clássico",
        icon = "🍅",
        subtitle = "Padrão comprovado",
        workMinutes = 25,
        shortBreakMinutes = 5,
        longBreakMinutes = 15
    ),
    PomodoroIntervalPreset(
        name = "Sprint TDAH",
        icon = "⚡",
        subtitle = "Para vencer a inércia",
        workMinutes = 15,
        shortBreakMinutes = 3,
        longBreakMinutes = 10
    ),
    PomodoroIntervalPreset(
        name = "Deep Work",
        icon = "🧠",
        subtitle = "Hiperfoco imersivo",
        workMinutes = 50,
        shortBreakMinutes = 10,
        longBreakMinutes = 20
    ),
    PomodoroIntervalPreset(
        name = "Fluxo Suave",
        icon = "🌿",
        subtitle = "Ritmo tranquilo",
        workMinutes = 20,
        shortBreakMinutes = 5,
        longBreakMinutes = 15
    )
)

/**
 * Standalone Pomodoro Timer Component with self-contained state and ticker.
 * Can be dropped into any screen without requiring an external ViewModel.
 */
@Composable
fun PomodoroTimer(
    modifier: Modifier = Modifier,
    initialWorkDurationMinutes: Int = 25,
    initialShortBreakDurationMinutes: Int = 5,
    initialLongBreakDurationMinutes: Int = 15,
    autoNoiseActive: Boolean = false,
    selectedNoise: NoiseType = NoiseType.BROWN,
    onSessionCompleted: ((PomodoroMode) -> Unit)? = null
) {
    var workDuration by remember { mutableIntStateOf(initialWorkDurationMinutes) }
    var shortBreakDuration by remember { mutableIntStateOf(initialShortBreakDurationMinutes) }
    var longBreakDuration by remember { mutableIntStateOf(initialLongBreakDurationMinutes) }

    var mode by remember { mutableStateOf(PomodoroMode.FOCUS) }
    var isRunning by remember { mutableStateOf(false) }

    val initialDurationSec = workDuration * 60
    var timeLeftSeconds by remember { mutableIntStateOf(initialDurationSec) }
    var totalSeconds by remember { mutableIntStateOf(initialDurationSec) }
    var completedCycles by remember { mutableIntStateOf(0) }

    fun getDurationFor(m: PomodoroMode): Int {
        return when (m) {
            PomodoroMode.FOCUS -> workDuration * 60
            PomodoroMode.SHORT_BREAK -> shortBreakDuration * 60
            PomodoroMode.LONG_BREAK -> longBreakDuration * 60
        }
    }

    LaunchedEffect(isRunning, timeLeftSeconds) {
        if (isRunning && timeLeftSeconds > 0) {
            delay(1000L)
            timeLeftSeconds--
        } else if (isRunning && timeLeftSeconds <= 0) {
            isRunning = false
            onSessionCompleted?.invoke(mode)
            if (mode == PomodoroMode.FOCUS) {
                completedCycles++
                val nextMode = if (completedCycles % 4 == 0) PomodoroMode.LONG_BREAK else PomodoroMode.SHORT_BREAK
                mode = nextMode
                val nextSec = getDurationFor(nextMode)
                timeLeftSeconds = nextSec
                totalSeconds = nextSec
            } else {
                mode = PomodoroMode.FOCUS
                val nextSec = getDurationFor(PomodoroMode.FOCUS)
                timeLeftSeconds = nextSec
                totalSeconds = nextSec
            }
        }
    }

    PomodoroTimer(
        timeLeftSeconds = timeLeftSeconds,
        totalSeconds = totalSeconds,
        isRunning = isRunning,
        mode = mode,
        workDurationMinutes = workDuration,
        shortBreakDurationMinutes = shortBreakDuration,
        longBreakDurationMinutes = longBreakDuration,
        autoNoiseActive = autoNoiseActive,
        selectedNoise = selectedNoise,
        completedCycles = completedCycles,
        onStart = {
            if (timeLeftSeconds <= 0) {
                val sec = getDurationFor(mode)
                timeLeftSeconds = sec
                totalSeconds = sec
            }
            isRunning = true
        },
        onPause = { isRunning = false },
        onReset = {
            isRunning = false
            val sec = getDurationFor(mode)
            timeLeftSeconds = sec
            totalSeconds = sec
        },
        onSelectMode = { newMode ->
            isRunning = false
            mode = newMode
            val sec = getDurationFor(newMode)
            timeLeftSeconds = sec
            totalSeconds = sec
        },
        onSetWorkDuration = { newMin ->
            workDuration = newMin
            if (mode == PomodoroMode.FOCUS && !isRunning) {
                val sec = newMin * 60
                timeLeftSeconds = sec
                totalSeconds = sec
            }
        },
        onSetShortBreakDuration = { newMin ->
            shortBreakDuration = newMin
            if (mode == PomodoroMode.SHORT_BREAK && !isRunning) {
                val sec = newMin * 60
                timeLeftSeconds = sec
                totalSeconds = sec
            }
        },
        onSetLongBreakDuration = { newMin ->
            longBreakDuration = newMin
            if (mode == PomodoroMode.LONG_BREAK && !isRunning) {
                val sec = newMin * 60
                timeLeftSeconds = sec
                totalSeconds = sec
            }
        },
        onSkipSession = {
            isRunning = false
            if (mode == PomodoroMode.FOCUS) {
                completedCycles++
                val nextMode = if (completedCycles % 4 == 0) PomodoroMode.LONG_BREAK else PomodoroMode.SHORT_BREAK
                mode = nextMode
                val sec = getDurationFor(nextMode)
                timeLeftSeconds = sec
                totalSeconds = sec
            } else {
                mode = PomodoroMode.FOCUS
                val sec = getDurationFor(PomodoroMode.FOCUS)
                timeLeftSeconds = sec
                totalSeconds = sec
            }
        },
        modifier = modifier
    )
}

/**
 * Controlled Pomodoro Timer Component.
 * Supports start, pause, reset, mode switching, and configurable intervals for work and breaks.
 */
@Composable
fun PomodoroTimer(
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
    var showAdjustIntervals by remember { mutableStateOf(false) }

    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    val rawProgress = if (totalSeconds > 0) {
        (timeLeftSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = 1f - rawProgress,
        animationSpec = tween(durationMillis = 350),
        label = "pomodoro_progress_anim"
    )

    val activeColor = when (mode) {
        PomodoroMode.FOCUS -> AquaPrimary
        PomodoroMode.SHORT_BREAK -> WarmAmber
        PomodoroMode.LONG_BREAK -> CalmMint
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .testTag("pomodoro_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row: Title, Subtitle, and Adjust Toggle Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(activeColor.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (mode) {
                                PomodoroMode.FOCUS -> "⏱️"
                                PomodoroMode.SHORT_BREAK -> "☕"
                                PomodoroMode.LONG_BREAK -> "🌿"
                            },
                            fontSize = 20.sp
                        )
                    }

                    Column {
                        Text(
                            text = "Timer Pomodoro",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = when {
                                isRunning && mode == PomodoroMode.FOCUS -> "Foco em andamento • Mantenha a calma"
                                isRunning && mode == PomodoroMode.SHORT_BREAK -> "Pausa curta ativa • Respire e hidrate-se"
                                isRunning && mode == PomodoroMode.LONG_BREAK -> "Pausa longa ativa • Desconecte a mente"
                                !isRunning && timeLeftSeconds != totalSeconds -> "Pausado • Retome quando estiver pronto"
                                else -> "Intervalos adaptados para TDAH"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = activeColor,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                // Button to toggle configuration panel
                OutlinedButton(
                    onClick = { showAdjustIntervals = !showAdjustIntervals },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("pomodoro_toggle_intervals_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Configurar Intervalos",
                        tint = activeColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Intervalos",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = if (showAdjustIntervals) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mode Selector segmented bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    Triple(PomodoroMode.FOCUS, "Foco ${workDurationMinutes}m", "pomodoro_tab_focus"),
                    Triple(PomodoroMode.SHORT_BREAK, "Pausa ${shortBreakDurationMinutes}m", "pomodoro_tab_short_break"),
                    Triple(PomodoroMode.LONG_BREAK, "Longa ${longBreakDurationMinutes}m", "pomodoro_tab_long_break")
                ).forEach { (m, label, tag) ->
                    val isSelected = mode == m
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) activeColor else Color.Transparent)
                            .clickable { onSelectMode(m) }
                            .testTag(tag),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSelected) NavyDarkBackground else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Circular Visual Timer Display
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(210.dp)
                    .padding(6.dp)
                    .testTag("pomodoro_circular_display")
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    val strokeWidth = 13.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2f
                    val centerOffset = Offset(size.width / 2f, size.height / 2f)

                    // Track background
                    drawCircle(
                        color = trackColor,
                        radius = radius,
                        style = Stroke(width = strokeWidth)
                    )

                    // Active progress arc
                    val sweepAngle = 360f * animatedProgress
                    drawArc(
                        color = activeColor,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Glowing tip dot
                    if (sweepAngle > 0f) {
                        val angleInRad = Math.toRadians((sweepAngle - 90f).toDouble())
                        val dotX = centerOffset.x + radius * cos(angleInRad).toFloat()
                        val dotY = centerOffset.y + radius * sin(angleInRad).toFloat()
                        drawCircle(
                            color = Color.White,
                            radius = strokeWidth * 0.38f,
                            center = Offset(dotX, dotY)
                        )
                    }
                }

                // Inner content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = activeColor.copy(alpha = 0.15f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = when {
                                isRunning && mode == PomodoroMode.FOCUS -> "EM FOCO"
                                isRunning -> "EM PAUSA"
                                timeLeftSeconds == totalSeconds -> "PRONTO"
                                else -> "PAUSADO"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = activeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                letterSpacing = 0.8.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Countdown text
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.testTag("pomodoro_countdown_text")
                    )

                    // Total minutes display
                    Text(
                        text = "de ${totalSeconds / 60} min",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Linear Progress Indicator
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .testTag("pomodoro_progress_bar"),
                color = activeColor,
                trackColor = trackColor
            )

            // Cycle progress dots: visualize 4 pomodoros per full cycle
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.testTag("pomodoro_cycle_dots")
            ) {
                Text(
                    text = "Ciclo 4x:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
                val currentInCycle = completedCycles % 4
                for (i in 0 until 4) {
                    val isDone = i < currentInCycle
                    val isCurrent = i == currentInCycle && isRunning
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isDone -> AquaPrimary
                                    isCurrent -> WarmAmber
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .border(
                                width = if (isCurrent) 1.5.dp else 0.dp,
                                color = if (isCurrent) AquaPrimary else Color.Transparent,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Auto-noise active badge (if configured)
            if (autoNoiseActive) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AquaPrimary.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("pomodoro_auto_noise_badge")
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = AquaPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Ruído sincronizado (${selectedNoise.displayName})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AquaPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Controls: Reset, Pause / Start, and Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(0.35f)
                        .height(50.dp)
                        .testTag("pomodoro_reset_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Zerar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Zerar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Start / Pause Button
                Button(
                    onClick = if (isRunning) onPause else onStart,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) WarmAmber else AquaPrimary
                    ),
                    modifier = Modifier
                        .weight(0.65f)
                        .height(50.dp)
                        .testTag("pomodoro_action_btn")
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pausar" else "Iniciar",
                        tint = NavyDarkBackground,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "Pausar" else "Iniciar Pomodoro",
                        color = NavyDarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                }
            }

            // Expandable Configurable Intervals Section
            AnimatedVisibility(
                visible = showAdjustIntervals,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .padding(16.dp)
                        .testTag("pomodoro_interval_config_panel"),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚙️ Configurar Intervalos",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Toque em um preset ou ajuste",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }

                    // ADHD Presets
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Rotinas Rápidas para TDAH:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ADHD_POMODORO_PRESETS.forEach { preset ->
                                val isSelected = workDurationMinutes == preset.workMinutes &&
                                        shortBreakDurationMinutes == preset.shortBreakMinutes &&
                                        longBreakDurationMinutes == preset.longBreakMinutes

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) AquaPrimary.copy(alpha = 0.22f)
                                            else MaterialTheme.colorScheme.surface
                                        )
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) AquaPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            onSetWorkDuration(preset.workMinutes)
                                            onSetShortBreakDuration(preset.shortBreakMinutes)
                                            onSetLongBreakDuration(preset.longBreakMinutes)
                                        }
                                        .padding(horizontal = 6.dp, vertical = 10.dp)
                                        .testTag("preset_${preset.name.lowercase().replace(" ", "_")}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(text = preset.icon, fontSize = 16.sp)
                                        Text(
                                            text = preset.name,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) AquaPrimary else MaterialTheme.colorScheme.onSurface,
                                                fontSize = 11.sp
                                            )
                                        )
                                        Text(
                                            text = "${preset.workMinutes}/${preset.shortBreakMinutes}m",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 9.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 1. Work / Focus interval adjustment
                    ConfigurableIntervalRow(
                        title = "Sessão de Foco (Trabalho)",
                        icon = "🧠",
                        currentMinutes = workDurationMinutes,
                        min = 5,
                        max = 90,
                        step = 5,
                        presets = listOf(15, 20, 25, 30, 45, 50, 60),
                        tagPrefix = "work",
                        accentColor = AquaPrimary,
                        onValueChange = onSetWorkDuration
                    )

                    // 2. Short Break interval adjustment
                    ConfigurableIntervalRow(
                        title = "Pausa Curta (Descanso)",
                        icon = "☕",
                        currentMinutes = shortBreakDurationMinutes,
                        min = 1,
                        max = 30,
                        step = 1,
                        presets = listOf(3, 5, 8, 10, 15),
                        tagPrefix = "short_break",
                        accentColor = WarmAmber,
                        onValueChange = onSetShortBreakDuration
                    )

                    // 3. Long Break interval adjustment
                    ConfigurableIntervalRow(
                        title = "Pausa Longa (Recuperação)",
                        icon = "🌿",
                        currentMinutes = longBreakDurationMinutes,
                        min = 5,
                        max = 60,
                        step = 5,
                        presets = listOf(10, 15, 20, 30, 45),
                        tagPrefix = "long_break",
                        accentColor = CalmMint,
                        onValueChange = onSetLongBreakDuration
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfigurableIntervalRow(
    title: String,
    icon: String,
    currentMinutes: Int,
    min: Int,
    max: Int,
    step: Int,
    presets: List<Int>,
    tagPrefix: String,
    accentColor: Color,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            .padding(12.dp)
            .testTag("adjuster_row_$tagPrefix"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Label + Stepper controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = icon, fontSize = 16.sp)
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            // Stepper buttons with minimum 48dp touch target
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = {
                        val newMin = (currentMinutes - step).coerceAtLeast(min)
                        onValueChange(newMin)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("btn_dec_$tagPrefix")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Diminuir $title",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = "${currentMinutes} min",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .testTag("text_val_$tagPrefix")
                )

                IconButton(
                    onClick = {
                        val newMin = (currentMinutes + step).coerceAtMost(max)
                        onValueChange(newMin)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("btn_inc_$tagPrefix")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar $title",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Continuous Slider for fluid adjustment
        Slider(
            value = currentMinutes.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = min.toFloat()..max.toFloat(),
            steps = (max - min) / step - 1,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .testTag("slider_$tagPrefix")
        )

        // Quick Preset Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            presets.forEach { preset ->
                val isSelected = currentMinutes == preset
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) accentColor.copy(alpha = 0.22f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        )
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) accentColor else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onValueChange(preset) }
                        .testTag("chip_${tagPrefix}_${preset}m"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${preset}m",
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
