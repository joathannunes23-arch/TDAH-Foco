package com.example.data.model

data class MindMapNode(
    val id: String,
    val text: String,
    val icon: String = "💡",
    val isCompleted: Boolean = false,
    val tag: String = "",
    val subtasks: List<String> = emptyList()
)

data class MindMap(
    val id: String,
    val title: String,
    val category: String,
    val icon: String,
    val scientificCitation: String,
    val scientificExplanation: String,
    val adhdNeuroTip: String,
    val centralNodeTitle: String,
    val nodes: List<MindMapNode>,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val backgroundColor: String = "#0A2540"
)
