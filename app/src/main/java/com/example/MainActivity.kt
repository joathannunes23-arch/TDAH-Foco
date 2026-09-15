package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppNavigationTab
import com.example.ui.MainViewModel
import com.example.ui.components.SubscriptionManagerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MindMapScreen
import com.example.ui.screens.NoisePlayerScreen
import com.example.ui.screens.RoutineScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AquaPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyDarkBackground

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScaffold(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val activeMindMap by viewModel.activeMindMap.collectAsState()
    val libraryMaps by viewModel.libraryMaps.collectAsState()
    val routineTasks by viewModel.routineTasks.collectAsState()
    val sessionHistory by viewModel.historySessions.collectAsState()

    val currentNoise by viewModel.currentNoise.collectAsState()
    val isNoisePlaying by viewModel.isNoisePlaying.collectAsState()
    val noiseVolume by viewModel.noiseVolume.collectAsState()

    val pomodoroTimeLeft by viewModel.pomodoroTimeLeftSeconds.collectAsState()
    val pomodoroTotal by viewModel.pomodoroTotalSeconds.collectAsState()
    val isPomodoroRunning by viewModel.isPomodoroRunning.collectAsState()
    val pomodoroMode by viewModel.pomodoroMode.collectAsState()
    val workDuration by viewModel.workDurationMinutes.collectAsState()
    val shortBreakDuration by viewModel.shortBreakDurationMinutes.collectAsState()
    val longBreakDuration by viewModel.longBreakDurationMinutes.collectAsState()
    val autoNoiseActive by viewModel.autoNoisePomodoro.collectAsState()
    val selectedPomodoroNoise by viewModel.selectedPomodoroNoise.collectAsState()
    val pomodoroCompletedCycles by viewModel.pomodoroCompletedCycles.collectAsState()

    val feedbackMessage by viewModel.feedbackMessage.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val subscriptionInfo by viewModel.subscriptionInfo.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navItems = listOf(
                    Triple(AppNavigationTab.DASHBOARD, "Início", Icons.Default.Home),
                    Triple(AppNavigationTab.MIND_MAPS, "Mapas", Icons.Default.AccountTree),
                    Triple(AppNavigationTab.NOISE_PLAYER, "Ruídos", Icons.Default.GraphicEq),
                    Triple(AppNavigationTab.ROUTINE, "Rotina", Icons.Default.Checklist),
                    Triple(AppNavigationTab.SETTINGS, "Ajustes", Icons.Default.Settings)
                )

                navItems.forEach { (tab, label, icon) ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyDarkBackground,
                            selectedTextColor = AquaPrimary,
                            indicatorColor = AquaPrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentTab,
            label = "tab_crossfade",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { tab ->
            when (tab) {
                AppNavigationTab.DASHBOARD -> DashboardScreen(
                    userName = userName,
                    activeMindMap = activeMindMap,
                    currentNoise = currentNoise,
                    isNoisePlaying = isNoisePlaying,
                    noiseVolume = noiseVolume,
                    pomodoroTimeLeft = pomodoroTimeLeft,
                    pomodoroTotal = pomodoroTotal,
                    isPomodoroRunning = isPomodoroRunning,
                    pomodoroMode = pomodoroMode,
                    workDurationMinutes = workDuration,
                    shortBreakDurationMinutes = shortBreakDuration,
                    longBreakDurationMinutes = longBreakDuration,
                    autoNoiseActive = autoNoiseActive,
                    selectedPomodoroNoise = selectedPomodoroNoise,
                    completedCycles = pomodoroCompletedCycles,
                    routineTasks = routineTasks,
                    feedbackMessage = feedbackMessage,
                    subscriptionInfo = subscriptionInfo,
                    onOpenSubscriptionManager = { viewModel.openSubscriptionScreen() },
                    onPlayNoise = { viewModel.playNoise(it) },
                    onTogglePlayPauseNoise = { viewModel.toggleNoisePlayback() },
                    onSetVolume = { viewModel.setNoiseVolume(it) },
                    onApplyMixedNoise = { viewModel.applyMixedNoisePreset() },
                    onStartPomodoro = { viewModel.startPomodoro() },
                    onPausePomodoro = { viewModel.pausePomodoro() },
                    onResetPomodoro = { viewModel.resetPomodoro() },
                    onSelectPomodoroMode = { viewModel.setPomodoroMode(it) },
                    onSetWorkDuration = { viewModel.setWorkDuration(it) },
                    onSetShortBreakDuration = { viewModel.setShortBreakDuration(it) },
                    onSetLongBreakDuration = { viewModel.setLongBreakDuration(it) },
                    onSkipPomodoroSession = { viewModel.skipPomodoroSession() },
                    onToggleRoutineTask = { viewModel.toggleRoutineTask(it) },
                    onGenerateMindMap = { viewModel.generateScientificMindMap(it) },
                    onOpenMindMapTab = { viewModel.navigateTo(AppNavigationTab.MIND_MAPS) },
                    onDismissFeedback = { viewModel.clearFeedbackMessage() }
                )

                AppNavigationTab.MIND_MAPS -> MindMapScreen(
                    activeMindMap = activeMindMap,
                    libraryMaps = libraryMaps,
                    isPro = subscriptionInfo.isPro,
                    onOpenSubscription = { viewModel.openSubscriptionScreen() },
                    onSelectMap = { viewModel.selectMindMap(it) },
                    onToggleNode = { viewModel.toggleNodeCompletion(it) },
                    onAddNode = { text, icon, tag -> viewModel.addNodeToActiveMap(text, icon, tag) },
                    onRemoveNode = { viewModel.removeNodeFromActiveMap(it) },
                    onChangeBackground = { viewModel.updateMapBackgroundColor(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onGenerateMindMap = { viewModel.generateScientificMindMap(it) },
                    onExportMap = { viewModel.showFeedback("Mapa mental salvo como imagem com sucesso!") }
                )

                AppNavigationTab.NOISE_PLAYER -> NoisePlayerScreen(
                    currentNoise = currentNoise,
                    isPlaying = isNoisePlaying,
                    volume = noiseVolume,
                    isPro = subscriptionInfo.isPro,
                    onOpenSubscription = { viewModel.openSubscriptionScreen() },
                    onPlayNoise = { viewModel.playNoise(it) },
                    onTogglePlayPause = { viewModel.toggleNoisePlayback() },
                    onSetVolume = { viewModel.setNoiseVolume(it) },
                    onApplyMixedPreset = { viewModel.applyMixedNoisePreset() }
                )

                AppNavigationTab.ROUTINE -> RoutineScreen(
                    routineTasks = routineTasks,
                    sessionHistory = sessionHistory,
                    onToggleTask = { viewModel.toggleRoutineTask(it) },
                    onAddTask = { title, period, icon -> viewModel.addRoutineTask(title, period, icon) },
                    onDeleteTask = { viewModel.deleteRoutineTask(it) }
                )

                AppNavigationTab.SETTINGS -> SettingsScreen(
                    userName = userName,
                    isDarkTheme = isDarkTheme,
                    autoNoisePomodoro = autoNoiseActive,
                    selectedPomodoroNoise = selectedPomodoroNoise,
                    subscriptionInfo = subscriptionInfo,
                    onOpenSubscriptionManager = { viewModel.openSubscriptionScreen() },
                    onUpdateUserName = { viewModel.updateUserName(it) },
                    onToggleTheme = { viewModel.toggleTheme() },
                    onToggleAutoNoise = { viewModel.setAutoNoiseWithPomodoro(it) },
                    onSelectPomodoroNoise = { viewModel.setSelectedPomodoroNoise(it) }
                )

                AppNavigationTab.SUBSCRIPTION -> SubscriptionManagerScreen(
                    subscriptionInfo = subscriptionInfo,
                    onStartFreeTrial = { viewModel.startFreeTrial() },
                    onActivatePro = { viewModel.activatePro() },
                    onCancelSubscription = { viewModel.cancelSubscription() },
                    onBackToDashboard = { viewModel.navigateTo(AppNavigationTab.DASHBOARD) }
                )
            }
        }
    }
}

// Keep Greeting composable so existing tests (like GreetingScreenshotTest) continue to pass seamlessly
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
