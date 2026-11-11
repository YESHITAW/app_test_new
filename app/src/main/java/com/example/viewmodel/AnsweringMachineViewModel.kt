package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioManagerHelper
import com.example.data.local.AppDatabase
import com.example.data.model.AppSettings
import com.example.data.model.CallerScenario
import com.example.data.model.Greeting
import com.example.data.model.Voicemail
import com.example.data.repository.AnsweringMachineRepository
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppTab {
  INBOX,
  SIMULATOR,
  GREETINGS,
  SETTINGS,
  DOCS
}

enum class SimulatorStatus {
  IDLE,
  RINGING,
  GREETING_PLAYING,
  BEEP,
  RECORDING,
  AI_ANALYZING,
  COMPLETED
}

enum class InboxFilter {
  ALL,
  URGENT,
  UNREAD,
  WORK,
  PERSONAL,
  SPAM
}

data class PlaybackState(
  val voicemailId: Long? = null,
  val isPlaying: Boolean = false,
  val currentSeconds: Int = 0,
  val totalSeconds: Int = 0,
  val progress: Float = 0f,
  val speed: Float = 1.0f
)

data class GreetingStudioState(
  val isRecording: Boolean = false,
  val recordingSeconds: Int = 0,
  val recordedFile: File? = null,
  val isPlayingPreview: Boolean = false,
  val ttsInputText: String = "Hi, you've reached Alex. I am currently out of reach. Please leave your details after the beep.",
  val ttsPitch: Float = 1.0f,
  val ttsSpeed: Float = 1.0f,
  val isTtsSpeaking: Boolean = false,
  val recentAmplitudes: List<Float> = emptyList()
)

data class CallSimulatorState(
  val status: SimulatorStatus = SimulatorStatus.IDLE,
  val callerName: String = "Dr. Emily Carter",
  val callerNumber: String = "+1 (555) 234-8901",
  val callerCategory: String = "MEDICAL",
  val avatarColorHex: Long = 0xFFEF4444,
  val ringsCount: Int = 0,
  val recordingSeconds: Int = 0,
  val liveTranscript: String = "",
  val isScreeningActive: Boolean = true,
  val currentDecibelLevel: Float = 0f,
  val lastProcessedVoicemailId: Long? = null,
  val isCustomCaller: Boolean = false
)

class AnsweringMachineViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application, viewModelScope)
  private val repository = AnsweringMachineRepository(database.voicemailDao(), database.greetingDao())
  val audioHelper = AudioManagerHelper(application)

  val allVoicemails = repository.voicemails.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val allGreetings = repository.greetings.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val activeGreeting = repository.activeGreeting.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    null
  )

  private val _currentTab = MutableStateFlow(AppTab.INBOX)
  val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

  private val _inboxFilter = MutableStateFlow(InboxFilter.ALL)
  val inboxFilter: StateFlow<InboxFilter> = _inboxFilter.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  val filteredVoicemails: StateFlow<List<Voicemail>> = combine(
    allVoicemails,
    _inboxFilter,
    _searchQuery
  ) { list, filter, query ->
    list.filter { item ->
      val matchesQuery = query.isEmpty() ||
        item.callerName.contains(query, ignoreCase = true) ||
        item.callerNumber.contains(query, ignoreCase = true) ||
        item.transcript.contains(query, ignoreCase = true) ||
        item.aiSummary.contains(query, ignoreCase = true)

      val matchesFilter = when (filter) {
        InboxFilter.ALL -> true
        InboxFilter.URGENT -> item.urgency == "HIGH"
        InboxFilter.UNREAD -> !item.isRead
        InboxFilter.WORK -> item.category == "WORK"
        InboxFilter.PERSONAL -> item.category == "PERSONAL"
        InboxFilter.SPAM -> item.category == "SPAM"
      }

      matchesQuery && matchesFilter
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _playbackState = MutableStateFlow(PlaybackState())
  val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  private val _simulatorState = MutableStateFlow(CallSimulatorState())
  val simulatorState: StateFlow<CallSimulatorState> = _simulatorState.asStateFlow()

  private val _studioState = MutableStateFlow(GreetingStudioState())
  val studioState: StateFlow<GreetingStudioState> = _studioState.asStateFlow()

  private val _appSettings = MutableStateFlow(AppSettings())
  val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

  val availableScenarios = repository.getCallerScenarios()

  private var playbackJob: Job? = null
  private var simulatorJob: Job? = null
  private var greetingRecorderJob: Job? = null

  fun setTab(tab: AppTab) {
    _currentTab.value = tab
  }

  fun setInboxFilter(filter: InboxFilter) {
    _inboxFilter.value = filter
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  // --- AUDIO PLAYBACK ---
  fun togglePlayVoicemail(voicemail: Voicemail) {
    val current = _playbackState.value
    if (current.voicemailId == voicemail.id && current.isPlaying) {
      pausePlayback()
    } else if (current.voicemailId == voicemail.id && !current.isPlaying) {
      resumePlayback()
    } else {
      startPlayingVoicemail(voicemail)
    }
  }

  private fun startPlayingVoicemail(voicemail: Voicemail) {
    audioHelper.stopPlayback()
    audioHelper.stopSpeaking()
    playbackJob?.cancel()

    // Mark as read automatically when played
    viewModelScope.launch(Dispatchers.IO) {
      repository.setReadStatus(voicemail.id, true)
    }

    val totalSec = if (voicemail.durationSeconds > 0) voicemail.durationSeconds else 30
    _playbackState.value = PlaybackState(
      voicemailId = voicemail.id,
      isPlaying = true,
      currentSeconds = 0,
      totalSeconds = totalSec,
      progress = 0f,
      speed = _playbackState.value.speed
    )

    val recordedFile = if (voicemail.audioFilePath.isNotBlank() && !voicemail.audioFilePath.startsWith("sample_")) {
      File(voicemail.audioFilePath)
    } else null

    if (recordedFile != null && recordedFile.exists()) {
      audioHelper.playAudioFile(
        file = recordedFile,
        speed = _playbackState.value.speed,
        onComplete = {
          _playbackState.value = _playbackState.value.copy(isPlaying = false, progress = 1f, currentSeconds = totalSec)
        }
      )
    } else {
      // Sample voicemail audio: play through speech synthesis preview or audio simulation
      audioHelper.speak(
        text = voicemail.transcript,
        pitch = 1.0f,
        speechRate = _playbackState.value.speed,
        onDone = {
          _playbackState.value = _playbackState.value.copy(isPlaying = false, progress = 1f, currentSeconds = totalSec)
        }
      )
    }

    // Progress counter job
    playbackJob = viewModelScope.launch {
      var currentSec = 0
      while (isActive && currentSec < totalSec && _playbackState.value.isPlaying) {
        delay((1000 / _playbackState.value.speed).toLong())
        currentSec++
        val progress = (currentSec.toFloat() / totalSec).coerceIn(0f, 1f)
        _playbackState.value = _playbackState.value.copy(
          currentSeconds = currentSec,
          progress = progress
        )
      }
      if (currentSec >= totalSec) {
        _playbackState.value = _playbackState.value.copy(isPlaying = false, progress = 0f, currentSeconds = 0)
      }
    }
  }

  fun pausePlayback() {
    audioHelper.pausePlayback()
    audioHelper.stopSpeaking()
    playbackJob?.cancel()
    _playbackState.value = _playbackState.value.copy(isPlaying = false)
  }

  fun resumePlayback() {
    _playbackState.value.voicemailId?.let { id ->
      allVoicemails.value.find { it.id == id }?.let { vm ->
        startPlayingVoicemail(vm)
      }
    }
  }

  fun seekPlayback(progress: Float) {
    val totalSec = _playbackState.value.totalSeconds
    val targetSec = (progress * totalSec).toInt()
    _playbackState.value = _playbackState.value.copy(
      progress = progress,
      currentSeconds = targetSec
    )
    audioHelper.seekTo(targetSec * 1000)
  }

  fun cyclePlaybackSpeed() {
    val speeds = listOf(1.0f, 1.25f, 1.5f, 2.0f)
    val currentIndex = speeds.indexOfFirst { kotlin.math.abs(it - _playbackState.value.speed) < 0.1f }
    val nextSpeed = speeds[(currentIndex + 1) % speeds.size]
    _playbackState.value = _playbackState.value.copy(speed = nextSpeed)
    audioHelper.setPlaybackSpeed(nextSpeed)
  }

  fun deleteVoicemail(voicemail: Voicemail) {
    viewModelScope.launch(Dispatchers.IO) {
      if (_playbackState.value.voicemailId == voicemail.id) {
        pausePlayback()
        _playbackState.value = PlaybackState()
      }
      repository.deleteVoicemail(voicemail)
      if (voicemail.audioFilePath.isNotBlank() && !voicemail.audioFilePath.startsWith("sample_")) {
        try {
          File(voicemail.audioFilePath).delete()
        } catch (_: Exception) {}
      }
    }
  }

  fun toggleStarVoicemail(id: Long) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.toggleStarred(id)
    }
  }

  fun markVoicemailRead(id: Long, isRead: Boolean) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.setReadStatus(id, isRead)
    }
  }

  fun clearAllVoicemails() {
    viewModelScope.launch(Dispatchers.IO) {
      pausePlayback()
      _playbackState.value = PlaybackState()
      repository.clearAllVoicemails()
    }
  }

  // --- CALL SIMULATOR & ANSWERING MACHINE ENGINE ---
  fun startCallSimulation(scenario: CallerScenario) {
    simulatorJob?.cancel()
    audioHelper.stopPlayback()
    audioHelper.stopSpeaking()

    _simulatorState.value = CallSimulatorState(
      status = SimulatorStatus.RINGING,
      callerName = scenario.callerName,
      callerNumber = scenario.callerNumber,
      callerCategory = scenario.callerCategory,
      avatarColorHex = scenario.avatarColorHex,
      ringsCount = 0,
      recordingSeconds = 0,
      liveTranscript = "",
      isScreeningActive = _appSettings.value.callScreeningEnabled,
      isCustomCaller = false
    )

    _currentTab.value = AppTab.SIMULATOR

    simulatorJob = viewModelScope.launch {
      // Phase 1: Ringing
      val ringsToPickup = _appSettings.value.ringsBeforePickup
      for (r in 1..ringsToPickup) {
        if (!isActive) return@launch
        _simulatorState.value = _simulatorState.value.copy(ringsCount = r)
        audioHelper.vibratePattern(longArrayOf(0, 700, 1000))
        delay(1800)
      }
      audioHelper.cancelVibration()

      // Phase 2: Pickup & Greeting Playback
      _simulatorState.value = _simulatorState.value.copy(status = SimulatorStatus.GREETING_PLAYING)
      val currentGreeting = activeGreeting.value
      val greetingText = currentGreeting?.scriptText ?: "Hello, please leave a message after the beep."
      
      var greetingDone = false
      audioHelper.speak(greetingText, onDone = {
        greetingDone = true
      })

      val maxWaitSec = (currentGreeting?.durationSeconds ?: 7) + 2
      var waitSec = 0
      while (isActive && !greetingDone && waitSec < maxWaitSec) {
        delay(1000)
        waitSec++
      }

      // Phase 3: Classic Answering Machine Beep Tone
      _simulatorState.value = _simulatorState.value.copy(status = SimulatorStatus.BEEP)
      audioHelper.playAnsweringMachineBeep(_appSettings.value.beepToneVolume)
      delay(500)

      // Phase 4: Recording Caller Voicemail
      _simulatorState.value = _simulatorState.value.copy(
        status = SimulatorStatus.RECORDING,
        recordingSeconds = 0
      )

      // Start actual mic recording if user wants to speak or caller scenario audio
      val recordingFile = audioHelper.startRecording("voicemail")

      // Stream caller message words into live screening transcript
      val words = scenario.initialSpokenMessage.split(" ")
      val totalRecordSec = minOf(_appSettings.value.maxRecordingSeconds, words.size / 2 + 3)
      var elapsed = 0
      val transcriptBuilder = StringBuilder()

      while (isActive && elapsed < totalRecordSec) {
        delay(1000)
        elapsed++
        
        // Add words progressively for live call screening visualization
        val wordIndex = (elapsed * 3).coerceAtMost(words.size)
        val currentWords = words.take(wordIndex).joinToString(" ")
        
        // Decibel pulse calculation
        val micAmp = audioHelper.getMaxAmplitude()
        val decibels = if (micAmp > 0) (20 * kotlin.math.log10(micAmp.toDouble())).toFloat() else (40f + (elapsed % 5) * 6f)

        _simulatorState.value = _simulatorState.value.copy(
          recordingSeconds = elapsed,
          liveTranscript = currentWords,
          currentDecibelLevel = decibels
        )
      }

      // Stop recording audio file
      val finalAudioFile = audioHelper.stopRecording()

      // Phase 5: AI Analyzing & Transcription
      _simulatorState.value = _simulatorState.value.copy(status = SimulatorStatus.AI_ANALYZING)
      delay(1200) // Realistic AI synthesis and NLP processing

      // Insert into Voicemail Inbox
      val savedVoicemail = Voicemail(
        callerName = scenario.callerName,
        callerNumber = scenario.callerNumber,
        timestampMillis = System.currentTimeMillis(),
        durationSeconds = elapsed,
        audioFilePath = finalAudioFile?.absolutePath ?: "sample_${scenario.id}",
        transcript = scenario.expectedTranscript,
        aiSummary = scenario.expectedSummary,
        aiKeyPoints = scenario.expectedKeyPoints.joinToString("|"),
        urgency = scenario.urgency,
        category = scenario.callerCategory,
        isRead = false,
        isStarred = false,
        callerAvatarColorHex = scenario.avatarColorHex
      )

      val newId = repository.insertVoicemail(savedVoicemail)

      _simulatorState.value = _simulatorState.value.copy(
        status = SimulatorStatus.COMPLETED,
        lastProcessedVoicemailId = newId
      )
    }
  }

  fun answerImmediately() {
    simulatorJob?.cancel()
    simulatorJob = viewModelScope.launch {
      audioHelper.cancelVibration()
      _simulatorState.value = _simulatorState.value.copy(status = SimulatorStatus.GREETING_PLAYING)
      val currentGreeting = activeGreeting.value
      val greetingText = currentGreeting?.scriptText ?: "Hello, please leave a message after the beep."
      
      var greetingDone = false
      audioHelper.speak(greetingText, onDone = { greetingDone = true })

      var waitSec = 0
      while (isActive && !greetingDone && waitSec < 7) {
        delay(1000)
        waitSec++
      }

      _simulatorState.value = _simulatorState.value.copy(status = SimulatorStatus.BEEP)
      audioHelper.playAnsweringMachineBeep(_appSettings.value.beepToneVolume)
      delay(500)

      _simulatorState.value = _simulatorState.value.copy(status = SimulatorStatus.RECORDING)
      val recordingFile = audioHelper.startRecording("voicemail")

      // User speaks into mic
      for (sec in 1.._appSettings.value.maxRecordingSeconds) {
        if (!isActive) break
        delay(1000)
        val amp = audioHelper.getMaxAmplitude()
        val db = if (amp > 0) (20 * kotlin.math.log10(amp.toDouble())).toFloat() else 42f
        _simulatorState.value = _simulatorState.value.copy(
          recordingSeconds = sec,
          currentDecibelLevel = db,
          liveTranscript = if (sec > 2) "Caller voice recording in progress (${sec}s)..." else "Listening for caller message..."
        )
      }
      finishRecordingSession(recordingFile)
    }
  }

  fun endCallSimulation() {
    simulatorJob?.cancel()
    audioHelper.cancelVibration()
    audioHelper.stopSpeaking()
    val recorded = audioHelper.stopRecording()
    
    if (_simulatorState.value.status == SimulatorStatus.RECORDING && _simulatorState.value.recordingSeconds > 2) {
      viewModelScope.launch {
        finishRecordingSession(recorded)
      }
    } else {
      _simulatorState.value = _simulatorState.value.copy(status = SimulatorStatus.IDLE)
    }
  }

  private suspend fun finishRecordingSession(audioFile: File?) {
    _simulatorState.value = _simulatorState.value.copy(status = SimulatorStatus.AI_ANALYZING)
    delay(1000)
    val state = _simulatorState.value
    val duration = maxOf(3, state.recordingSeconds)

    val transcript = if (state.liveTranscript.isNotBlank() && !state.liveTranscript.contains("...")) {
      state.liveTranscript
    } else {
      "Caller left a message regarding an urgent follow-up inquiry. Voice audio recorded and ready for playback."
    }

    val newVoicemail = Voicemail(
      callerName = state.callerName,
      callerNumber = state.callerNumber,
      timestampMillis = System.currentTimeMillis(),
      durationSeconds = duration,
      audioFilePath = audioFile?.absolutePath ?: "",
      transcript = transcript,
      aiSummary = "Voicemail from ${state.callerName} (${state.callerCategory}). Transcribed with AI sentiment analysis.",
      aiKeyPoints = "New incoming voice recording|Duration: ${duration}s|Callback required",
      urgency = if (state.callerCategory == "MEDICAL" || state.callerCategory == "URGENT") "HIGH" else "MEDIUM",
      category = state.callerCategory,
      isRead = false,
      isStarred = false,
      callerAvatarColorHex = state.avatarColorHex
    )
    val id = repository.insertVoicemail(newVoicemail)
    _simulatorState.value = _simulatorState.value.copy(
      status = SimulatorStatus.COMPLETED,
      lastProcessedVoicemailId = id
    )
  }

  fun toggleScreeningAudio() {
    val current = _simulatorState.value.isScreeningActive
    _simulatorState.value = _simulatorState.value.copy(isScreeningActive = !current)
  }

  // --- GREETING STUDIO ---
  fun startRecordingGreeting() {
    audioHelper.stopPlayback()
    audioHelper.stopSpeaking()
    greetingRecorderJob?.cancel()

    val file = audioHelper.startRecording("greeting")
    _studioState.value = _studioState.value.copy(
      isRecording = true,
      recordingSeconds = 0,
      recordedFile = file,
      recentAmplitudes = emptyList()
    )

    greetingRecorderJob = viewModelScope.launch {
      var sec = 0
      val ampList = mutableListOf<Float>()
      while (isActive && _studioState.value.isRecording) {
        delay(200)
        val amp = audioHelper.getMaxAmplitude()
        val normalized = (amp / 32767f).coerceIn(0.05f, 1.0f)
        ampList.add(normalized)
        if (ampList.size > 30) ampList.removeAt(0)

        if ((ampList.size % 5) == 0) {
          sec++
        }
        _studioState.value = _studioState.value.copy(
          recordingSeconds = sec,
          recentAmplitudes = ampList.toList()
        )
      }
    }
  }

  fun stopRecordingGreeting() {
    greetingRecorderJob?.cancel()
    val file = audioHelper.stopRecording()
    _studioState.value = _studioState.value.copy(
      isRecording = false,
      recordedFile = file
    )
  }

  fun playGreetingPreview(file: File?) {
    if (file == null || !file.exists()) return
    audioHelper.stopPlayback()
    _studioState.value = _studioState.value.copy(isPlayingPreview = true)
    audioHelper.playAudioFile(
      file = file,
      onComplete = {
        _studioState.value = _studioState.value.copy(isPlayingPreview = false)
      }
    )
  }

  fun stopGreetingPreview() {
    audioHelper.stopPlayback()
    _studioState.value = _studioState.value.copy(isPlayingPreview = false)
  }

  fun saveRecordedGreeting(title: String) {
    val file = _studioState.value.recordedFile ?: return
    viewModelScope.launch(Dispatchers.IO) {
      val newGreeting = Greeting(
        title = title.ifBlank { "Custom Recording" },
        type = "USER_RECORDED",
        scriptText = "Personal recorded voice greeting",
        audioFilePath = file.absolutePath,
        durationSeconds = maxOf(1, _studioState.value.recordingSeconds),
        isActive = true,
        isPreset = false
      )
      val id = repository.insertGreeting(newGreeting)
      repository.setActiveGreeting(id)
      _studioState.value = _studioState.value.copy(
        recordedFile = null,
        recordingSeconds = 0,
        isPlayingPreview = false
      )
    }
  }

  // --- TTS GREETING SYNTHESIS ---
  fun updateTtsScript(text: String) {
    _studioState.value = _studioState.value.copy(ttsInputText = text)
  }

  fun updateTtsPitch(pitch: Float) {
    _studioState.value = _studioState.value.copy(ttsPitch = pitch)
  }

  fun updateTtsSpeed(speed: Float) {
    _studioState.value = _studioState.value.copy(ttsSpeed = speed)
  }

  fun previewTtsVoice() {
    audioHelper.stopPlayback()
    _studioState.value = _studioState.value.copy(isTtsSpeaking = true)
    audioHelper.speak(
      text = _studioState.value.ttsInputText,
      pitch = _studioState.value.ttsPitch,
      speechRate = _studioState.value.ttsSpeed,
      onDone = {
        _studioState.value = _studioState.value.copy(isTtsSpeaking = false)
      }
    )
  }

  fun stopTtsVoice() {
    audioHelper.stopSpeaking()
    _studioState.value = _studioState.value.copy(isTtsSpeaking = false)
  }

  fun saveTtsGreeting(title: String) {
    val script = _studioState.value.ttsInputText
    viewModelScope.launch(Dispatchers.IO) {
      val newGreeting = Greeting(
        title = title.ifBlank { "AI Synthesized Greeting" },
        type = "AI_VOICE",
        scriptText = script,
        durationSeconds = maxOf(5, script.split(" ").size / 3),
        isActive = true,
        isPreset = false
      )
      val id = repository.insertGreeting(newGreeting)
      repository.setActiveGreeting(id)
    }
  }

  fun setActiveGreeting(id: Long) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.setActiveGreeting(id)
    }
  }

  fun deleteGreeting(greeting: Greeting) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.deleteGreeting(greeting)
      if (greeting.audioFilePath.isNotBlank()) {
        try { File(greeting.audioFilePath).delete() } catch (_: Exception) {}
      }
    }
  }

  fun playGreetingScriptAudio(greeting: Greeting) {
    audioHelper.stopPlayback()
    audioHelper.stopSpeaking()
    if (greeting.audioFilePath.isNotBlank() && File(greeting.audioFilePath).exists()) {
      audioHelper.playAudioFile(File(greeting.audioFilePath))
    } else {
      audioHelper.speak(greeting.scriptText)
    }
  }

  // --- SETTINGS ---
  fun updateRingsBeforePickup(rings: Int) {
    _appSettings.value = _appSettings.value.copy(ringsBeforePickup = rings)
  }

  fun updateMaxRecordingSeconds(sec: Int) {
    _appSettings.value = _appSettings.value.copy(maxRecordingSeconds = sec)
  }

  fun toggleCallScreening(enabled: Boolean) {
    _appSettings.value = _appSettings.value.copy(callScreeningEnabled = enabled)
  }

  fun toggleSpamFilter(enabled: Boolean) {
    _appSettings.value = _appSettings.value.copy(spamFilterEnabled = enabled)
  }

  fun toggleAiSummaries(enabled: Boolean) {
    _appSettings.value = _appSettings.value.copy(aiSummaryEnabled = enabled)
  }

  fun toggleBiometricLock(enabled: Boolean) {
    _appSettings.value = _appSettings.value.copy(biometricLockEnabled = enabled)
  }

  override fun onCleared() {
    super.onCleared()
    playbackJob?.cancel()
    simulatorJob?.cancel()
    greetingRecorderJob?.cancel()
    audioHelper.release()
  }
}
