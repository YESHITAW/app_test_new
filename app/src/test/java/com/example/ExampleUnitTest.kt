package com.example

import com.example.data.model.AppSettings
import com.example.data.model.CallerScenario
import com.example.data.model.Greeting
import com.example.data.model.Voicemail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun voicemail_defaults_are_correct() {
    val vm = Voicemail(
      callerName = "Dr. Emily Carter",
      callerNumber = "+1 (555) 234-8901",
      timestampMillis = 1700000000000L,
      durationSeconds = 34,
      audioFilePath = "sample_path.m4a",
      transcript = "Lab results are normal",
      aiSummary = "Doctor follow up",
      aiKeyPoints = "Point 1|Point 2",
      urgency = "HIGH",
      category = "MEDICAL"
    )
    assertEquals("HIGH", vm.urgency)
    assertEquals("MEDICAL", vm.category)
    assertFalse(vm.isRead)
    assertFalse(vm.isStarred)
  }

  @Test
  fun appSettings_defaults_are_correct() {
    val settings = AppSettings()
    assertEquals(2, settings.ringsBeforePickup)
    assertEquals(60, settings.maxRecordingSeconds)
    assertTrue(settings.callScreeningEnabled)
    assertTrue(settings.aiSummaryEnabled)
    assertTrue(settings.spamFilterEnabled)
  }

  @Test
  fun greeting_model_properties() {
    val greeting = Greeting(
      title = "Executive Quick Note",
      type = "AI_VOICE",
      scriptText = "Please leave your message.",
      durationSeconds = 7,
      isActive = true,
      isPreset = true
    )
    assertTrue(greeting.isActive)
    assertEquals("AI_VOICE", greeting.type)
  }
}
