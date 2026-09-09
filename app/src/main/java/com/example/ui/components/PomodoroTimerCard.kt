package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoiseType
import com.example.data.model.PomodoroMode
import com.example.ui.theme.AquaPrimary
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.WarmAmber

@Composable
fun PomodoroTimerCard(
    timeLeftSeconds: Int,
    totalSeconds: Int,
    isRunning: Boolean,
    mode: PomodoroMode,
    autoNoiseActive: Boolean,
    selectedNoise: NoiseType,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSelectMode: (PomodoroMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)
    val progress = if (totalSeconds > 0) {
        (timeLeftSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("pomodoro_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode selector (Foco 25m, Pausa 5m)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    PomodoroMode.FOCUS to "Foco 25m",
                    PomodoroMode.SHORT_BREAK to "Pausa 5m",
                    PomodoroMode.LONG_BREAK to "Pausa 15m"
                ).forEach { (m, label) ->
                    val isSelected = mode == m
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AquaPrimary else Color.Transparent)
                            .padding(vertical = 8.dp)
                            .testTag("pomodoro_tab_${m.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.text.BasicText(
                            text = label,
                            style = androidx.compose.ui.text.TextStyle(
                                color = if (isSelected) NavyDarkBackground else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Digital Countdown Display
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                    color = if (isRunning) AquaPrimary else MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.testTag("pomodoro_countdown_text")
            )

            // Progress bar
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { 1f - progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .testTag("pomodoro_progress_bar"),
                color = if (mode == PomodoroMode.FOCUS) AquaPrimary else WarmAmber,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Auto-noise indicator tag
            Spacer(modifier = Modifier.height(12.dp))
            if (autoNoiseActive) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AquaPrimary.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = AquaPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Ruído automático sincronizado (${selectedNoise.displayName})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AquaPrimary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons (Iniciar / Pausar / Reiniciar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(0.4f)
                        .height(48.dp)
                        .testTag("pomodoro_reset_btn")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reiniciar", modifier = Modifier.size(18.dp))
                }

                Button(
                    onClick = if (isRunning) onPause else onStart,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) WarmAmber else AquaPrimary
                    ),
                    modifier = Modifier
                        .weight(0.6f)
                        .height(48.dp)
                        .testTag("pomodoro_action_btn")
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pausar" else "Iniciar",
                        tint = NavyDarkBackground,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRunning) "Pausar" else "Iniciar Pomodoro",
                        color = NavyDarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
