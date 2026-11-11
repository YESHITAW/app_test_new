package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
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
import com.example.ui.theme.UrgentHigh
import com.example.ui.theme.UrgentLow
import com.example.ui.theme.UrgentMedium
import com.example.viewmodel.AppTab

@Composable
fun AppHeader(
  currentTab: AppTab,
  onOpenDocs: () -> Unit,
  onOpenSimulator: () -> Unit,
  activeGreetingTitle: String,
  modifier: Modifier = Modifier
) {
  Surface(
    color = NavySurface,
    modifier = modifier.fillMaxWidth(),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
              Brush.linearGradient(
                listOf(ElectricSky, AmberTape)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.GraphicEq,
            contentDescription = "Logo",
            tint = NavyDarkBackground,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Answering Machine",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(EmeraldRecord.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "ACTIVE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldRecord
              )
            }
          }
          Text(
            text = "Greeting: $activeGreetingTitle",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
            maxLines = 1
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        if (currentTab != AppTab.SIMULATOR) {
          IconButton(
            onClick = onOpenSimulator,
            modifier = Modifier.testTag("header_simulator_button")
          ) {
            Icon(
              imageVector = Icons.Default.PhoneInTalk,
              contentDescription = "Test Call Simulator",
              tint = ElectricSky
            )
          }
        }
        IconButton(
          onClick = onOpenDocs,
          modifier = Modifier.testTag("header_docs_button")
        ) {
          Icon(
            imageVector = Icons.Default.Description,
            contentDescription = "Specs, Guide & Strategy",
            tint = AmberTape
          )
        }
      }
    }
  }
}

@Composable
fun AppBottomNav(
  currentTab: AppTab,
  onTabSelected: (AppTab) -> Unit,
  unheardVoicemailsCount: Int,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    containerColor = NavySurface,
    contentColor = TextPrimary,
    tonalElevation = 8.dp,
    modifier = modifier.border(androidx.compose.foundation.BorderStroke(1.dp, NavyBorder))
  ) {
    NavigationBarItem(
      selected = currentTab == AppTab.INBOX,
      onClick = { onTabSelected(AppTab.INBOX) },
      icon = {
        if (unheardVoicemailsCount > 0) {
          BadgedBox(
            badge = {
              Badge(containerColor = ElectricSky, contentColor = NavyDarkBackground) {
                Text(unheardVoicemailsCount.toString(), fontWeight = FontWeight.Bold)
              }
            }
          ) {
            Icon(Icons.Default.Inbox, contentDescription = "Voicemail Inbox")
          }
        } else {
          Icon(Icons.Default.Inbox, contentDescription = "Voicemail Inbox")
        }
      },
      label = { Text("Inbox") },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = ElectricSky,
        selectedTextColor = ElectricSky,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted,
        indicatorColor = ElectricSky.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_inbox")
    )

    NavigationBarItem(
      selected = currentTab == AppTab.SIMULATOR,
      onClick = { onTabSelected(AppTab.SIMULATOR) },
      icon = { Icon(Icons.Default.PhoneInTalk, contentDescription = "Call Simulator") },
      label = { Text("Simulator") },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = AmberTape,
        selectedTextColor = AmberTape,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted,
        indicatorColor = AmberTape.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_simulator")
    )

    NavigationBarItem(
      selected = currentTab == AppTab.GREETINGS,
      onClick = { onTabSelected(AppTab.GREETINGS) },
      icon = { Icon(Icons.Default.Mic, contentDescription = "Greetings Studio") },
      label = { Text("Greetings") },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = ElectricSky,
        selectedTextColor = ElectricSky,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted,
        indicatorColor = ElectricSky.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_greetings")
    )

    NavigationBarItem(
      selected = currentTab == AppTab.SETTINGS,
      onClick = { onTabSelected(AppTab.SETTINGS) },
      icon = { Icon(Icons.Default.Settings, contentDescription = "Settings & Privacy") },
      label = { Text("Settings") },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = ElectricSky,
        selectedTextColor = ElectricSky,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted,
        indicatorColor = ElectricSky.copy(alpha = 0.15f)
      ),
      modifier = Modifier.testTag("nav_settings")
    )
  }
}

@Composable
fun TapeCassetteReel(
  isSpinning: Boolean,
  modifier: Modifier = Modifier
) {
  val rotation = remember { Animatable(0f) }

  LaunchedEffect(isSpinning) {
    if (isSpinning) {
      rotation.animateTo(
        targetValue = 360f * 100f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = 2000, easing = LinearEasing),
          repeatMode = RepeatMode.Restart
        )
      )
    } else {
      rotation.stop()
    }
  }

  Box(
    modifier = modifier
      .size(36.dp)
      .clip(CircleShape)
      .background(NavySurfaceVariant)
      .border(2.dp, AmberTape, CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Canvas(
      modifier = Modifier
        .size(24.dp)
        .rotate(if (isSpinning) rotation.value % 360f else 0f)
    ) {
      val center = Offset(size.width / 2, size.height / 2)
      val radius = size.minDimension / 2
      drawCircle(
        color = AmberTape,
        radius = radius * 0.4f,
        center = center
      )
      // Teeth spokes
      for (i in 0 until 6) {
        val angle = Math.toRadians((i * 60).toDouble())
        val x = center.x + (radius * 0.8f * Math.cos(angle)).toFloat()
        val y = center.y + (radius * 0.8f * Math.sin(angle)).toFloat()
        drawLine(
          color = AmberTape,
          start = center,
          end = Offset(x, y),
          strokeWidth = 3f
        )
      }
    }
  }
}

@Composable
fun SoundWaveVisualizer(
  isAnimating: Boolean,
  barCount: Int = 16,
  heightDp: Int = 32,
  primaryColor: Color = ElectricSky,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .height(heightDp.dp)
      .padding(horizontal = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(3.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    for (i in 0 until barCount) {
      val anim = remember { Animatable(0.2f) }
      LaunchedEffect(isAnimating) {
        if (isAnimating) {
          val delayOffset = (i * 70) % 400
          val targetHeights = listOf(0.3f, 0.9f, 0.5f, 1.0f, 0.4f, 0.7f)
          val targetHeight = targetHeights[i % targetHeights.size]
          anim.animateTo(
            targetValue = targetHeight,
            animationSpec = infiniteRepeatable(
              animation = tween(durationMillis = 400 + delayOffset, easing = LinearEasing),
              repeatMode = RepeatMode.Reverse
            )
          )
        } else {
          anim.snapTo(0.15f)
        }
      }

      Box(
        modifier = Modifier
          .weight(1f)
          .height((heightDp * anim.value).coerceAtLeast(3f).dp)
          .clip(RoundedCornerShape(2.dp))
          .background(primaryColor)
      )
    }
  }
}

@Composable
fun UrgencyBadge(urgency: String, modifier: Modifier = Modifier) {
  val (color, label) = when (urgency.uppercase()) {
    "HIGH" -> UrgentHigh to "HIGH URGENCY"
    "MEDIUM" -> UrgentMedium to "MEDIUM"
    else -> UrgentLow to "NORMAL"
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(4.dp))
      .background(color.copy(alpha = 0.15f))
      .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      color = color
    )
  }
}

@Composable
fun CategoryBadge(category: String, modifier: Modifier = Modifier) {
  val color = when (category.uppercase()) {
    "WORK" -> ElectricSky
    "MEDICAL" -> RoseAlarm
    "PERSONAL" -> EmeraldRecord
    "SPAM" -> TextMuted
    else -> AmberTape
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(4.dp))
      .background(NavySurfaceVariant)
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Text(
      text = category.uppercase(),
      fontSize = 10.sp,
      fontWeight = FontWeight.SemiBold,
      color = color
    )
  }
}
