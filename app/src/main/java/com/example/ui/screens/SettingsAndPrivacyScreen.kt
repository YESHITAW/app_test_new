package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
import com.example.viewmodel.AnsweringMachineViewModel

@Composable
fun SettingsAndPrivacyScreen(
  viewModel: AnsweringMachineViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val appSettings by viewModel.appSettings.collectAsState()
  val allVoicemails by viewModel.allVoicemails.collectAsState()

  var showClearConfirmDialog by remember { mutableStateOf(false) }

  var micGranted by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    )
  }

  val micLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { granted ->
    micGranted = granted
  }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NavyDarkBackground)
      .verticalScroll(scrollState)
      .padding(16.dp)
  ) {
    // Top Section Header
    Surface(
      color = NavySurface,
      shape = RoundedCornerShape(16.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ElectricSky.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Tune, contentDescription = null, tint = ElectricSky)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "Answering Machine Settings",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = TextPrimary
          )
          Text(
            text = "Configure auto-pickup rules, recording limits, and privacy protection.",
            fontSize = 12.sp,
            color = TextSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Section 1: Call Handling & Answering Rules
    Text(
      text = "CALL HANDLING & AUTOMATION",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = AmberTape,
      letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        // Rings before pickup selector
        Text("Rings Before Answering Machine Pickup", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val ringOptions = listOf(1, 2, 4, 6)
          ringOptions.forEach { rings ->
            val isSelected = appSettings.ringsBeforePickup == rings
            Surface(
              color = if (isSelected) ElectricSky else NavySurfaceVariant,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .clickable { viewModel.updateRingsBeforePickup(rings) }
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "$rings ${if (rings == 1) "Ring" else "Rings"}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) NavyDarkBackground else TextSecondary
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Max Recording Duration
        Text("Maximum Voicemail Recording Length", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val durationOptions = listOf(30 to "30s", 60 to "60s", 120 to "2m", 300 to "5m")
          durationOptions.forEach { (sec, label) ->
            val isSelected = appSettings.maxRecordingSeconds == sec
            Surface(
              color = if (isSelected) AmberTape else NavySurfaceVariant,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .clickable { viewModel.updateMaxRecordingSeconds(sec) }
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = label,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) NavyDarkBackground else TextSecondary
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle: Call Screening
        SettingsToggleRow(
          icon = Icons.Default.Hearing,
          title = "Real-time Call Screening",
          subtitle = "Stream live audio & transcript while caller leaves message",
          checked = appSettings.callScreeningEnabled,
          onCheckedChange = { viewModel.toggleCallScreening(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Toggle: AI Transcription & Summaries
        SettingsToggleRow(
          icon = Icons.Default.AutoAwesome,
          title = "AI Transcription & Insights",
          subtitle = "Automatically extract key action items and caller urgency",
          checked = appSettings.aiSummaryEnabled,
          onCheckedChange = { viewModel.toggleAiSummaries(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Toggle: Spam Shield
        SettingsToggleRow(
          icon = Icons.Default.Shield,
          title = "Spam & Robocall Filter",
          subtitle = "Flag commercial promotional solicitations automatically",
          checked = appSettings.spamFilterEnabled,
          onCheckedChange = { viewModel.toggleSpamFilter(it) }
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Section 2: Privacy & Data Protection
    Text(
      text = "PRIVACY & LOCAL ENCRYPTION",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = AmberTape,
      letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldRecord, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Zero-Knowledge Local Storage", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "All voice messages and greetings are stored exclusively in the private sandboxed app directory. No voice recordings are uploaded to external tracking brokers.",
          fontSize = 12.sp,
          color = TextSecondary,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsToggleRow(
          icon = Icons.Default.Fingerprint,
          title = "Biometric Voicemail Lock",
          subtitle = "Require biometric / fingerprint before playing sensitive messages",
          checked = appSettings.biometricLockEnabled,
          onCheckedChange = { viewModel.toggleBiometricLock(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Storage usage summary
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Storage, contentDescription = null, tint = ElectricSky, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text("Recorded Audio Storage", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
              Text("${allVoicemails.size} voicemails stored (~${allVoicemails.size * 600 / 1024 + 1} MB)", fontSize = 11.sp, color = TextSecondary)
            }
          }

          OutlinedButton(
            onClick = { showClearConfirmDialog = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseAlarm),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Clear All", fontSize = 11.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Section 3: System Permissions & Hardware Diagnostics
    Text(
      text = "SYSTEM PERMISSIONS & COMPATIBILITY",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = AmberTape,
      letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    Card(
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        // Mic Permission Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Mic, contentDescription = null, tint = if (micGranted) EmeraldRecord else RoseAlarm, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Microphone Access", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
              Text(
                if (micGranted) "Granted • Ready for recording" else "Permission required for voice greetings",
                fontSize = 11.sp,
                color = if (micGranted) EmeraldRecord else RoseAlarm
              )
            }
          }

          if (!micGranted) {
            Button(
              onClick = { micLauncher.launch(Manifest.permission.RECORD_AUDIO) },
              colors = ButtonDefaults.buttonColors(containerColor = ElectricSky, contentColor = NavyDarkBackground),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(32.dp)
            ) {
              Text("Grant", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          } else {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldRecord, modifier = Modifier.size(20.dp))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Battery Optimization
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.BatterySaver, contentDescription = null, tint = AmberTape, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text("Power Consumption Optimization", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
            Text(
              "Background services use coroutine dispatchers to sleep during idle standby, saving battery.",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
        }
      }
    }
  }

  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      containerColor = NavySurface,
      titleContentColor = TextPrimary,
      textContentColor = TextSecondary,
      title = { Text("Delete All Voicemails?") },
      text = { Text("This will remove all saved recordings, transcripts, and AI summaries from the device.") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.clearAllVoicemails()
            showClearConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoseAlarm)
        ) {
          Text("Delete All", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirmDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}

@Composable
fun SettingsToggleRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(NavySurfaceVariant),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = ElectricSky, modifier = Modifier.size(16.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
        Text(subtitle, fontSize = 11.sp, color = TextSecondary)
      }
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = NavyDarkBackground,
        checkedTrackColor = ElectricSky,
        uncheckedThumbColor = TextMuted,
        uncheckedTrackColor = NavySurfaceVariant
      )
    )
  }
}
