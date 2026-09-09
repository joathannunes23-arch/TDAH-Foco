package com.example.data.repository

import com.example.data.model.MindMap
import com.example.data.model.MindMapNode
import com.example.data.model.NoiseSession
import com.example.data.model.NoiseType
import com.example.data.model.RoutineTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AdhdRepository {

    private val defaultMaps = listOf(
        MindMap(
            id = "foco-tarefas",
            title = "Foco em Tarefas Diárias",
            category = "Produtividade",
            icon = "🎯",
            scientificCitation = "Barkley, R. A. (2015). Attention-deficit hyperactivity disorder: A handbook for diagnosis and treatment. Guilford Publications.",
            scientificExplanation = "Pessoas com TDAH enfrentam sobrecarga na memória de trabalho. A externalização visual reduz a carga pré-frontal imediata em até 40%, combatendo a procrastinação por paralisia de escolha.",
            adhdNeuroTip = "Inicie pelo micro-passo de 2 minutos para liberar uma dose basal de dopamina e quebrar a barreira de inércia motora.",
            centralNodeTitle = "Missão Foco do Dia",
            nodes = listOf(
                MindMapNode("fn1", "Micro-passo de 2 minutos (Ignite Dopamina)", "⚡", false, "Início", listOf("Abrir documento", "Digitar 1 linha")),
                MindMapNode("fn2", "Chunking: 3 blocos bem definidos", "🧱", false, "Planejamento", listOf("Bloco 1", "Bloco 2", "Bloco 3")),
                MindMapNode("fn3", "Blindagem Sensorial: Ruído Marrom ativo", "🎧", true, "Ambiente", listOf("Fones colocados", "Celular longe")),
                MindMapNode("fn4", "Cronômetro Visível (25 minutos)", "⏱️", false, "Tempo", listOf("Sem olhar notificações")),
                MindMapNode("fn5", "Recompensa Instantânea pós-bloco", "🎁", false, "Dopamina", listOf("Caminhada", "Café fresco"))
            )
        ),
        MindMap(
            id = "gestao-emocoes",
            title = "Gerenciamento de Emoções",
            category = "Regulação Afetiva",
            icon = "😌",
            scientificCitation = "Ramsay, J. R., & Rostain, A. L. (2016). Cognitive-behavioral therapy for adult ADHD. Routledge.",
            scientificExplanation = "A Disforia Sensível à Rejeição (RSD) e a labilidade emocional decorrem da fraca modulação inibitória límbica. Identificar e desacelerar o circuito da amígdala restaura o autocontrole.",
            adhdNeuroTip = "A pausa de 90 segundos resfria a onda de adrenalina no corpo antes que ela se transforme em ação impulsiva.",
            centralNodeTitle = "Regulação Emocional",
            nodes = listOf(
                MindMapNode("en1", "Pausa Fisiológica (Respiração 4-7-8)", "🛑", false, "Corpo"),
                MindMapNode("en2", "Nomear o Sentimento (RSD, Fadiga ou Frustração?)", "🏷️", false, "Identificação"),
                MindMapNode("en3", "Ancoragem Auditiva com Ruído Rosa", "🌧️", true, "Áudio"),
                MindMapNode("en4", "Descarrego Mental no papel (Brain Dump)", "📝", false, "Expressão"),
                MindMapNode("en5", "Deslocamento Físico: 2 min caminhando", "🚶", false, "Movimento")
            )
        ),
        MindMap(
            id = "sono-reparador",
            title = "Rotina de Sono e Sono Profundo",
            category = "Saúde Circadiana",
            icon = "💤",
            scientificCitation = "Becker, S. P., et al. (2020). Sleep in adults with ADHD: Systematic review and meta-analysis. Sleep Medicine Reviews.",
            scientificExplanation = "Até 75% dos adultos com TDAH têm atraso de fase no ritmo circadiano da melatonina e hiperexcitação cognitiva noturna. A transição sensorial suave é essencial.",
            adhdNeuroTip = "O ruído marrom abafa variações bruscas de decibéis do ambiente e estabiliza ondas cerebrais delta.",
            centralNodeTitle = "Higiene do Sono TDAH",
            nodes = listOf(
                MindMapNode("sn1", "Toque de recolher digital (60 min antes)", "📵", false, "Luz"),
                MindMapNode("sn2", "Luzes quentes âmbar ou indiretas", "🕯️", false, "Ambiente"),
                MindMapNode("sn3", "Anotar 3 pendências para amanhã", "📋", false, "Mente"),
                MindMapNode("sn4", "Ruído Marrom programado no app", "🌊", true, "Áudio"),
                MindMapNode("sn5", "Quarto ventilado e temperatura fresca (19-21°C)", "❄️", false, "Conforto")
            )
        ),
        MindMap(
            id = "estudo-subtarefas",
            title = "Tarefas de Estudo com Subtarefas",
            category = "Aprendizagem",
            icon = "📚",
            scientificCitation = "Sweller, J. (1988). Cognitive load during problem solving. Cognitive Science, 12(2), 257-285.",
            scientificExplanation = "A teoria da carga cognitiva comprova que materiais densos saturam a memória operacional. Fatiar o estudo em micro-objetivos viabiliza o fluxo de assimilação.",
            adhdNeuroTip = "Use o método Feynman: explique o conceito com suas próprias palavras para um amigo imaginário por 2 minutos.",
            centralNodeTitle = "Estudo Descomplicado",
            nodes = listOf(
                MindMapNode("st1", "Definir 1 conceito central (Escopo fechado)", "🎯", false, "Objetivo"),
                MindMapNode("st2", "Leitura ativa: sublinhar apenas palavras-chave", "✏️", false, "Foco"),
                MindMapNode("st3", "Autoexplicação em voz alta (Feynman)", "🗣️", false, "Fixação"),
                MindMapNode("st4", "Ruído Branco ativo para isolar ruídos da casa", "💨", true, "Som"),
                MindMapNode("st5", "Flashcard de 3 perguntas para fixação", "💡", false, "Revisão")
            )
        ),
        MindMap(
            id = "reducao-ansiedade",
            title = "Redução de Ansiedade",
            category = "Bem-Estar",
            icon = "🌿",
            scientificCitation = "Zylowska, L., et al. (2012). The Mindfulness Prescription for Adult ADHD. Trumpeter Books.",
            scientificExplanation = "No TDAH, pensamentos catastróficos costumam ser tentativas inconscientes de gerar adrenalina para suprir a dopamina em baixa. Reequilibrar o tônus simpático é o antídoto.",
            adhdNeuroTip = "Tocar superfícies com texturas diferentes ou beber água bem gelada força a atenção de volta ao presente.",
            centralNodeTitle = "Descompressão Sensorial",
            nodes = listOf(
                MindMapNode("an1", "Técnica 5-4-3-2-1 dos Sentidos", "👁️", false, "Presença"),
                MindMapNode("an2", "Gole de água gelada com foco sensorial", "💧", false, "Vago"),
                MindMapNode("an3", "Ruído Mixado (70% Rosa + 30% Marrom)", "🎛️", true, "Áudio"),
                MindMapNode("an4", "Frase-âncora: 'Isto é apenas química cerebral transitória'", "🧠", false, "Crença"),
                MindMapNode("an5", "Relaxar conscientemente os ombros e a mandíbula", "✨", false, "Relax")
            )
        ),
        MindMap(
            id = "planejamento-semanal",
            title = "Planejamento Semanal",
            category = "Funções Executivas",
            icon = "📅",
            scientificCitation = "Tuckman, A. (2017). More Attention, Less Deficit: Success Strategies for Adults with ADHD. Specialty Press.",
            scientificExplanation = "A 'cegueira temporal' do TDAH faz com que prazos futuros pareçam inexistentes até virarem crises de última hora. Um mapa visual semanal cria um horizonte perceptível.",
            adhdNeuroTip = "Defina no máximo 3 prioridades essenciais para a semana inteira. O restante é bônus.",
            centralNodeTitle = "Visão da Semana",
            nodes = listOf(
                MindMapNode("pn1", "Regra dos 3: Apenas 3 grandes metas", "🏆", false, "Prioridade"),
                MindMapNode("pn2", "Bloquear horários de descanso antes do trabalho", "☕", false, "Energia"),
                MindMapNode("pn3", "Mapear dias com gargalo de reuniões", "⚠️", false, "Atenção"),
                MindMapNode("pn4", "Configurar alarmes para saídas e trocas de turno", "🔔", false, "Transições"),
                MindMapNode("pn5", "Deixar 20% do calendário livre para imprevistos", "🛡️", false, "Margem")
            )
        ),
        MindMap(
            id = "rotina-matinal",
            title = "Rotina Matinal",
            category = "Momentum Diário",
            icon = "☀️",
            scientificCitation = "Faraone, S. V., et al. (2021). The World Federation of ADHD International Consensus Statement. Neuroscience & Biobehavioral Reviews.",
            scientificExplanation = "O cérebro neurodivergente acorda com baixo nível de ativação do córtex pré-frontal. Evitar decisões complexas nas primeiras horas preserva energia mental.",
            adhdNeuroTip = "Luz natural nos olhos logo ao acordar ajusta a curva de cortisol e desativa a inércia do sono.",
            centralNodeTitle = "Despertar com Momentum",
            nodes = listOf(
                MindMapNode("mn1", "500ml de água antes de olhar telas", "💧", true, "Hidratação"),
                MindMapNode("mn2", "5 minutos de exposição à luz do sol", "☀️", false, "Circadiano"),
                MindMapNode("mn3", "Café da manhã rico em proteínas", "🍳", false, "Nutrição"),
                MindMapNode("mn4", "Play no Ruído Marrom ao sentar para trabalhar", "🌊", false, "Foco"),
                MindMapNode("mn5", "Abrir o mapa mental do dia no app", "📱", true, "Direção")
            )
        ),
        MindMap(
            id = "rotina-noturna",
            title = "Rotina Noturna",
            category = "Desaceleração",
            icon = "🌙",
            scientificCitation = "Van der Heijden, K. B., et al. (2018). Sensory modulation and evening settling in neurodevelopmental conditions. Pediatric Sleep Medicine.",
            scientificExplanation = "O estado de hiperfoco noturno é comum no TDAH. Sem um gatilho ambiental de desaceleração, o cérebro mantém a ruminação criativa de madrugada.",
            adhdNeuroTip = "Um banho morno auxilia a queda da temperatura corporal interna, sinal primordial de indução do sono.",
            centralNodeTitle = "Desaceleração Noturna",
            nodes = listOf(
                MindMapNode("nn1", "Guardar a mesa de trabalho (Redução de pistas visuais)", "🧹", false, "Visual"),
                MindMapNode("nn2", "Banho morno relaxante", "🚿", false, "Temperatura"),
                MindMapNode("nn3", "Leitura de ficção ou livro não-estimulante", "📖", false, "Mente"),
                MindMapNode("nn4", "Chá de ervas sem cafeína (camomila/melissa)", "🫖", false, "Calma"),
                MindMapNode("nn5", "Ativar Ruído Marrom por 60 minutos", "🎧", true, "Sono")
            )
        )
    )

    private val initialRoutines = listOf(
        RoutineTask("r1", "Tomar 500ml de água ao acordar", true, "Manhã", "💧"),
        RoutineTask("r2", "Luz natural nos olhos por 5 min", true, "Manhã", "☀️"),
        RoutineTask("r3", "Definir a meta única do dia no mapa mental", true, "Manhã", "🎯"),
        RoutineTask("r4", "1ª Sessão Pomodoro (25m) com Ruído Marrom", false, "Tarde", "🌊"),
        RoutineTask("r5", "Pausa ativa para alongamento e hidratação", false, "Tarde", "🧘"),
        RoutineTask("r6", "2ª Sessão de foco com Ruído Rosa", false, "Tarde", "🌧️"),
        RoutineTask("r7", "Esvaziar pendências da mente (Brain Dump)", false, "Noite", "📝"),
        RoutineTask("r8", "Desligar telas e ativar ruído relaxante", false, "Noite", "🌙")
    )

    private val _libraryMaps = MutableStateFlow<List<MindMap>>(defaultMaps)
    val libraryMaps: StateFlow<List<MindMap>> = _libraryMaps.asStateFlow()

    private val _activeMindMap = MutableStateFlow<MindMap>(defaultMaps.first())
    val activeMindMap: StateFlow<MindMap> = _activeMindMap.asStateFlow()

    private val _routineTasks = MutableStateFlow<List<RoutineTask>>(initialRoutines)
    val routineTasks: StateFlow<List<RoutineTask>> = _routineTasks.asStateFlow()

    private val _historySessions = MutableStateFlow<List<NoiseSession>>(
        listOf(
            NoiseSession(noiseType = NoiseType.BROWN, durationMinutes = 25, wasPomodoro = true),
            NoiseSession(noiseType = NoiseType.MIXED, durationMinutes = 50, wasPomodoro = true),
            NoiseSession(noiseType = NoiseType.PINK, durationMinutes = 20, wasPomodoro = false),
            NoiseSession(noiseType = NoiseType.WHITE, durationMinutes = 30, wasPomodoro = false)
        )
    )
    val historySessions: StateFlow<List<NoiseSession>> = _historySessions.asStateFlow()

    private val _userName = MutableStateFlow("Alex")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _autoNoisePomodoro = MutableStateFlow(true)
    val autoNoisePomodoro: StateFlow<Boolean> = _autoNoisePomodoro.asStateFlow()

    private val _selectedPomodoroNoise = MutableStateFlow(NoiseType.BROWN)
    val selectedPomodoroNoise: StateFlow<NoiseType> = _selectedPomodoroNoise.asStateFlow()

    fun setUserName(name: String) {
        if (name.isNotBlank()) {
            _userName.value = name
        }
    }

    fun setAutoNoisePomodoro(enabled: Boolean) {
        _autoNoisePomodoro.value = enabled
    }

    fun setSelectedPomodoroNoise(noiseType: NoiseType) {
        _selectedPomodoroNoise.value = noiseType
    }

    fun selectActiveMap(mapId: String) {
        val found = _libraryMaps.value.find { it.id == mapId }
        if (found != null) {
            _activeMindMap.value = found
        }
    }

    fun toggleFavorite(mapId: String) {
        _libraryMaps.value = _libraryMaps.value.map { map ->
            if (map.id == mapId) map.copy(isFavorite = !map.isFavorite) else map
        }
        if (_activeMindMap.value.id == mapId) {
            _activeMindMap.value = _activeMindMap.value.copy(isFavorite = !_activeMindMap.value.isFavorite)
        }
    }

    fun toggleNodeCompletion(nodeId: String) {
        val current = _activeMindMap.value
        val updatedNodes = current.nodes.map { node ->
            if (node.id == nodeId) node.copy(isCompleted = !node.isCompleted) else node
        }
        val updatedMap = current.copy(nodes = updatedNodes)
        _activeMindMap.value = updatedMap
        _libraryMaps.value = _libraryMaps.value.map { if (it.id == updatedMap.id) updatedMap else it }
    }

    fun addNodeToActiveMap(text: String, icon: String = "💡", tag: String = "Passo") {
        val current = _activeMindMap.value
        val newNode = MindMapNode(
            id = UUID.randomUUID().toString(),
            text = text,
            icon = icon,
            isCompleted = false,
            tag = tag
        )
        val updatedMap = current.copy(nodes = current.nodes + newNode)
        _activeMindMap.value = updatedMap
        _libraryMaps.value = _libraryMaps.value.map { if (it.id == updatedMap.id) updatedMap else it }
    }

    fun removeNodeFromActiveMap(nodeId: String) {
        val current = _activeMindMap.value
        val updatedMap = current.copy(nodes = current.nodes.filterNot { it.id == nodeId })
        _activeMindMap.value = updatedMap
        _libraryMaps.value = _libraryMaps.value.map { if (it.id == updatedMap.id) updatedMap else it }
    }

    fun updateActiveMapBackground(colorHex: String) {
        val updated = _activeMindMap.value.copy(backgroundColor = colorHex)
        _activeMindMap.value = updated
        _libraryMaps.value = _libraryMaps.value.map { if (it.id == updated.id) updated else it }
    }

    fun toggleTask(taskId: String) {
        _routineTasks.value = _routineTasks.value.map { task ->
            if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
        }
    }

    fun addTask(title: String, period: String = "Manhã", icon: String = "⚡") {
        val newTask = RoutineTask(
            id = UUID.randomUUID().toString(),
            title = title,
            isCompleted = false,
            period = period,
            icon = icon
        )
        _routineTasks.value = _routineTasks.value + newTask
    }

    fun deleteTask(taskId: String) {
        _routineTasks.value = _routineTasks.value.filterNot { it.id == taskId }
    }

    fun recordSession(noiseType: NoiseType, minutes: Int, wasPomodoro: Boolean) {
        if (minutes <= 0) return
        val session = NoiseSession(
            noiseType = noiseType,
            durationMinutes = minutes,
            wasPomodoro = wasPomodoro
        )
        _historySessions.value = listOf(session) + _historySessions.value
    }

    fun generateScientificMindMap(theme: String): MindMap {
        val cleanTheme = theme.ifBlank { "Foco e Regulação Cognitiva" }
        val id = "ia-map-" + System.currentTimeMillis()
        val newMap = MindMap(
            id = id,
            title = "Mapa: $cleanTheme",
            category = "Neurociência Aplicada",
            icon = "🧠",
            scientificCitation = "Estudos de Neuroplasticidade & TDAH (Barkley, 2024; Söderlund, 2023).",
            scientificExplanation = "Este mapa para '$cleanTheme' divide a demanda em nós modulares com baixo atrito de inicialização, blindando contra sobrecarga sensorial e desvio impulsivo de atenção.",
            adhdNeuroTip = "Execute o nó 1 em menos de 120 segundos. Não avalie a qualidade do trabalho antes do término do 1º bloco de ruído.",
            centralNodeTitle = cleanTheme,
            nodes = listOf(
                MindMapNode(UUID.randomUUID().toString(), "Ativação em 120s: Ação física imediata", "⚡", false, "Início rápido"),
                MindMapNode(UUID.randomUUID().toString(), "Remover distrações visuais da bancada", "🛡️", false, "Ambiente"),
                MindMapNode(UUID.randomUUID().toString(), "Foco em 1 único micro-resultado", "🎯", false, "Foco"),
                MindMapNode(UUID.randomUUID().toString(), "Sessão de Ruído Marrom contínuo", "🌊", true, "Áudio"),
                MindMapNode(UUID.randomUUID().toString(), "Pausa sensorial de 5 min pós-tarefa", "☕", false, "Recompensa")
            ),
            isCustom = true
        )
        _libraryMaps.value = listOf(newMap) + _libraryMaps.value
        _activeMindMap.value = newMap
        return newMap
    }
}
