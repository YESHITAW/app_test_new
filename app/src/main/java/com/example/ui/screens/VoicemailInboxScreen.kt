package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Voicemail
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
import com.example.viewmodel.InboxFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VoicemailInboxScreen(
  viewModel: AnsweringMachineViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val voicemails by viewModel.filteredVoicemails.collectAsState()
  val allVoicemails by viewModel.allVoicemails.collectAsState()
  val activeGreeting by viewModel.activeGreeting.collectAsState()
  val currentFilter by viewModel.inboxFilter.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val playbackState by viewModel.playbackState.collectAsState()

  var voicemailToDelete by remember { mutableStateOf<Voicemail?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(NavyDarkBackground)
  ) {
    // Top Answering Machine Status Widget
    Surface(
      color = NavySurface,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      shape = RoundedCornerShape(16.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            TapeCassetteReel(
              isSpinning = playbackState.isPlaying,
              modifier = Modifier.padding(end = 12.dp)
            )
            Column {
              Text(
                text = "VOICE MESSAGING HUB",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AmberTape,
                letterSpacing = 1.sp
              )
              Text(
                text = "${allVoicemails.count { !it.isRead }} New • ${allVoicemails.size} Total Recordings",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
              )
            }
          }

          Button(
            onClick = { viewModel.setTab(AppTab.SIMULATOR) },
            colors = ButtonDefaults.buttonColors(
              containerColor = ElectricSky,
              contentColor = NavyDarkBackground
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("test_call_simulator_btn")
          ) {
            Icon(
              imageVector = Icons.Default.PhoneInTalk,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Test Call", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { viewModel.setSearchQuery(it) },
      placeholder = { Text("Search caller, transcript, or summary...", color = TextMuted, fontSize = 13.sp) },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { viewModel.setSearchQuery("") }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
          }
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
        .testTag("voicemail_search_field"),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ElectricSky,
        unfocusedBorderColor = NavyBorder,
        focusedContainerColor = NavySurface,
        unfocusedContainerColor = NavySurface,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      ),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    // Filter Chips Row
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(InboxFilter.values()) { filter ->
        val count = when (filter) {
          InboxFilter.ALL -> allVoicemails.size
          InboxFilter.URGENT -> allVoicemails.count { it.urgency == "HIGH" }
          InboxFilter.UNREAD -> allVoicemails.count { !it.isRead }
          InboxFilter.WORK -> allVoicemails.count { it.category == "WORK" }
          InboxFilter.PERSONAL -> allVoicemails.count { it.category == "PERSONAL" }
          InboxFilter.SPAM -> allVoicemails.count { it.category == "SPAM" }
        }

        FilterChip(
          selected = currentFilter == filter,
          onClick = { viewModel.setInboxFilter(filter) },
          label = {
            Text(
              text = "${filter.name.lowercase().replaceFirstChar { it.uppercase() }} ($count)",
              fontSize = 12.sp,
              fontWeight = if (currentFilter == filter) FontWeight.Bold else FontWeight.Normal
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = ElectricSky.copy(alpha = 0.2f),
            selectedLabelColor = ElectricSky,
            containerColor = NavySurface,
            labelColor = TextSecondary
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = currentFilter == filter,
            borderColor = NavyBorder,
            selectedBorderColor = ElectricSky
          )
        )
      }
    }

    // Voicemails List or Empty State
    if (voicemails.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(NavySurfaceVariant),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(32.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "No voicemails match this filter",
            style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Simulate an incoming call to record and transcribe caller messages automatically.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(20.dp))
          Button(
            onClick = { viewModel.setTab(AppTab.SIMULATOR) },
            colors = ButtonDefaults.buttonColors(containerColor = AmberTape, contentColor = NavyDarkBackground),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.PhoneInTalk, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simulate Incoming Call", fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(voicemails, key = { it.id }) { voicemail ->
          val isThisPlaying = playbackState.voicemailId == voicemail.id && playbackState.isPlaying
          val isThisSelected = playbackState.voicemailId == voicemail.id

          VoicemailCard(
            voicemail = voicemail,
            isPlaying = isThisPlaying,
            isSelected = isThisSelected,
            playbackCurrentSec = if (isThisSelected) playbackState.currentSeconds else 0,
            playbackTotalSec = if (isThisSelected) playbackState.totalSeconds else voicemail.durationSeconds,
            playbackProgress = if (isThisSelected) playbackState.progress else 0f,
            playbackSpeed = playbackState.speed,
            onTogglePlay = { viewModel.togglePlayVoicemail(voicemail) },
            onSeek = { progress -> viewModel.seekPlayback(progress) },
            onCycleSpeed = { viewModel.cyclePlaybackSpeed() },
            onToggleStar = { viewModel.toggleStarVoicemail(voicemail.id) },
            onToggleRead = { viewModel.markVoicemailRead(voicemail.id, !voicemail.isRead) },
            onDelete = { voicemailToDelete = voicemail },
            onCallback = {
              try {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                  data = Uri.parse("tel:${voicemail.callerNumber}")
                }
                context.startActivity(intent)
              } catch (_: Exception) {}
            },
            onQuickSms = {
              try {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                  data = Uri.parse("smsto:${voicemail.callerNumber}")
                  putExtra("sms_body", "Hi, I received your voice message regarding: ${voicemail.aiSummary}")
                }
                context.startActivity(intent)
              } catch (_: Exception) {}
            },
            onShare = {
              val shareText = "Voicemail from ${voicemail.callerName} (${voicemail.callerNumber})\n" +
                "Summary: ${voicemail.aiSummary}\n\nTranscript:\n${voicemail.transcript}"
              val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Voicemail from ${voicemail.callerName}")
                putExtra(Intent.EXTRA_TEXT, shareText)
              }
              context.startActivity(Intent.createChooser(intent, "Share Voicemail"))
            }
          )
        }
        item {
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }

  // Delete Confirmation Dialog
  val itemToDelete = voicemailToDelete
  if (itemToDelete != null) {
    AlertDialog(
      onDismissRequest = { voicemailToDelete = null },
      containerColor = NavySurface,
      titleContentColor = TextPrimary,
      textContentColor = TextSecondary,
      title = { Text("Delete Voicemail?") },
      text = { Text("Are you sure you want to permanently delete the recording and transcript from ${itemToDelete.callerName}?") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteVoicemail(itemToDelete)
            voicemailToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoseAlarm)
        ) {
          Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { voicemailToDelete = null }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}

@Composable
fun VoicemailCard(
  voicemail: Voicemail,
  isPlaying: Boolean,
  isSelected: Boolean,
  playbackCurrentSec: Int,
  playbackTotalSec: Int,
  playbackProgress: Float,
  playbackSpeed: Float,
  onTogglePlay: () -> Unit,
  onSeek: (Float) -> Unit,
  onCycleSpeed: () -> Unit,
  onToggleStar: () -> Unit,
  onToggleRead: () -> Unit,
  onDelete: () -> Unit,
  onCallback: () -> Unit,
  onQuickSms: () -> Unit,
  onShare: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }
  val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }
  val formattedDate = remember(voicemail.timestampMillis) {
    dateFormat.format(Date(voicemail.timestampMillis))
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = NavySurfaceCard),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isPlaying) 1.5.dp else 1.dp,
      color = if (isPlaying) ElectricSky else if (!voicemail.isRead) ElectricSky.copy(alpha = 0.5f) else NavyBorder
    ),
    modifier = modifier
      .fillMaxWidth()
      .animateContentSize()
      .testTag("voicemail_card_${voicemail.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Caller Avatar, Name, Number, Badges, Star
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Avatar with unread indicator
        Box {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(Color(voicemail.callerAvatarColorHex).copy(alpha = 0.2f))
              .border(1.5.dp, Color(voicemail.callerAvatarColorHex), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            val initials = voicemail.callerName.split(" ")
              .take(2)
              .mapNotNull { it.firstOrNull()?.toString() }
              .joinToString("")
            Text(
              text = initials.ifEmpty { "C" },
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = Color(voicemail.callerAvatarColorHex)
            )
          }
          if (!voicemail.isRead) {
            Box(
              modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(ElectricSky)
                .border(2.dp, NavySurfaceCard, CircleShape)
                .align(Alignment.TopEnd)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = voicemail.callerName,
              fontWeight = if (!voicemail.isRead) FontWeight.Bold else FontWeight.SemiBold,
              fontSize = 15.sp,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            UrgencyBadge(urgency = voicemail.urgency)
          }
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = voicemail.callerNumber,
              fontSize = 12.sp,
              color = TextSecondary
            )
            Text(text = " • ", color = TextMuted, fontSize = 12.sp)
            Text(
              text = formattedDate,
              fontSize = 11.sp,
              color = TextMuted
            )
          }
        }

        IconButton(
          onClick = onToggleStar,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = if (voicemail.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
            contentDescription = "Star",
            tint = if (voicemail.isStarred) AmberTape else TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Audio Player Controller
      Surface(
        color = NavySurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Play / Pause Circle Button
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isPlaying) AmberTape else ElectricSky)
                .clickable { onTogglePlay() },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = NavyDarkBackground,
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Waveform & scrubbing
            Column(modifier = Modifier.weight(1f)) {
              SoundWaveVisualizer(
                isAnimating = isPlaying,
                barCount = 18,
                heightDp = 22,
                primaryColor = if (isPlaying) ElectricSky else TextMuted
              )

              Slider(
                value = if (isSelected) playbackProgress else 0f,
                onValueChange = onSeek,
                colors = SliderDefaults.colors(
                  thumbColor = ElectricSky,
                  activeTrackColor = ElectricSky,
                  inactiveTrackColor = NavyBorder
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(18.dp)
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                val currentMins = playbackCurrentSec / 60
                val currentSecs = playbackCurrentSec % 60
                val totalMins = playbackTotalSec / 60
                val totalSecs = playbackTotalSec % 60
                Text(
                  text = String.format("%02d:%02d", currentMins, currentSecs),
                  fontSize = 11.sp,
                  color = if (isPlaying) ElectricSky else TextMuted
                )
                Text(
                  text = String.format("%02d:%02d", totalMins, totalSecs),
                  fontSize = 11.sp,
                  color = TextMuted
                )
              }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Speed multiplier pill
            Surface(
              color = NavySurface,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.clickable { onCycleSpeed() }
            ) {
              Text(
                text = "${playbackSpeed}x",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricSky,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // AI Summary Box
      Surface(
        color = NavySurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = AmberTape,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "AI VOICEMAIL INSIGHT",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = AmberTape,
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            CategoryBadge(category = voicemail.category)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = voicemail.aiSummary,
            fontSize = 13.sp,
            color = TextPrimary,
            lineHeight = 18.sp
          )

          // Key Action Items
          if (voicemail.aiKeyPoints.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            val items = voicemail.aiKeyPoints.split("|").filter { it.isNotBlank() }
            items.forEach { point ->
              Row(
                modifier = Modifier.padding(vertical = 1.dp),
                verticalAlignment = Alignment.Top
              ) {
                Text("• ", color = ElectricSky, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(point.trim(), color = TextSecondary, fontSize = 12.sp)
              }
            }
          }
        }
      }

      // Transcript accordion toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded }
          .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = if (isExpanded) "Hide Full Audio Transcript" else "View Full Audio Transcript",
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = ElectricSky
        )
        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = ElectricSky,
          modifier = Modifier.size(18.dp)
        )
      }

      AnimatedVisibility(visible = isExpanded) {
        Surface(
          color = NavySurfaceVariant,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
        ) {
          Text(
            text = voicemail.transcript,
            fontSize = 12.sp,
            color = TextPrimary,
            lineHeight = 17.sp,
            modifier = Modifier.padding(10.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Action toolbar: Call Back, SMS, Share, Read toggle, Delete
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = onCallback,
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldRecord, contentColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Call Back", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = onQuickSms,
            colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceVariant, contentColor = TextPrimary),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(14.dp), tint = ElectricSky)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Reply SMS", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
          IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Share, contentDescription = "Share", tint = TextSecondary, modifier = Modifier.size(16.dp))
          }
          IconButton(onClick = onToggleRead, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = if (voicemail.isRead) Icons.Default.MarkEmailUnread else Icons.Default.MarkEmailRead,
              contentDescription = "Toggle Read Status",
              tint = TextSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
          IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RoseAlarm, modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}
