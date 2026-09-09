package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MindMap
import com.example.ui.components.MindMapCanvas
import com.example.ui.components.ScientificEvidenceCard
import com.example.ui.theme.AquaPrimary
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.launch

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MindMapScreen(
    activeMindMap: MindMap,
    libraryMaps: List<MindMap>,
    onSelectMap: (String) -> Unit,
    onToggleNode: (String) -> Unit,
    onAddNode: (String, String, String) -> Unit,
    onRemoveNode: (String) -> Unit,
    onChangeBackground: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onGenerateMindMap: (String) -> Unit,
    onExportMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLibrarySheet by remember { mutableStateOf(false) }
    var showGenerateDialog by remember { mutableStateOf(false) }
    var generatePrompt by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar: Active Title, Favorite, Library Button, Generate Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = activeMindMap.icon, fontSize = 24.sp)
                Column {
                    Text(
                        text = activeMindMap.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = activeMindMap.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AquaPrimary
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onToggleFavorite(activeMindMap.id) },
                    modifier = Modifier.testTag("mindmap_fav_btn")
                ) {
                    Icon(
                        imageVector = if (activeMindMap.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Favoritar Mapa",
                        tint = WarmAmber
                    )
                }

                IconButton(
                    onClick = { showLibrarySheet = true },
                    modifier = Modifier.testTag("mindmap_library_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderSpecial,
                        contentDescription = "Biblioteca de 8 Mapas",
                        tint = AquaPrimary
                    )
                }

                Button(
                    onClick = { showGenerateDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AquaPrimary),
                    modifier = Modifier.testTag("mindmap_screen_generate_btn")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NavyDarkBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("IA", color = NavyDarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Quick Selector Row for Pre-defined Research Maps
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(libraryMaps) { map ->
                val isSelected = map.id == activeMindMap.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) AquaPrimary else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onSelectMap(map.id) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("quick_select_map_${map.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = map.icon, fontSize = 14.sp)
                        Text(
                            text = map.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NavyDarkBackground else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Scientific Evidence Card (Always shown automatically for the opened map!)
        ScientificEvidenceCard(
            citation = activeMindMap.scientificCitation,
            explanation = activeMindMap.scientificExplanation,
            adhdTip = activeMindMap.adhdNeuroTip,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            initiallyExpanded = false
        )

        // Mind Map Visual Canvas & Editor
        MindMapCanvas(
            mindMap = activeMindMap,
            onToggleNode = onToggleNode,
            onAddNode = onAddNode,
            onRemoveNode = onRemoveNode,
            onChangeBackground = onChangeBackground,
            onExport = onExportMap,
            modifier = Modifier.weight(1f)
        )
    }

    // Modal Bottom Sheet: Full Library of 8 Scientific Mind Maps + Favorites
    if (showLibrarySheet) {
        ModalBottomSheet(
            onDismissRequest = { showLibrarySheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Biblioteca Científica (8 Mapas TDAH)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Pesquisas de Barkley, Ramsay, Becker, Sweller e outros",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    libraryMaps.forEach { map ->
                        val isSelected = map.id == activeMindMap.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSelectMap(map.id)
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        showLibrarySheet = false
                                    }
                                }
                                .testTag("library_item_${map.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) AquaPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = map.icon, fontSize = 24.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = map.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = map.category,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = AquaPrimary
                                        )
                                    )
                                }
                                if (map.isFavorite) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = "Favorito",
                                        tint = WarmAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // AI Generate Dialog
    if (showGenerateDialog) {
        AlertDialog(
            onDismissRequest = { showGenerateDialog = false },
            title = {
                Text(
                    text = "Gerar Mapa Científico com IA",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Informe a tarefa ou contexto neurodivergente:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = generatePrompt,
                        onValueChange = { generatePrompt = it },
                        placeholder = { Text("Ex: Fazer declaração de impostos, estudar para prova...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onGenerateMindMap(generatePrompt)
                        generatePrompt = ""
                        showGenerateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AquaPrimary)
                ) {
                    Text("Gerar", color = NavyDarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGenerateDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
