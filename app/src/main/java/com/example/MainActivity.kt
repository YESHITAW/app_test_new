package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppHeader
import com.example.ui.screens.CallSimulatorScreen
import com.example.ui.screens.DevSpecsAndGuideScreen
import com.example.ui.screens.GreetingsStudioScreen
import com.example.ui.screens.SettingsAndPrivacyScreen
import com.example.ui.screens.VoicemailInboxScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyDarkBackground
import com.example.viewmodel.AnsweringMachineViewModel
import com.example.viewmodel.AppTab

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAnsweringMachineApp()
      }
    }
  }
}

@Composable
fun MainAnsweringMachineApp(
  viewModel: AnsweringMachineViewModel = viewModel()
) {
  val currentTab by viewModel.currentTab.collectAsState()
  val allVoicemails by viewModel.allVoicemails.collectAsState()
  val activeGreeting by viewModel.activeGreeting.collectAsState()
  val unreadCount = allVoicemails.count { !it.isRead }

  // Handle system back navigation smoothly
  BackHandler(enabled = currentTab != AppTab.INBOX) {
    viewModel.setTab(AppTab.INBOX)
  }

  Scaffold(
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      AppHeader(
        currentTab = currentTab,
        onOpenDocs = { viewModel.setTab(AppTab.DOCS) },
        onOpenSimulator = { viewModel.setTab(AppTab.SIMULATOR) },
        activeGreetingTitle = activeGreeting?.title ?: "Standard Greeting"
      )
    },
    bottomBar = {
      if (currentTab != AppTab.DOCS) {
        AppBottomNav(
          currentTab = currentTab,
          onTabSelected = { tab -> viewModel.setTab(tab) },
          unheardVoicemailsCount = unreadCount
        )
      }
    },
    containerColor = NavyDarkBackground
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(NavyDarkBackground)
    ) {
      when (currentTab) {
        AppTab.INBOX -> VoicemailInboxScreen(viewModel = viewModel)
        AppTab.SIMULATOR -> CallSimulatorScreen(viewModel = viewModel)
        AppTab.GREETINGS -> GreetingsStudioScreen(viewModel = viewModel)
        AppTab.SETTINGS -> SettingsAndPrivacyScreen(viewModel = viewModel)
        AppTab.DOCS -> DevSpecsAndGuideScreen()
      }
    }
  }
}
