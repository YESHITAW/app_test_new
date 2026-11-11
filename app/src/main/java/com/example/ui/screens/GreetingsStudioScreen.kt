package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.Greeting
import com.example.ui.components.SoundWaveVisualizer
import com.example.ui.components.TapeCassetteReel
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
fun GreetingsStudioScreen(
  viewModel: AnsweringMachineViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val greetings by viewModel.allGreetings.collectAsState()
  val activeGreeting by viewModel.activeGreeting.collectAsState()
  val studioState by viewModel.studioState.collectAsState()

  var selectedTab by remember { mutableStateOf(0) } // 0: Library & Record, 1: AI TTS Synthesizer
  var recordedGreetingTitle by remember { mutableStateOf("") }
  var ttsGreetingTitle by remember { mutableStateOf("Smart AI Greeting") }

  // Permission launcher for microphone recording
  val recordAudioLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      viewModel.startRecordingGreeting()
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NavyDarkBackground)
  ) {
    // Active Greeting Banner
    Surface(
      color = NavySurface,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      shape = RoundedCornerShape(16.dp),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricSky)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            TapeCassetteReel(isSpinning = studioState.isPlayingPreview)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "CURRENT BROADCAST GREETING",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricSky,
                letterSpacing = 1.sp
              )
              Text(
                text = activeGreeting?.title ?: "Default Greeting",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
              )
            }
          }

          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(ElectricSky)
              .clickable {
                activeGreeting?.let { viewModel.playGreetingScriptAudio(it) }
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.VolumeUp,
              contentDescription = "Test Audio",
              tint = NavyDarkBackground,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "\"${activeGreeting?.scriptText ?: "Hello, please leave a message..."}\"",
          fontSize = 12.sp,
          color = TextSecondary,
          lineHeight = 17.sp
        )
      }
    }

    // Tabs: Library / Record vs AI Synthesizer
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = NavySurface,
      contentColor = ElectricSky,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = ElectricSky
        )
      }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Record & Library", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
        selectedContentColor = ElectricSky,
        unselectedContentColor = TextMuted
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("AI Voice Synthesizer", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
        selectedContentColor = ElectricSky,
        unselectedContentColor = TextMuted
      )
    }

    if (selectedTab == 0) {
      // Tab 0: Library & Live Microphone Recorder
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Record New Voice Section
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (studioState.isRecording) RoseAlarm else NavyBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(RoseAlarm.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Mic, contentDescription = null, tint = RoseAlarm, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text("Record Voice Greeting", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                  Text("Capture your own voice for callers with studio audio quality", fontSize = 11.sp, color = TextSecondary)
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              if (studioState.isRecording) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = String.format("Recording: %02d:%02d", studioState.recordingSeconds / 60, studioState.recordingSeconds % 60),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = RoseAlarm
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  SoundWaveVisualizer(isAnimating = true, barCount = 20, heightDp = 30, primaryColor = RoseAlarm)
                  Spacer(modifier = Modifier.height(12.dp))
                  Button(
                    onClick = { viewModel.stopRecordingGreeting() },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseAlarm),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Stop Recording")
                  }
                }
              } else if (studioState.recordedFile != null) {
                // Review & Save recorded file
                Column(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text("Voice Recorded (${studioState.recordingSeconds}s)", fontWeight = FontWeight.Bold, color = EmeraldRecord)
                  Spacer(modifier = Modifier.height(8.dp))
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                      onClick = {
                        if (studioState.isPlayingPreview) viewModel.stopGreetingPreview()
                        else viewModel.playGreetingPreview(studioState.recordedFile)
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = ElectricSky, contentColor = NavyDarkBackground),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Icon(
                        if (studioState.isPlayingPreview) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(if (studioState.isPlayingPreview) "Pause" else "Test Playback", fontSize = 12.sp)
                    }

                    OutlinedButton(
                      onClick = { viewModel.startRecordingGreeting() },
                      colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Text("Re-record", fontSize = 12.sp)
                    }
                  }

                  Spacer(modifier = Modifier.height(12.dp))
                  OutlinedTextField(
                    value = recordedGreetingTitle,
                    onValueChange = { recordedGreetingTitle = it },
                    placeholder = { Text("Greeting Title (e.g. My Personal Note)", color = TextMuted, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = ElectricSky,
                      unfocusedBorderColor = NavyBorder,
                      focusedTextColor = TextPrimary,
                      unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                  )
                  Spacer(modifier = Modifier.height(10.dp))
                  Button(
                    onClick = {
                      viewModel.saveRecordedGreeting(recordedGreetingTitle)
                      recordedGreetingTitle = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldRecord, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save & Set Active", fontWeight = FontWeight.Bold)
                  }
                }
              } else {
                Button(
                  onClick = {
                    val hasPermission = ContextCompat.checkSelfPermission(
                      context,
                      Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                      viewModel.startRecordingGreeting()
                    } else {
                      recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = AmberTape, contentColor = NavyDarkBackground),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("start_record_greeting_btn")
                ) {
                  Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Tap to Record Custom Voice Greeting", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
              }
            }
          }
        }

        // Library of Greetings
        item {
          Text(
            text = "Saved Greetings Library",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = TextPrimary
          )
        }

        items(greetings, key = { it.id }) { greeting ->
          val isCurrentActive = greeting.id == activeGreeting?.id
          GreetingItemCard(
            greeting = greeting,
            isActive = isCurrentActive,
            onSetActive = { viewModel.setActiveGreeting(greeting.id) },
            onPlay = { viewModel.playGreetingScriptAudio(greeting) },
            onDelete = { viewModel.deleteGreeting(greeting) }
          )
        }
      }
    } else {
      // Tab 1: AI Text-to-Speech Voice Synthesizer
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricSky, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Greeting Script Generator", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Select a preset script or type your custom message to synthesize high-clarity voice greetings.",
                fontSize = 12.sp,
                color = TextSecondary
              )

              Spacer(modifier = Modifier.height(14.dp))

              // Preset Quick Buttons
              Text("Quick Script Templates:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AmberTape)
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                val templates = listOf(
                  "Professional" to "Hello, you have reached Alex Johnson. I am currently unavailable. Please leave your details after the beep.",
                  "Casual" to "Hey! Alex here. Can't take your call right now, but leave a message and I'll get right back to you!",
                  "Out of Office" to "Hi, I am currently out of the office on leave. For urgent matters, email support@company.com."
                )
                templates.forEach { (label, script) ->
                  Surface(
                    color = NavySurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { viewModel.updateTtsScript(script) }
                  ) {
                    Text(
                      text = label,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Medium,
                      color = ElectricSky,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              OutlinedTextField(
                value = studioState.ttsInputText,
                onValueChange = { viewModel.updateTtsScript(it) },
                label = { Text("Voice Greeting Script", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = ElectricSky,
                  unfocusedBorderColor = NavyBorder,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary
                )
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Voice Pitch & Speed Controls
              Text("Voice Tone & Speech Rate", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
              Spacer(modifier = Modifier.height(4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Pitch: ${String.format("%.1f", studioState.ttsPitch)}x", fontSize = 11.sp, color = TextSecondary)
              }
              Slider(
                value = studioState.ttsPitch,
                onValueChange = { viewModel.updateTtsPitch(it) },
                valueRange = 0.7f..1.4f,
                colors = SliderDefaults.colors(thumbColor = ElectricSky, activeTrackColor = ElectricSky)
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Speed: ${String.format("%.1f", studioState.ttsSpeed)}x", fontSize = 11.sp, color = TextSecondary)
              }
              Slider(
                value = studioState.ttsSpeed,
                onValueChange = { viewModel.updateTtsSpeed(it) },
                valueRange = 0.7f..1.3f,
                colors = SliderDefaults.colors(thumbColor = AmberTape, activeTrackColor = AmberTape)
              )

              Spacer(modifier = Modifier.height(12.dp))

              // Test Preview & Save Actions
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Button(
                  onClick = {
                    if (studioState.isTtsSpeaking) viewModel.stopTtsVoice()
                    else viewModel.previewTtsVoice()
                  },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = if (studioState.isTtsSpeaking) RoseAlarm else ElectricSky,
                    contentColor = NavyDarkBackground
                  ),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(
                    imageVector = if (studioState.isTtsSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(if (studioState.isTtsSpeaking) "Stop Preview" else "Preview Voice", fontWeight = FontWeight.Bold)
                }

                Button(
                  onClick = {
                    viewModel.saveTtsGreeting(ttsGreetingTitle)
                    selectedTab = 0
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = EmeraldRecord, contentColor = Color.White),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Save as Active", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun GreetingItemCard(
  greeting: Greeting,
  isActive: Boolean,
  onSetActive: () -> Unit,
  onPlay: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isActive) ElectricSky else NavyBorder
    ),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(if (isActive) ElectricSky.copy(alpha = 0.2f) else NavySurfaceVariant),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (greeting.type == "AI_VOICE") Icons.Default.AutoAwesome else Icons.Default.Mic,
              contentDescription = null,
              tint = if (isActive) ElectricSky else TextSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = greeting.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
              )
              if (isActive) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(EmeraldRecord.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldRecord)
                }
              }
            }
            Text(
              text = if (greeting.type == "AI_VOICE") "AI Voice Synthesis • ~${greeting.durationSeconds}s" else "Recorded Voice • ~${greeting.durationSeconds}s",
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onPlay, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Default.VolumeUp, contentDescription = "Play", tint = ElectricSky, modifier = Modifier.size(18.dp))
          }
          if (!greeting.isPreset) {
            IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
              Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RoseAlarm, modifier = Modifier.size(18.dp))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "\"${greeting.scriptText}\"",
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 16.sp
      )

      if (!isActive) {
        Spacer(modifier = Modifier.height(10.dp))
        Button(
          onClick = onSetActive,
          colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceVariant, contentColor = ElectricSky),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth().height(36.dp)
        ) {
          Text("Set as Active Greeting", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}
