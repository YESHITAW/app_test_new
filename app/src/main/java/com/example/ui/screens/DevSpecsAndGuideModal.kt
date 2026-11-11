package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberTape
import com.example.ui.theme.ElectricSky
import com.example.ui.theme.EmeraldRecord
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceCard
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.RoseAlarm
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DevSpecsAndGuideScreen(
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(0) }
  val tabs = listOf("Technical Specs", "User Guide", "Marketing Strategy")
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NavyDarkBackground)
  ) {
    ScrollableTabRow(
      selectedTabIndex = selectedTab,
      containerColor = NavySurface,
      contentColor = ElectricSky,
      edgePadding = 16.dp,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = ElectricSky
        )
      }
    ) {
      tabs.forEachIndexed { index, title ->
        Tab(
          selected = selectedTab == index,
          onClick = { selectedTab = index },
          text = {
            Text(
              text = title,
              fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
              fontSize = 13.sp
            )
          },
          selectedContentColor = ElectricSky,
          unselectedContentColor = TextMuted
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(16.dp)
    ) {
      when (selectedTab) {
        0 -> TechnicalSpecsTab()
        1 -> UserGuideTab()
        2 -> MarketingStrategyTab()
      }
    }
  }
}

@Composable
private fun TechnicalSpecsTab() {
  Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    // Header
    Surface(
      color = NavySurface,
      shape = RoundedCornerShape(14.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Code, contentDescription = null, tint = ElectricSky, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text("System Architecture & Specifications", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
          Text("Answering Machine Engine v1.0 • Android SDK 36", fontSize = 11.sp, color = TextSecondary)
        }
      }
    }

    // Spec Item 1: Audio Processing Pipeline
    SpecSectionCard(
      title = "1. Audio Ingestion & Playback Pipeline",
      icon = Icons.Default.Mic,
      iconColor = RoseAlarm
    ) {
      Text(
        text = "• Ingestion: MediaRecorder configured for AAC Audio Encoder (MPEG-4 Container, 44.1 kHz sample rate, 64 kbps bit rate). Real-time decibel tracking via maxAmplitude polling (20 * log10(amp)).\n" +
          "• Answering Machine Tone: ToneGenerator(STREAM_MUSIC) emitting standard DTMF / 1000 Hz TONE_PROP_BEEP for 350ms to signal recording initiation.\n" +
          "• Playback: MediaPlayer with AudioAttributes(CONTENT_TYPE_SPEECH, USAGE_MEDIA). Hardware playbackParams speed regulation (1.0x, 1.25x, 1.5x, 2.0x).\n" +
          "• Speech Synthesis: Native TextToSpeech engine with customizable Pitch and SpeechRate multipliers.",
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 18.sp
      )
    }

    // Spec Item 2: Telecom Subsystem Hooks
    SpecSectionCard(
      title = "2. Telecom & Call Screening Integration",
      icon = Icons.Default.PhoneInTalk,
      iconColor = AmberTape
    ) {
      Text(
        text = "• CallScreeningService: Android Manifest binding for android.telecom.CallScreeningService. Allows system to query Answering Machine before presenting system incoming call UI.\n" +
          "• InCallService & ConnectionService: Hooks for intercepting incoming audio streams on carrier-supported devices.\n" +
          "• Simulation Engine: Realistic mock telecom state machine facilitating complete functional testing of auto-pickup, greeting broadcast, tone emission, and caller voice recording.",
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 18.sp
      )
    }

    // Spec Item 3: Room Database Schema
    SpecSectionCard(
      title = "3. Room Database & Local Security",
      icon = Icons.Default.Security,
      iconColor = EmeraldRecord
    ) {
      Text(
        text = "• Entity 'voicemails': id (PK Auto), callerName (Text), callerNumber (Text), timestampMillis (Long), durationSeconds (Int), audioFilePath (Text), transcript (Text), aiSummary (Text), aiKeyPoints (Text), urgency (Text), category (Text), isRead (Bool), isStarred (Bool).\n" +
          "• Entity 'greetings': id (PK Auto), title (Text), type (AI_VOICE | USER_RECORDED | DEFAULT_PRESET), scriptText (Text), audioFilePath (Text), durationSeconds (Int), isActive (Bool).\n" +
          "• Zero-Trust Privacy: Audio files stored strictly in private context.filesDir/recordings/. No cloud telemetry or third-party ad network transmission.",
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 18.sp,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}

@Composable
private fun UserGuideTab() {
  Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Surface(
      color = NavySurface,
      shape = RoundedCornerShape(14.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.MenuBook, contentDescription = null, tint = AmberTape, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text("End-User Operating Manual", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
          Text("How to configure greetings, screen calls, and listen to voicemails", fontSize = 11.sp, color = TextSecondary)
        }
      }
    }

    GuideStepCard(
      step = "1",
      title = "Configure or Record Your Voice Greeting",
      description = "Navigate to the 'Greetings' tab. You can either select from high-clarity preset greetings (Professional, Casual, Vacation), record your personalized voice with the live decibel microphone studio, or use the AI Voice Synthesizer to convert custom scripts to speech."
    )

    GuideStepCard(
      step = "2",
      title = "Test With the Incoming Call Simulator",
      description = "Open the 'Simulator' tab to test how your answering machine answers incoming calls. Select a scenario (such as Clinic, Project Lead, or Delivery Courier) or type a custom caller script. Watch the call ring, hear the active greeting broadcast, listen for the beep, and review the live recording."
    )

    GuideStepCard(
      step = "3",
      title = "Screen Calls in Real Time",
      description = "While an incoming caller is leaving a message, you can listen in live with 'Call Screening' enabled. View the live transcribed text on screen as the caller speaks, and choose to break in or let them finish their message."
    )

    GuideStepCard(
      step = "4",
      title = "Review Voicemails, AI Summaries & Actions",
      description = "In the 'Inbox' tab, browse your voice recordings. The AI automatically generates an instant summary, urgency rating (High/Medium/Low), and bulleted key points. Use the 1x/1.5x/2x speed controls, scrub through the audio wave, and tap 'Call Back' or 'Reply SMS' instantly."
    )
  }
}

@Composable
private fun MarketingStrategyTab() {
  Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Surface(
      color = NavySurface,
      shape = RoundedCornerShape(14.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Campaign, contentDescription = null, tint = EmeraldRecord, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text("Go-To-Market & Growth Strategy", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
          Text("Positioning, target personas, value propositions & ASO", fontSize = 11.sp, color = TextSecondary)
        }
      }
    }

    SpecSectionCard(
      title = "Value Proposition & Brand Positioning",
      icon = Icons.Default.AutoAwesome,
      iconColor = ElectricSky
    ) {
      Text(
        text = "\"Answering Machine: The Classic Tactile Voice Attendant, Reimagined for the Modern Executive.\"\n\n" +
          "Unlike generic carrier voicemails that require dial-in menus and dial tones, Answering Machine gives users instant visual summaries, live call screening (listen while they speak), and AI-generated action items—all with 100% on-device privacy.",
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 18.sp
      )
    }

    SpecSectionCard(
      title = "Target Audience Personas",
      icon = Icons.Default.Hearing,
      iconColor = AmberTape
    ) {
      Text(
        text = "1. Busy Executives & Founders: Receives 20+ unscheduled calls daily; needs instant AI bulleted summaries and urgency scores to triage call-backs without listening to 3-minute messages.\n\n" +
          "2. Independent Contractors & SMB Owners: Often working with loud machinery or with clients; relies on custom greetings and call screening so clients feel cared for without interrupting physical work.\n\n" +
          "3. Privacy-Conscious Users: Reject Big Tech cloud voicemail uploaders and demand on-device local sandbox recording.",
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 18.sp
      )
    }

    SpecSectionCard(
      title = "App Store Optimization (ASO) & Acquisition Channels",
      icon = Icons.Default.TrendingUp,
      iconColor = EmeraldRecord
    ) {
      Text(
        text = "• Primary Keywords: AI Answering Machine, Call Screening App, Visual Voicemail, Auto Attendant, Voice Greeting Studio, Robocall Spam Shield.\n" +
          "• Launch Strategy: Release freemium edition on Google Play with pre-configured simulator and unlimited on-device greetings. Feature spotlight on tech publications (Product Hunt, Android Police) highlighting live call screening and AI action extraction.",
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 18.sp
      )
    }
  }
}

@Composable
private fun SpecSectionCard(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconColor: Color,
  content: @Composable () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
      }
      Spacer(modifier = Modifier.height(10.dp))
      content()
    }
  }
}

@Composable
private fun GuideStepCard(
  step: String,
  title: String,
  description: String
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(modifier = Modifier.padding(16.dp)) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(ElectricSky.copy(alpha = 0.2f))
          .border(1.5.dp, ElectricSky, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(step, fontWeight = FontWeight.Bold, color = ElectricSky, fontSize = 14.sp)
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(description, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
      }
    }
  }
}
