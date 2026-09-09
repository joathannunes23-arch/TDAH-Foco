package com.example.data.model

enum class NoiseType(val displayName: String, val description: String, val icon: String) {
    BROWN(
        displayName = "Ruído Marrom",
        description = "Cachoeira profunda e tons graves. Reduz agitação e induz ressonância estocástica.",
        icon = "🌊"
    ),
    WHITE(
        displayName = "Ruído Branco",
        description = "Ventilador constante e amplo espectro. Bloqueia sons ambientes dispersos.",
        icon = "💨"
    ),
    PINK(
        displayName = "Ruído Rosa",
        description = "Chuva suave e ondas rítmicas. Equilibra a memória de trabalho e ritmo alfa.",
        icon = "🌧️"
    ),
    MIXED(
        displayName = "Mixado (Rosa 70% + Marrom 30%)",
        description = "Combinação ideal para foco profundo e serenidade emocional.",
        icon = "🎛️"
    ),
    NONE(
        displayName = "Silêncio",
        description = "Sem ruído ativo.",
        icon = "🔇"
    )
}

enum class PomodoroMode(val minutes: Int, val label: String) {
    FOCUS(25, "Foco Total"),
    SHORT_BREAK(5, "Pausa Curta"),
    LONG_BREAK(15, "Pausa Longa")
}

data class NoiseSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val noiseType: NoiseType,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val wasPomodoro: Boolean = false
)
