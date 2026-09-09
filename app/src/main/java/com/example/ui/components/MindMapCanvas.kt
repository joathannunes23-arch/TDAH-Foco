package com.example.ui.components

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MindMap
import com.example.ui.theme.AquaPrimary
import com.example.ui.theme.CalmMint
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.SoftPurple
import com.example.ui.theme.WarmAmber
import com.example.ui.theme.WarmCoral

@Composable
fun MindMapCanvas(
    mindMap: MindMap,
    onToggleNode: (String) -> Unit,
    onAddNode: (String, String, String) -> Unit,
    onRemoveNode: (String) -> Unit,
    onChangeBackground: (String) -> Unit,
    onExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var newNodeText by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("💡") }

    val bgParsedColor = try {
        Color(android.graphics.Color.parseColor(mindMap.backgroundColor))
    } catch (e: Exception) {
        NavyDarkBackground
    }

    val availableEmojis = listOf("💡", "⚡", "🎯", "🎧", "🧠", "😌", "📅", "💧", "☕", "🌊")
    val backgroundColors = listOf(
        Pair("#0A2540", "Azul Noturno"),
        Pair("#0F172A", "Ardósia Escura"),
        Pair("#042F2E", "Verde Musgo Foco"),
        Pair("#1E1B4B", "Índigo Profundo")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgParsedColor)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Toolbar: Add Node, Change Background, Export
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${mindMap.nodes.count { it.isCompleted }}/${mindMap.nodes.size} Etapas",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AquaPrimary
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Color button
                OutlinedButton(
                    onClick = { showColorPicker = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AquaPrimary),
                    modifier = Modifier.testTag("mindmap_color_btn")
                ) {
                    Icon(Icons.Default.Palette, contentDescription = "Mudar Cor", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Fundo", fontSize = 12.sp)
                }

                // Export button
                OutlinedButton(
                    onClick = onExport,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmAmber),
                    modifier = Modifier.testTag("mindmap_export_btn")
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Salvar PNG/PDF", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Salvar", fontSize = 12.sp)
                }

                // Add Node button
                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AquaPrimary),
                    modifier = Modifier.testTag("mindmap_add_node_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Caixa", tint = NavyDarkBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Caixa", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NavyDarkBackground)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Central Node (Core Concept)
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .border(2.dp, AquaPrimary, RoundedCornerShape(20.dp))
                .testTag("central_node_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = mindMap.icon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = mindMap.centralNodeTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Centro de Foco Dopamínico",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AquaPrimary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Connector canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 3.dp.toPx()
                val midX = size.width / 2f
                val path = Path().apply {
                    moveTo(midX, 0f)
                    lineTo(midX, size.height)
                }
                drawPath(path, color = AquaPrimary.copy(alpha = 0.6f), style = Stroke(width = strokeWidth))
            }
        }

        // Child Nodes (Interactive Boxes)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            mindMap.nodes.forEachIndexed { index, node ->
                val nodeBorderColor = if (node.isCompleted) CalmMint else when (index % 3) {
                    0 -> AquaPrimary.copy(alpha = 0.8f)
                    1 -> WarmAmber.copy(alpha = 0.8f)
                    else -> SoftPurple.copy(alpha = 0.8f)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, nodeBorderColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .testTag("node_item_${node.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (node.isCompleted) {
                            CalmMint.copy(alpha = 0.08f)
                        } else {
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Completion check button
                            IconButton(
                                onClick = { onToggleNode(node.id) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("toggle_node_${node.id}")
                            ) {
                                Icon(
                                    imageVector = if (node.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                    contentDescription = if (node.isCompleted) "Concluído" else "Marcar como concluído",
                                    tint = if (node.isCompleted) CalmMint else AquaPrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Node emoji icon
                            Text(text = node.icon, fontSize = 20.sp)

                            // Node text and tag
                            Column {
                                if (node.tag.isNotBlank()) {
                                    Text(
                                        text = node.tag.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = nodeBorderColor
                                        )
                                    )
                                }
                                Text(
                                    text = node.text,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = if (node.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                        textDecoration = if (node.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                )
                            }
                        }

                        // Remove Node action
                        IconButton(
                            onClick = { onRemoveNode(node.id) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("remove_node_${node.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remover nó",
                                tint = WarmCoral.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Add Node Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Adicionar Caixa ao Mapa",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Escolha um ícone âncora:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        availableEmojis.take(5).forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedEmoji == emoji) AquaPrimary.copy(alpha = 0.3f) else Color.Transparent)
                                    .clickable { selectedEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 20.sp)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        availableEmojis.drop(5).forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedEmoji == emoji) AquaPrimary.copy(alpha = 0.3f) else Color.Transparent)
                                    .clickable { selectedEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 20.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newNodeText,
                        onValueChange = { newNodeText = it },
                        label = { Text("Ação simples / Micro-passo") },
                        placeholder = { Text("Ex: Respirar fundo 3x, abrir caderno") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_node_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AquaPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNodeText.isNotBlank()) {
                            onAddNode(newNodeText, selectedEmoji, "Personalizado")
                            newNodeText = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquaPrimary),
                    modifier = Modifier.testTag("confirm_add_node_btn")
                ) {
                    Text("Adicionar", color = NavyDarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Background Color Picker Dialog
    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = {
                Text(
                    text = "Mudar Cor do Fundo",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    backgroundColors.forEach { (colorHex, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(android.graphics.Color.parseColor(colorHex)))
                                .clickable {
                                    onChangeBackground(colorHex)
                                    showColorPicker = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(AquaPrimary)
                            )
                            Text(text = name, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showColorPicker = false }) {
                    Text("Fechar")
                }
            }
        )
    }
}
