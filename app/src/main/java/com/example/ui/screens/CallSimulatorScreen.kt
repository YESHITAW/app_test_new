package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.HearingDisabled
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneCallback
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CallerScenario
import com.example.ui.components.CategoryBadge
import com.example.ui.components.SoundWaveVisualizer
import com.example.ui.components.TapeCassetteReel
import com.example.ui.components.UrgencyBadge
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
import com.example.viewmodel.AppTab
import com.example.viewmodel.SimulatorStatus

@Composable
fun CallSimulatorScreen(
  viewModel: AnsweringMachineViewModel,
  modifier: Modifier = Modifier
) {
  val simulatorState by viewModel.simulatorState.collectAsState()
  val activeGreeting by viewModel.activeGreeting.collectAsState()
  val appSettings by viewModel.appSettings.collectAsState()
  val scenarios = viewModel.availableScenarios

  var selectedScenario by remember { mutableStateOf(scenarios.first()) }
  var showCustomCallerForm by remember { mutableStateOf(false) }
  var customName by remember { mutableStateOf("David Miller") }
  var customPhone by remember { mutableStateOf("+1 (555) 777-3321") }
  var customMessage by remember {
    mutableStateOf("Hi, this is David regarding our contract review tomorrow. Please call me back before noon.")
  }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NavyDarkBackground)
      .verticalScroll(scrollState)
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header section
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
            .background(AmberTape.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = AmberTape)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "Answering Machine Call Simulator",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
          )
          Text(
            text = "Simulates incoming calls, greeting delivery, beep signal, caller speech recording, and real-time screening.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
            fontSize = 12.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Interactive Call HUD Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
      border = androidx.compose.foundation.BorderStroke(
        width = 2.dp,
        color = when (simulatorState.status) {
          SimulatorStatus.RINGING -> AmberTape
          SimulatorStatus.GREETING_PLAYING -> ElectricSky
          SimulatorStatus.BEEP -> AmberTape
          SimulatorStatus.RECORDING -> RoseAlarm
          SimulatorStatus.AI_ANALYZING -> ElectricSky
          SimulatorStatus.COMPLETED -> EmeraldRecord
          SimulatorStatus.IDLE -> NavyBorder
        }
      ),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("simulator_hud_card")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Status Badge Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
              when (simulatorState.status) {
                SimulatorStatus.RINGING -> AmberTape.copy(alpha = 0.2f)
                SimulatorStatus.GREETING_PLAYING -> ElectricSky.copy(alpha = 0.2f)
                SimulatorStatus.BEEP -> AmberTape.copy(alpha = 0.2f)
                SimulatorStatus.RECORDING -> RoseAlarm.copy(alpha = 0.2f)
                SimulatorStatus.AI_ANALYZING -> ElectricSky.copy(alpha = 0.2f)
                SimulatorStatus.COMPLETED -> EmeraldRecord.copy(alpha = 0.2f)
                SimulatorStatus.IDLE -> NavySurfaceVariant
              }
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            when (simulatorState.status) {
              SimulatorStatus.RINGING -> {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AmberTape, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  "INCOMING CALL • RING ${simulatorState.ringsCount} OF ${appSettings.ringsBeforePickup}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = AmberTape
                )
              }
              SimulatorStatus.GREETING_PLAYING -> {
                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = ElectricSky, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  "PLAYING GREETING TO CALLER",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = ElectricSky
                )
              }
              SimulatorStatus.BEEP -> {
                Text(
                  "BEEP TONE (1000Hz) SENT",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = AmberTape
                )
              }
              SimulatorStatus.RECORDING -> {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(RoseAlarm)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  "RECORDING CALLER VOICEMAIL (${simulatorState.recordingSeconds}s)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = RoseAlarm
                )
              }
              SimulatorStatus.AI_ANALYZING -> {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricSky, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("AI NLP ANALYSIS & SUMMARY", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ElectricSky)
              }
              SimulatorStatus.COMPLETED -> {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldRecord, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("VOICEMAIL SAVED TO INBOX", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EmeraldRecord)
              }
              SimulatorStatus.IDLE -> {
                Text("ANSWERING MACHINE STANDBY", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = TextSecondary)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Caller Avatar with Ring Pulse Animation
        val ringPulse = remember { Animatable(1.0f) }
        LaunchedEffect(simulatorState.status) {
          if (simulatorState.status == SimulatorStatus.RINGING) {
            ringPulse.animateTo(
              targetValue = 1.15f,
              animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
              )
            )
          } else {
            ringPulse.snapTo(1.0f)
          }
        }

        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.size(100.dp)
        ) {
          if (simulatorState.status == SimulatorStatus.RINGING) {
            Box(
              modifier = Modifier
                .size(96.dp)
                .scale(ringPulse.value)
                .clip(CircleShape)
                .background(AmberTape.copy(alpha = 0.15f))
            )
          }
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .background(
                Color(if (simulatorState.status != SimulatorStatus.IDLE) simulatorState.avatarColorHex else selectedScenario.avatarColorHex).copy(alpha = 0.25f)
              )
              .border(
                2.dp,
                Color(if (simulatorState.status != SimulatorStatus.IDLE) simulatorState.avatarColorHex else selectedScenario.avatarColorHex),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            val name = if (simulatorState.status != SimulatorStatus.IDLE) simulatorState.callerName else selectedScenario.callerName
            val initials = name.split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString("")
            Text(
              text = initials.ifEmpty { "C" },
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              color = Color(if (simulatorState.status != SimulatorStatus.IDLE) simulatorState.avatarColorHex else selectedScenario.avatarColorHex)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Caller Info
        Text(
          text = if (simulatorState.status != SimulatorStatus.IDLE) simulatorState.callerName else selectedScenario.callerName,
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        )
        Text(
          text = if (simulatorState.status != SimulatorStatus.IDLE) simulatorState.callerNumber else selectedScenario.callerNumber,
          style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // HUD Middle Content depending on Phase
        when (simulatorState.status) {
          SimulatorStatus.IDLE -> {
            Surface(
              color = NavySurface,
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Scenario: ${selectedScenario.callerCategory}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = ElectricSky
                  )
                  UrgencyBadge(urgency = selectedScenario.urgency)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "\"${selectedScenario.initialSpokenMessage}\"",
                  fontSize = 12.sp,
                  color = TextSecondary,
                  lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  TapeCassetteReel(isSpinning = false, modifier = Modifier.size(20.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Active Greeting: ${activeGreeting?.title ?: "Default"}",
                    fontSize = 11.sp,
                    color = AmberTape
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = { viewModel.startCallSimulation(selectedScenario) },
              colors = ButtonDefaults.buttonColors(
                containerColor = EmeraldRecord,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("start_simulation_button")
            ) {
              Icon(Icons.Default.PhoneCallback, contentDescription = null, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text("Launch Incoming Call Simulation", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
          }

          SimulatorStatus.RINGING -> {
            Text(
              text = "Answering machine will automatically pick up after ${appSettings.ringsBeforePickup} rings...",
              fontSize = 13.sp,
              color = TextSecondary,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = { viewModel.answerImmediately() },
              colors = ButtonDefaults.buttonColors(containerColor = ElectricSky, contentColor = NavyDarkBackground),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("answer_now_button")
            ) {
              Text("Answer Immediately", fontWeight = FontWeight.Bold)
            }
          }

          SimulatorStatus.GREETING_PLAYING -> {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              TapeCassetteReel(isSpinning = true)
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Broadcasting Voice Greeting...",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = ElectricSky
                )
                Text(
                  text = "\"${activeGreeting?.scriptText?.take(60) ?: "Hello, please leave a message..."}...\"",
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }
            Spacer(modifier = Modifier.height(14.dp))
            SoundWaveVisualizer(isAnimating = true, barCount = 20, heightDp = 28, primaryColor = ElectricSky)
          }

          SimulatorStatus.BEEP -> {
            Text(
              text = "BEEP! Playing tone signal to prompt caller...",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = AmberTape
            )
          }

          SimulatorStatus.RECORDING -> {
            Surface(
              color = NavySurface,
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, RoseAlarm.copy(alpha = 0.5f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = RoseAlarm, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LIVE CALL SCREENING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoseAlarm)
                  }
                  Text(
                    text = "${simulatorState.currentDecibelLevel.toInt()} dB",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberTape
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))
                SoundWaveVisualizer(isAnimating = true, barCount = 22, heightDp = 26, primaryColor = RoseAlarm)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = if (simulatorState.liveTranscript.isNotBlank())
                    "\"${simulatorState.liveTranscript}\""
                  else
                    "Listening to caller... Speak into microphone or let caller scenario proceed.",
                  fontSize = 13.sp,
                  color = TextPrimary,
                  lineHeight = 18.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Screening Controls: Listen In, Break In (Pick Up), End Call
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              OutlinedButton(
                onClick = { viewModel.toggleScreeningAudio() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricSky),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(
                  imageVector = if (simulatorState.isScreeningActive) Icons.Default.Hearing else Icons.Default.HearingDisabled,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (simulatorState.isScreeningActive) "Screening ON" else "Muted", fontSize = 11.sp)
              }

              Button(
                onClick = { viewModel.endCallSimulation() },
                colors = ButtonDefaults.buttonColors(containerColor = RoseAlarm, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("end_call_button")
              ) {
                Icon(Icons.Default.CallEnd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Finish Recording", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }

          SimulatorStatus.AI_ANALYZING -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CircularProgressIndicator(color = ElectricSky, modifier = Modifier.size(36.dp))
              Spacer(modifier = Modifier.height(12.dp))
              Text("AI Processing Voicemail...", fontWeight = FontWeight.Bold, color = ElectricSky)
              Text(
                "Transcribing audio stream • Generating structured action points • Estimating urgency",
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }

          SimulatorStatus.COMPLETED -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "Voicemail successfully transcribed & saved!",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = EmeraldRecord
              )
              Spacer(modifier = Modifier.height(16.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                  onClick = { viewModel.setTab(AppTab.INBOX) },
                  colors = ButtonDefaults.buttonColors(containerColor = ElectricSky, contentColor = NavyDarkBackground),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.testTag("view_in_inbox_button")
                ) {
                  Text("Listen in Inbox", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                  onClick = { viewModel.endCallSimulation() },
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Text("Reset Simulator")
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Scenario Picker Section (Visible when IDLE)
    if (simulatorState.status == SimulatorStatus.IDLE) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Select Caller Scenario to Test:",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(scenarios) { sc ->
            val isSelected = selectedScenario.id == sc.id
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isSelected) NavySurfaceVariant else NavySurface
              ),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) ElectricSky else NavyBorder
              ),
              modifier = Modifier
                .width(180.dp)
                .clickable { selectedScenario = sc }
                .testTag("scenario_chip_${sc.id}")
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  UrgencyBadge(urgency = sc.urgency)
                  CategoryBadge(category = sc.callerCategory)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = sc.callerName,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = TextPrimary,
                  maxLines = 1
                )
                Text(
                  text = sc.callerNumber,
                  fontSize = 11.sp,
                  color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = sc.initialSpokenMessage,
                  fontSize = 11.sp,
                  color = TextMuted,
                  maxLines = 2
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle Custom Caller Simulation
        Surface(
          color = NavySurface,
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { showCustomCallerForm = !showCustomCallerForm },
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Or Create Custom Caller Script",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = ElectricSky
              )
              Text(
                text = if (showCustomCallerForm) "Hide" else "Customize",
                fontSize = 12.sp,
                color = AmberTape
              )
            }

            AnimatedVisibility(visible = showCustomCallerForm) {
              Column(modifier = Modifier.padding(top = 12.dp)) {
                OutlinedTextField(
                  value = customName,
                  onValueChange = { customName = it },
                  label = { Text("Caller Name", color = TextSecondary) },
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricSky,
                    unfocusedBorderColor = NavyBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                  ),
                  modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                  value = customPhone,
                  onValueChange = { customPhone = it },
                  label = { Text("Caller Phone", color = TextSecondary) },
                  singleLine = true,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricSky,
                    unfocusedBorderColor = NavyBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                  ),
                  modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                  value = customMessage,
                  onValueChange = { customMessage = it },
                  label = { Text("Voicemail Message Script", color = TextSecondary) },
                  maxLines = 3,
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricSky,
                    unfocusedBorderColor = NavyBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                  ),
                  modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                  onClick = {
                    val customScenario = CallerScenario(
                      id = "custom_${System.currentTimeMillis()}",
                      callerName = customName,
                      callerNumber = customPhone,
                      callerCategory = "WORK",
                      initialSpokenMessage = customMessage,
                      expectedTranscript = customMessage,
                      expectedSummary = "Custom caller message left by $customName.",
                      expectedKeyPoints = listOf("Custom simulation message", "Review voice details"),
                      urgency = "MEDIUM",
                      avatarColorHex = 0xFFF59E0B
                    )
                    selectedScenario = customScenario
                    viewModel.startCallSimulation(customScenario)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = AmberTape, contentColor = NavyDarkBackground),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text("Simulate Custom Caller", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
  }
}
