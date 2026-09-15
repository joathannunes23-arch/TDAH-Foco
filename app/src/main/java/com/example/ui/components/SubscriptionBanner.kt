package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubscriptionInfo
import com.example.data.model.SubscriptionPlanStatus
import com.example.ui.theme.AquaPrimary
import com.example.ui.theme.CalmMint
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.WarmAmber

/**
 * Banner discreto e acolhedor de status da assinatura Pro para a tela inicial (Dashboard).
 * Projetado especialmente para evitar sobrecarga sensorial no TDAH, usando linguagem calma.
 */
@Composable
fun SubscriptionBanner(
    subscriptionInfo: SubscriptionInfo,
    onOpenSubscriptionManager: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTrial = subscriptionInfo.status == SubscriptionPlanStatus.FREE_TRIAL
    val isPro = subscriptionInfo.isPro

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onOpenSubscriptionManager() }
            .testTag("subscription_dashboard_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPro) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = if (isPro) {
                            listOf(AquaPrimary.copy(alpha = 0.5f), CalmMint.copy(alpha = 0.4f))
                        } else {
                            listOf(WarmAmber.copy(alpha = 0.5f), AquaPrimary.copy(alpha = 0.3f))
                        }
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isPro) AquaPrimary.copy(alpha = 0.18f) else WarmAmber.copy(alpha = 0.18f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPro) Icons.Default.Diamond else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (isPro) AquaPrimary else WarmAmber,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isPro) "Plano Pro Ativo" else "Plano Gratuito",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isTrial) {
                                    AquaPrimary.copy(alpha = 0.2f)
                                } else if (isPro) {
                                    CalmMint.copy(alpha = 0.2f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = if (isTrial) {
                                        "Teste: ${subscriptionInfo.daysLeft} dias"
                                    } else if (isPro) {
                                        "PRO"
                                    } else {
                                        "BÁSICO"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isTrial) AquaPrimary else if (isPro) CalmMint else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = if (isTrial) {
                                "Aproveite 7 dias grátis de IA e ruídos contínuos — R$ 9,90/mês após o teste"
                            } else if (isPro) {
                                "Mapas com IA, ruídos ilimitados e Pomodoro inteligente desbloqueados"
                            } else {
                                "Desbloqueie IA científica e ruídos ilimitados por R$ 9,90/mês"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.5.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver Assinatura",
                    tint = AquaPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
