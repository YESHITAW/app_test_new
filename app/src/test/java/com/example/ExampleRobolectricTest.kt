package com.example

import com.example.data.model.AppSettings
import com.example.data.model.CallerScenario
import com.example.data.model.Greeting
import com.example.data.model.Voicemail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun testVoicemailModelCreation() {
    val voicemail = Voicemail(
      callerName = "Dr. Emily Carter",
      callerNumber = "+1 (555) 234-8901",
      timestampMillis = System.currentTimeMillis(),
      durationSeconds = 34,
      audioFilePath = "test_path.m4a",
      transcript = "Test message transcript",
      aiSummary = "Doctor follow up test summary",
      aiKeyPoints = "Point 1|Point 2",
      urgency = "HIGH",
      category = "MEDICAL"
    )

    assertEquals("Dr. Emily Carter", voicemail.callerName)
    assertEquals("+1 (555) 234-8901", voicemail.callerNumber)
    assertEquals("HIGH", voicemail.urgency)
    assertEquals("MEDICAL", voicemail.category)
    assertFalse(voicemail.isRead)
    assertFalse(voicemail.isStarred)
  }

  @Test
  fun testAppSettingsDefaults() {
    val settings = AppSettings()
    assertEquals(2, settings.ringsBeforePickup)
    assertEquals(60, settings.maxRecordingSeconds)
    assertTrue(settings.callScreeningEnabled)
    assertTrue(settings.aiSummaryEnabled)
    assertTrue(settings.spamFilterEnabled)
    assertFalse(settings.biometricLockEnabled)
  }

  @Test
  fun testGreetingPresetInitialization() {
    val greeting = Greeting(
      title = "Professional Office",
      type = "DEFAULT_PRESET",
      scriptText = "Please leave your message after the tone.",
      durationSeconds = 8,
      isActive = true,
      isPreset = true
    )

    assertTrue(greeting.isActive)
    assertTrue(greeting.isPreset)
    assertEquals("DEFAULT_PRESET", greeting.type)
  }
}
