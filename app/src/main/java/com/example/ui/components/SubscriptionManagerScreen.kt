package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubscriptionInfo
import com.example.data.model.SubscriptionPlanStatus
import com.example.ui.theme.AquaPrimary
import com.example.ui.theme.CalmMint
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.WarmAmber
import com.example.ui.theme.WarmCoral

/**
 * Tela de Gerenciamento da Assinatura Pro ("Minha Assinatura").
 * Em português brasileiro, com linguagem acolhedora, calma e acessível para quem tem TDAH.
 * Sem pressões agressivas de marketing.
 */
@Composable
fun SubscriptionManagerScreen(
    subscriptionInfo: SubscriptionInfo,
    onStartFreeTrial: () -> Unit,
    onActivatePro: () -> Unit,
    onCancelSubscription: () -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCancelConfirmDialog by remember { mutableStateOf(false) }
    var showCheckoutSimulationDialog by remember { mutableStateOf(false) }

    val isPro = subscriptionInfo.isPro
    val isTrial = subscriptionInfo.status == SubscriptionPlanStatus.FREE_TRIAL

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Minha Assinatura",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "Linguagem calma, sem pegadinhas ou renovações silenciosas",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .testTag("subscription_status_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isPro) AquaPrimary.copy(alpha = 0.2f) else WarmAmber.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPro) Icons.Default.Diamond else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isPro) AquaPrimary else WarmAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = when (subscriptionInfo.status) {
                                    SubscriptionPlanStatus.FREE_TRIAL -> "Teste Grátis de 7 Dias"
                                    SubscriptionPlanStatus.PRO_ACTIVE -> "Plano Pro Mensal"
                                    SubscriptionPlanStatus.EXPIRED -> "Assinatura Expirada / Cancelada"
                                    SubscriptionPlanStatus.FREE_TIER -> "Plano Básico Gratuito"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = if (isTrial) {
                                    "Restam ${subscriptionInfo.daysLeft} dias de teste gratuito"
                                } else if (isPro) {
                                    "Acesso ilimitado e sem restrições"
                                } else {
                                    "Recursos avançados de IA e áudio bloqueados"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isTrial || isPro) CalmMint else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPro) CalmMint.copy(alpha = 0.18f) else WarmAmber.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = if (isPro) "ATIVO" else "GRÁTIS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isPro) CalmMint else WarmAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Detalhe de Preço
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Valor da Assinatura Pro",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = subscriptionInfo.priceFormatted,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AquaPrimary
                                    )
                                )
                                Text(
                                    text = " / ${subscriptionInfo.period}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Cancele a qualquer momento",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CalmMint,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Botões de Ação de Assinatura
                if (!isPro) {
                    Button(
                        onClick = { showCheckoutSimulationDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("subscribe_pro_now_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AquaPrimary)
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = NavyDarkBackground)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Assinar Pro Agora (R$ 9,90/mês)",
                            color = NavyDarkBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onStartFreeTrial,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("start_free_trial_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AquaPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Iniciar Teste Grátis de 7 Dias", color = AquaPrimary, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    OutlinedButton(
                        onClick = { showCancelConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("cancel_subscription_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = WarmCoral
                        )
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, tint = WarmCoral, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cancelar assinatura a qualquer momento", color = WarmCoral, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Benefícios Pro Comparativo (Calmo e Claro)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "O que está incluído no seu plano Pro:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                val benefits = listOf(
                    "Geração ilimitada de Mapas Mentais Científicos com IA",
                    "Acesso contínuo sem limites aos ruídos (Marrom, Branco, Rosa)",
                    "Pomodoro Inteligente com sincronização automática de ruídos",
                    "Exportação de mapas e resumos em PDF / texto estruturado",
                    "Histórico de sessões de foco e rotinas sem interrupções",
                    "Suporte dedicado e atualizações neurocientíficas contínuas"
                )

                benefits.forEach { benefit ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CalmMint,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = benefit,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }

        // Garantia de Segurança e Respeito
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = AquaPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Cobrança segura via Stripe Checkout. Você não receberá e-mails insistentes ou cobranças inesperadas. Você está no controle total.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }

        // Botão de retorno ao Dashboard
        TextButton(
            onClick = onBackToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("back_to_dashboard_btn")
        ) {
            Text("Voltar ao Início", color = AquaPrimary, fontWeight = FontWeight.Bold)
        }
    }

    // Modal de Confirmação de Cancelamento
    if (showCancelConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmDialog = false },
            title = {
                Text(
                    text = "Deseja cancelar a assinatura?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Tudo bem, sem pressão. Você ainda pode usar o aplicativo no plano básico gratuito. O cancelamento é imediato e você voltará para o painel principal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelSubscription()
                        showCancelConfirmDialog = false
                        onBackToDashboard()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmCoral)
                ) {
                    Text("Confirmar Cancelamento", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirmDialog = false }) {
                    Text("Continuar no Pro", color = AquaPrimary)
                }
            }
        )
    }

    // Modal de Simulação do Stripe Checkout
    if (showCheckoutSimulationDialog) {
        AlertDialog(
            onDismissRequest = { showCheckoutSimulationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = AquaPrimary)
                    Text("Stripe Checkout Seguro", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Assinatura Recorrente TDAH Foco Pro",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "• Valor: R$ 9,90 / mês\n• Teste de 7 dias com cobrança apenas ao término\n• Webhook seguro configurado para liberação instantânea",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onActivatePro()
                        showCheckoutSimulationDialog = false
                        onBackToDashboard()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquaPrimary)
                ) {
                    Text("Concluir Assinatura Pro", color = NavyDarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckoutSimulationDialog = false }) {
                    Text("Voltar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}
