package com.example.data.model

data class RoutineTask(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val period: String = "Manhã",
    val icon: String = "⚡"
)
