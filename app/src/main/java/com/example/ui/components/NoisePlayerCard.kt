package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.WarmAmber

/**
 * Componente reutilizável de player de áudio para ruídos de foco (Marrom, Branco e Rosa).
 * Inclui controles de reprodução/pausa, alternância entre tipos de ruído, controle de volume deslizante
 * e feedback visual de ondas sonoras animadas.
 */
@Composable
fun NoisePlayerCard(
    currentNoise: NoiseType,
    isPlaying: Boolean,
    volume: Float,
    onPlayNoise: (NoiseType) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSetVolume: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onApplyMixedPreset: (() -> Unit)? = null
) {
    // Animação de ondas de áudio quando reproduzindo
    val infiniteTransition = rememberInfiniteTransition(label = "noise_card_wave")
    val waveAnim1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nw1"
    )
    val waveAnim2 by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nw2"
    )
    val waveAnim3 by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(680, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nw3"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .testTag("noise_player_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cabeçalho com título, status e indicador visual
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) AquaPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (currentNoise) {
                                NoiseType.BROWN -> "🌊"
                                NoiseType.WHITE -> "💨"
                                NoiseType.PINK -> "🌧️"
                                NoiseType.MIXED -> "🎛️"
                                else -> "🎧"
                            },
                            fontSize = 18.sp
                        )
                    }
                    Column {
                        Text(
                            text = "Player de Ruídos para Foco",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = if (isPlaying) "Tocando: ${currentNoise.displayName}" else "Áudio pausado",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isPlaying) CalmMint else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isPlaying) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPlaying) CalmMint.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = if (isPlaying) CalmMint else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isPlaying) "ATIVO" else "PAUSADO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isPlaying) CalmMint else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Visualizador sonoro de barras animadas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val barCount = 24
                    val barWidth = 5.dp.toPx()
                    val spacing = (size.width - (barCount * barWidth)) / (barCount - 1)
                    val barColor = if (isPlaying) AquaPrimary else AquaPrimary.copy(alpha = 0.25f)

                    for (i in 0 until barCount) {
                        val factor = when (i % 3) {
                            0 -> if (isPlaying) waveAnim1 else 0.2f
                            1 -> if (isPlaying) waveAnim2 else 0.15f
                            else -> if (isPlaying) waveAnim3 else 0.25f
                        }
                        val barHeight = (size.height * 0.75f * factor).coerceAtLeast(6.dp.toPx())
                        val left = i * (barWidth + spacing)
                        val top = (size.height - barHeight) / 2f

                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                        )
                    }
                }
            }

            // Seletor dos 3 tipos de Ruídos: Marrom, Branco e Rosa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple(NoiseType.BROWN, "Marrom", "🌊"),
                    Triple(NoiseType.WHITE, "Branco", "💨"),
                    Triple(NoiseType.PINK, "Rosa", "🌧️")
                ).forEach { (type, label, icon) ->
                    val isSelected = currentNoise == type
                    val isActive = isPlaying && isSelected

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    isActive -> AquaPrimary
                                    isSelected -> AquaPrimary.copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                }
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) AquaPrimary else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onPlayNoise(type) }
                            .padding(vertical = 10.dp, horizontal = 6.dp)
                            .testTag("noise_selector_${type.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(text = icon, fontSize = 18.sp)
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isActive) NavyDarkBackground else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = when (type) {
                                    NoiseType.BROWN -> "Grave"
                                    NoiseType.WHITE -> "Plano"
                                    NoiseType.PINK -> "Suave"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isActive) NavyDarkBackground.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            // Controles de Reprodução / Pausa e Volume
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botão de Play / Pause Principal
                Button(
                    onClick = onTogglePlayPause,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) WarmAmber else AquaPrimary
                    ),
                    modifier = Modifier
                        .height(46.dp)
                        .testTag("noise_player_play_pause_btn")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                        tint = NavyDarkBackground,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "Pausar" else "Ouvir",
                        color = NavyDarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Controle deslizante de volume com ícones e porcentagem
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { onSetVolume(if (volume > 0f) 0f else 0.5f) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (volume == 0f) Icons.Default.VolumeMute else Icons.Default.VolumeDown,
                            contentDescription = "Mudo / Volume",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Slider(
                        value = volume,
                        onValueChange = onSetVolume,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("noise_player_volume_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = AquaPrimary,
                            activeTrackColor = AquaPrimary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    Text(
                        text = "${(volume * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AquaPrimary,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.width(32.dp)
                    )
                }
            }
        }
    }
}
