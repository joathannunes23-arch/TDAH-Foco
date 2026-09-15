package com.example

import com.example.data.model.PomodoroMode
import com.example.ui.components.ADHD_POMODORO_PRESETS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PomodoroTimerUnitTest {

    @Test
    fun `default pomodoro presets are configured correctly`() {
        val classic = ADHD_POMODORO_PRESETS.first { it.name == "Clássico" }
        assertEquals(25, classic.workMinutes)
        assertEquals(5, classic.shortBreakMinutes)
        assertEquals(15, classic.longBreakMinutes)

        val sprint = ADHD_POMODORO_PRESETS.first { it.name == "Sprint TDAH" }
        assertEquals(15, sprint.workMinutes)
        assertEquals(3, sprint.shortBreakMinutes)
        assertEquals(10, sprint.longBreakMinutes)

        val deep = ADHD_POMODORO_PRESETS.first { it.name == "Deep Work" }
        assertEquals(50, deep.workMinutes)
        assertEquals(10, deep.shortBreakMinutes)
        assertEquals(20, deep.longBreakMinutes)
    }

    @Test
    fun `pomodoro mode labels and durations`() {
        assertEquals("Foco Total", PomodoroMode.FOCUS.label)
        assertEquals(25, PomodoroMode.FOCUS.minutes)

        assertEquals("Pausa Curta", PomodoroMode.SHORT_BREAK.label)
        assertEquals(5, PomodoroMode.SHORT_BREAK.minutes)

        assertEquals("Pausa Longa", PomodoroMode.LONG_BREAK.label)
        assertEquals(15, PomodoroMode.LONG_BREAK.minutes)
    }

    @Test
    fun `timer progress calculations`() {
        val totalSec = 25 * 60
        var timeLeftSec = 25 * 60
        var rawProgress = (timeLeftSec.toFloat() / totalSec.toFloat()).coerceIn(0f, 1f)
        var elapsedProgress = 1f - rawProgress
        assertEquals(0f, elapsedProgress, 0.001f)

        timeLeftSec = 0
        rawProgress = (timeLeftSec.toFloat() / totalSec.toFloat()).coerceIn(0f, 1f)
        elapsedProgress = 1f - rawProgress
        assertEquals(1f, elapsedProgress, 0.001f)
    }
}
