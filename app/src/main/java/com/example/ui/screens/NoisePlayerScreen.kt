package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoiseType
import com.example.ui.theme.AquaPrimary
import com.example.ui.theme.CalmMint
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.SoftPurple
import com.example.ui.theme.WarmAmber

@Composable
fun NoisePlayerScreen(
    currentNoise: NoiseType,
    isPlaying: Boolean,
    volume: Float,
    onPlayNoise: (NoiseType) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSetVolume: (Float) -> Unit,
    onApplyMixedPreset: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timerOption by remember { mutableStateOf("Loop Infinito") }

    // Waveform audio visualization animation
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")
    val waveAnim1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w1"
    )
    val waveAnim2 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w2"
    )
    val waveAnim3 by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "w3"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title and Subtitle
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Módulo de Ruídos para Foco",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Ressonância Estocástica e Mascaramento para TDAH",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AquaPrimary
                )
            )
        }

        // Visualizer Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("audio_visualizer_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Waveform animation canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barCount = 28
                        val barWidth = 6.dp.toPx()
                        val spacing = (size.width - (barCount * barWidth)) / (barCount - 1)
                        val activeColor = if (isPlaying) AquaPrimary else AquaPrimary.copy(alpha = 0.25f)

                        for (i in 0 until barCount) {
                            val factor = when (i % 3) {
                                0 -> if (isPlaying) waveAnim1 else 0.2f
                                1 -> if (isPlaying) waveAnim2 else 0.15f
                                else -> if (isPlaying) waveAnim3 else 0.3f
                            }
                            val barHeight = (size.height * 0.8f * factor).coerceAtLeast(8.dp.toPx())
                            val left = i * (barWidth + spacing)
                            val top = (size.height - barHeight) / 2f

                            drawRoundRect(
                                color = activeColor,
                                topLeft = Offset(left, top),
                                size = Size(barWidth, barHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                            )
                        }
                    }
                }

                // Status text
                Text(
                    text = if (isPlaying) "REPRODUZINDO: ${currentNoise.displayName.uppercase()}" else "ÁUDIO EM PAUSA",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = if (isPlaying) CalmMint else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // Master Play/Pause Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) WarmAmber else AquaPrimary)
                        .clickable { onTogglePlayPause() }
                        .testTag("noise_master_play_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Tocar",
                        tint = NavyDarkBackground,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Volume slider
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Volume Geral",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${(volume * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AquaPrimary
                            )
                        )
                    }

                    Slider(
                        value = volume,
                        onValueChange = onSetVolume,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("noise_volume_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = AquaPrimary,
                            activeTrackColor = AquaPrimary
                        )
                    )
                }
            }
        }

        // Preset Quick Mixed Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, AquaPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .testTag("preset_mixed_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AquaPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = AquaPrimary, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(
                            text = "Mixagem Científica TDAH",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Rosa 70% (ondas alfa) + Marrom 30% (relaxamento)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Button(
                    onClick = onApplyMixedPreset,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AquaPrimary),
                    modifier = Modifier.testTag("apply_mix_preset_btn")
                ) {
                    Text("Ativar Mix", color = NavyDarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // 3 Individual Noise Cards (Marrom, Branco, Rosa)
        listOf(
            Triple(
                NoiseType.BROWN,
                "Ruído Marrom (Cachoeira Profunda)",
                "Tom grave. Comprovado pelo estudo de Söderlund et al. (2010): o efeito de Ressonância Estocástica estimula receptores auditivos e melhora o desempenho cognitivo no TDAH."
            ),
            Triple(
                NoiseType.WHITE,
                "Ruído Branco (Ventilador Constante)",
                "Espectro sonoro plano em todas as frequências audíveis. Mascara de forma excelente conversas paralelas e sons domésticos que capturam a atenção involuntária."
            ),
            Triple(
                NoiseType.PINK,
                "Ruído Rosa (Chuva Suave / Vento)",
                "Distribuição 1/f com atenuação de 3dB por oitava. Induz sincronização das ondas neurais alfa e beta, facilitando a retenção da memória de trabalho."
            )
        ).forEach { (noiseType, title, scientificNote) ->
            val isActive = isPlaying && currentNoise == noiseType

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.5.dp,
                        if (isActive) AquaPrimary else Color.Transparent,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onPlayNoise(noiseType) }
                    .testTag("noise_card_${noiseType.name.lowercase()}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive) AquaPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            Text(text = noiseType.icon, fontSize = 22.sp)
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) AquaPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Button(
                            onClick = { onPlayNoise(noiseType) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isActive) WarmAmber else AquaPrimary
                            )
                        ) {
                            Text(
                                text = if (isActive) "Pausar" else "Ouvir",
                                color = NavyDarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Text(
                        text = scientificNote,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        // Duration / Timer Option (Loop infinito vs 1h)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = AquaPrimary, modifier = Modifier.size(16.dp))
                Text(
                    text = "Duração de Reprodução:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Loop Infinito", "1 Hora", "Sincronizar Pomodoro").forEach { opt ->
                    val isSelected = timerOption == opt
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AquaPrimary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { timerOption = opt }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = opt,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NavyDarkBackground else MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
