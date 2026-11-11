package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.io.File
import java.io.IOException
import java.util.Locale

class AudioManagerHelper(private val context: Context) : TextToSpeech.OnInitListener {
  private var textToSpeech: TextToSpeech? = null
  private var isTtsReady = false

  private var mediaPlayer: MediaPlayer? = null
  private var mediaRecorder: MediaRecorder? = null
  private var toneGenerator: ToneGenerator? = null

  private var currentRecordingFile: File? = null
  private var isRecording = false

  init {
    try {
      textToSpeech = TextToSpeech(context.applicationContext, this)
      toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
    } catch (e: Exception) {
      Log.e("AudioManagerHelper", "Init error", e)
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val result = textToSpeech?.setLanguage(Locale.US)
      isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
    }
  }

  // --- TTS SPEECH ---
  fun speak(text: String, pitch: Float = 1.0f, speechRate: Float = 1.0f, onDone: (() -> Unit)? = null) {
    if (!isTtsReady || textToSpeech == null) {
      onDone?.invoke()
      return
    }
    stopPlayback()
    textToSpeech?.setPitch(pitch)
    textToSpeech?.setSpeechRate(speechRate)

    val utteranceId = "tts_${System.currentTimeMillis()}"
    textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
      override fun onStart(id: String?) {}
      override fun onDone(id: String?) {
        if (id == utteranceId) onDone?.invoke()
      }
      override fun onError(id: String?) {
        if (id == utteranceId) onDone?.invoke()
      }
    })

    textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
  }

  fun stopSpeaking() {
    try {
      textToSpeech?.stop()
    } catch (e: Exception) {
      Log.e("AudioManagerHelper", "Error stopping TTS", e)
    }
  }

  // --- ANSWERING MACHINE BEEP TONE ---
  fun playAnsweringMachineBeep(volume: Float = 0.85f, onFinished: (() -> Unit)? = null) {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 350)
      android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        onFinished?.invoke()
      }, 400)
    } catch (e: Exception) {
      Log.e("AudioManagerHelper", "Error playing beep", e)
      onFinished?.invoke()
    }
  }

  // --- VIBRATION (FOR RINGING) ---
  fun vibratePattern(pattern: LongArray) {
    try {
      val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(pattern, -1)
      }
    } catch (e: Exception) {
      Log.e("AudioManagerHelper", "Vibrate error", e)
    }
  }

  fun cancelVibration() {
    try {
      val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }
      vibrator?.cancel()
    } catch (e: Exception) {
      Log.e("AudioManagerHelper", "Cancel vibration error", e)
    }
  }

  // --- RECORDING ---
  fun startRecording(prefix: String = "audio"): File? {
    stopRecording()
    stopPlayback()

    val dir = File(context.filesDir, "recordings").apply { if (!exists()) mkdirs() }
    val file = File(dir, "${prefix}_${System.currentTimeMillis()}.m4a")
    currentRecordingFile = file

    try {
      mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
      } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
      }

      mediaRecorder?.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setAudioEncodingBitRate(64000)
        setAudioSamplingRate(44100)
        setOutputFile(file.absolutePath)
        prepare()
        start()
      }
      isRecording = true
      return file
    } catch (e: Exception) {
      Log.e("AudioManagerHelper", "startRecording failed", e)
      isRecording = false
      return null
    }
  }

  fun getMaxAmplitude(): Int {
    return if (isRecording && mediaRecorder != null) {
      try {
        mediaRecorder?.maxAmplitude ?: 0
      } catch (e: Exception) {
        0
      }
    } else {
      0
    }
  }

  fun stopRecording(): File? {
    if (!isRecording) return currentRecordingFile
    return try {
      mediaRecorder?.apply {
        stop()
        reset()
        release()
      }
      mediaRecorder = null
      isRecording = false
      currentRecordingFile
    } catch (e: Exception) {
      Log.e("AudioManagerHelper", "stopRecording error", e)
      mediaRecorder = null
      isRecording = false
      currentRecordingFile
    }
  }

  // --- PLAYBACK ---
  fun playAudioFile(
    file: File,
    speed: Float = 1.0f,
    onProgress: ((currentMs: Int, totalMs: Int) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
  ): Boolean {
    stopPlayback()
    if (!file.exists()) return false

    return try {
      mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
          AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        )
        setDataSource(file.absolutePath)
        prepare()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          playbackParams = playbackParams.setSpeed(speed)
        }
        setOnCompletionListener {
          onComplete?.invoke()
          stopPlayback()
        }
        start()
      }
      true
    } catch (e: Exception) {
      Log.e("AudioManagerHelper", "playAudioFile failed", e)
      false
    }
  }

  fun pausePlayback() {
    mediaPlayer?.let {
      if (it.isPlaying) it.pause()
    }
  }

  fun resumePlayback() {
    mediaPlayer?.let {
      if (!it.isPlaying) it.start()
    }
  }

  fun seekTo(positionMs: Int) {
    mediaPlayer?.seekTo(positionMs)
  }

  fun isPlaying(): Boolean {
    return mediaPlayer?.isPlaying ?: false
  }

  fun getPlaybackPosition(): Int {
    return mediaPlayer?.currentPosition ?: 0
  }

  fun getPlaybackDuration(): Int {
    return mediaPlayer?.duration ?: 0
  }

  fun setPlaybackSpeed(speed: Float) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      mediaPlayer?.let {
        if (it.isPlaying) {
          it.playbackParams = it.playbackParams.setSpeed(speed)
        }
      }
    }
  }

  fun stopPlayback() {
    try {
      mediaPlayer?.apply {
        if (isPlaying) stop()
        reset()
        release()
      }
    } catch (e: Exception) {
      Log.e("AudioManagerHelper", "stopPlayback error", e)
    }
    mediaPlayer = null
  }

  fun release() {
    stopRecording()
    stopPlayback()
    stopSpeaking()
    try {
      toneGenerator?.release()
    } catch (e: Exception) {
      Log.e("AudioManagerHelper", "toneGenerator release error", e)
    }
    try {
      textToSpeech?.shutdown()
    } catch (e: Exception) {
      Log.e("AudioManagerHelper", "TTS shutdown error", e)
    }
  }
}
