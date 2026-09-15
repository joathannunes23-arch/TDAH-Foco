package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MindMap
import com.example.data.model.NoiseType
import com.example.data.model.PomodoroMode
import com.example.data.model.RoutineTask
import com.example.data.model.SubscriptionInfo
import com.example.ui.components.NoisePlayerCard
import com.example.ui.components.PomodoroTimerCard
import com.example.ui.components.SubscriptionBanner
import com.example.ui.theme.AquaPrimary
import com.example.ui.theme.CalmMint
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.WarmAmber
import com.example.ui.theme.WarmCoral

@Composable
fun DashboardScreen(
    userName: String,
    activeMindMap: MindMap,
    currentNoise: NoiseType,
    isNoisePlaying: Boolean,
    noiseVolume: Float,
    pomodoroTimeLeft: Int,
    pomodoroTotal: Int,
    isPomodoroRunning: Boolean,
    pomodoroMode: PomodoroMode,
    workDurationMinutes: Int = 25,
    shortBreakDurationMinutes: Int = 5,
    longBreakDurationMinutes: Int = 15,
    autoNoiseActive: Boolean,
    selectedPomodoroNoise: NoiseType,
    completedCycles: Int = 0,
    routineTasks: List<RoutineTask>,
    feedbackMessage: String?,
    subscriptionInfo: SubscriptionInfo = SubscriptionInfo(),
    onOpenSubscriptionManager: () -> Unit = {},
    onPlayNoise: (NoiseType) -> Unit,
    onTogglePlayPauseNoise: () -> Unit = {},
    onSetVolume: (Float) -> Unit,
    onApplyMixedNoise: () -> Unit,
    onStartPomodoro: () -> Unit,
    onPausePomodoro: () -> Unit,
    onResetPomodoro: () -> Unit,
    onSelectPomodoroMode: (PomodoroMode) -> Unit,
    onSetWorkDuration: (Int) -> Unit = {},
    onSetShortBreakDuration: (Int) -> Unit = {},
    onSetLongBreakDuration: (Int) -> Unit = {},
    onSkipPomodoroSession: (() -> Unit)? = null,
    onToggleRoutineTask: (String) -> Unit,
    onGenerateMindMap: (String) -> Unit,
    onOpenMindMapTab: () -> Unit,
    onDismissFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Header with Logo + "Bem-vindo, [nome]" + Star Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(AquaPrimary.copy(alpha = 0.18f))
                            .border(1.5.dp, AquaPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🧠", fontSize = 22.sp)
                    }

                    Column {
                        Text(
                            text = "TDAH Foco",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AquaPrimary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Bem-vindo, $userName!",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.testTag("user_welcome_title")
                        )
                    }
                }

                // Star badge for streaks & focus
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(WarmAmber.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Estrela de Foco",
                            tint = WarmAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Foco Ativo",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarmAmber
                            )
                        )
                    }
                }
            }

            // Feedback Banner Message
            AnimatedVisibility(visible = feedbackMessage != null) {
                feedbackMessage?.let { msg ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = AquaPrimary.copy(alpha = 0.12f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "✨", fontSize = 16.sp)
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AquaPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                            IconButton(
                                onClick = onDismissFeedback,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = AquaPrimary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // Subscription Freemium Banner (Calmo e Acolhedor)
            SubscriptionBanner(
                subscriptionInfo = subscriptionInfo,
                onOpenSubscriptionManager = onOpenSubscriptionManager
            )

            // 1. Large Card: "Meu Mapa Mental Hoje"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("card_my_mindmap_today"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🗺️", fontSize = 20.sp)
                            Column {
                                Text(
                                    text = "Meu Mapa Mental Hoje",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "Baseado em neurociência para TDAH",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AquaPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${activeMindMap.nodes.count { it.isCompleted }}/${activeMindMap.nodes.size} feitos",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AquaPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Active Map Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { onOpenMindMapTab() }
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = activeMindMap.icon, fontSize = 24.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeMindMap.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = activeMindMap.centralNodeTitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AquaPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Text(
                                text = "Abrir ➔",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = AquaPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Big Action Button "Gerar Mapa"
                    Button(
                        onClick = {
                            onGenerateMindMap("")
                            onOpenMindMapTab()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AquaPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dashboard_generate_map_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NavyDarkBackground,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gerar Mapa",
                            fontWeight = FontWeight.Bold,
                            color = NavyDarkBackground,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // 2. Large Card: "Player de Ruídos" (Marrom, Branco, Rosa, volume e play/pause)
            NoisePlayerCard(
                currentNoise = currentNoise,
                isPlaying = isNoisePlaying,
                volume = noiseVolume,
                onPlayNoise = onPlayNoise,
                onTogglePlayPause = onTogglePlayPauseNoise,
                onSetVolume = onSetVolume,
                onApplyMixedPreset = onApplyMixedNoise
            )

            // 3. Large Card: "Sessão Pomodoro" (com controles e intervalos ajustáveis)
            PomodoroTimerCard(
                timeLeftSeconds = pomodoroTimeLeft,
                totalSeconds = pomodoroTotal,
                isRunning = isPomodoroRunning,
                mode = pomodoroMode,
                workDurationMinutes = workDurationMinutes,
                shortBreakDurationMinutes = shortBreakDurationMinutes,
                longBreakDurationMinutes = longBreakDurationMinutes,
                autoNoiseActive = autoNoiseActive,
                selectedNoise = selectedPomodoroNoise,
                completedCycles = completedCycles,
                onStart = onStartPomodoro,
                onPause = onPausePomodoro,
                onReset = onResetPomodoro,
                onSelectMode = onSelectPomodoroMode,
                onSetWorkDuration = onSetWorkDuration,
                onSetShortBreakDuration = onSetShortBreakDuration,
                onSetLongBreakDuration = onSetLongBreakDuration,
                onSkipSession = onSkipPomodoroSession
            )

            // 4. Large Card: "Rotina Hoje" (lista simples com checkmarks)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("card_routine_today"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "✅", fontSize = 20.sp)
                            Column {
                                Text(
                                    text = "Rotina Hoje",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "Micro-hábitos de dopamina e baixa fricção",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        Text(
                            text = "${routineTasks.count { it.isCompleted }}/${routineTasks.size}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CalmMint
                            )
                        )
                    }

                    routineTasks.take(4).forEach { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onToggleRoutineTask(task.id) }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                contentDescription = if (task.isCompleted) "Concluído" else "Marcar tarefa",
                                tint = if (task.isCompleted) CalmMint else AquaPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(text = task.icon, fontSize = 16.sp)
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
