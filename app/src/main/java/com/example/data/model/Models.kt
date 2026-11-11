package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voicemails")
data class Voicemail(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val callerName: String,
  val callerNumber: String,
  val timestampMillis: Long,
  val durationSeconds: Int,
  val audioFilePath: String,
  val transcript: String,
  val aiSummary: String,
  val aiKeyPoints: String, // Pipe separated list of bullet items
  val urgency: String, // "HIGH", "MEDIUM", "LOW"
  val category: String, // "WORK", "PERSONAL", "MEDICAL", "URGENT", "SPAM"
  val isRead: Boolean = false,
  val isStarred: Boolean = false,
  val callerAvatarColorHex: Long = 0xFF38BDF8
)

@Entity(tableName = "greetings")
data class Greeting(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val type: String, // "AI_VOICE", "USER_RECORDED", "DEFAULT_PRESET"
  val scriptText: String,
  val audioFilePath: String = "",
  val durationSeconds: Int = 10,
  val isActive: Boolean = false,
  val isPreset: Boolean = false
)

data class CallerScenario(
  val id: String,
  val callerName: String,
  val callerNumber: String,
  val callerCategory: String,
  val initialSpokenMessage: String,
  val expectedTranscript: String,
  val expectedSummary: String,
  val expectedKeyPoints: List<String>,
  val urgency: String,
  val avatarColorHex: Long
)

data class AppSettings(
  val ringsBeforePickup: Int = 2,
  val maxRecordingSeconds: Int = 60,
  val callScreeningEnabled: Boolean = true,
  val aiTranscriptionEnabled: Boolean = true,
  val aiSummaryEnabled: Boolean = true,
  val spamFilterEnabled: Boolean = true,
  val biometricLockEnabled: Boolean = false,
  val beepToneVolume: Float = 0.85f
)
