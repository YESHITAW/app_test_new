package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Greeting
import com.example.data.model.Voicemail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [Voicemail::class, Greeting::class],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun voicemailDao(): VoicemailDao
  abstract fun greetingDao(): GreetingDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "answering_machine_database"
        )
          .addCallback(DatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.greetingDao(), database.voicemailDao())
          }
        }
      }

      private suspend fun populateInitialData(
        greetingDao: GreetingDao,
        voicemailDao: VoicemailDao
      ) {
        // Seed default greetings
        val defaultGreetings = listOf(
          Greeting(
            title = "Professional Office",
            type = "DEFAULT_PRESET",
            scriptText = "Hello, you have reached Alex Johnson. I am currently unavailable or on another line. Please leave your full name, contact number, and a detailed message after the tone.",
            durationSeconds = 8,
            isActive = true,
            isPreset = true
          ),
          Greeting(
            title = "Casual & Friendly",
            type = "AI_VOICE",
            scriptText = "Hey there! Thanks for calling. I can't pick up right now, but please leave a message and I'll get back to you as soon as I can. Talk soon!",
            durationSeconds = 6,
            isActive = false,
            isPreset = true
          ),
          Greeting(
            title = "Vacation & Out of Office",
            type = "DEFAULT_PRESET",
            scriptText = "Hi, thank you for reaching out. I am currently out of the office on annual leave with limited access to phone service. For urgent matters, please contact my team at team@office.com.",
            durationSeconds = 11,
            isActive = false,
            isPreset = true
          ),
          Greeting(
            title = "Executive Quick Note",
            type = "AI_VOICE",
            scriptText = "You've reached Alex. Leave a quick message after the beep and our AI assistant will transcribe and prioritize your request immediately.",
            durationSeconds = 7,
            isActive = false,
            isPreset = true
          )
        )
        greetingDao.insertAll(defaultGreetings)

        // Seed initial voicemails with AI summaries and action items
        val now = System.currentTimeMillis()
        val oneHourAgo = now - 3600 * 1000
        val threeHoursAgo = now - 3 * 3600 * 1000
        val yesterday = now - 24 * 3600 * 1000

        val initialVoicemails = listOf(
          Voicemail(
            callerName = "Dr. Emily Carter",
            callerNumber = "+1 (555) 234-8901",
            timestampMillis = oneHourAgo,
            durationSeconds = 34,
            audioFilePath = "sample_medical_clinic",
            transcript = "Hi Alex, this is Dr. Emily Carter from Metro Health Clinic following up on your recent bloodwork. Everything looks very positive overall, but I would like to adjust your vitamin D dosage slightly. Please give our nursing desk a callback before 4:30 PM today at extension 402 to confirm your preferred pharmacy.",
            aiSummary = "Doctor Carter reviewed lab results (positive overall) and needs callback before 4:30 PM to confirm vitamin D prescription update.",
            aiKeyPoints = "Lab results are normal|Prescription adjustment needed for Vitamin D|Callback requested before 4:30 PM today (ext. 402)",
            urgency = "HIGH",
            category = "MEDICAL",
            isRead = false,
            isStarred = true,
            callerAvatarColorHex = 0xFFEF4444
          ),
          Voicemail(
            callerName = "Marcus Vance",
            callerNumber = "+1 (555) 892-4112",
            timestampMillis = threeHoursAgo,
            durationSeconds = 48,
            audioFilePath = "sample_work_project",
            transcript = "Hey Alex, Marcus here from the architecture team. The client just approved the revised 3D renderings for the downtown commercial plaza! We are scheduling the final investor walkthrough for Thursday at 10:00 AM. I sent the slide deck to your email. Check it out when you have a moment.",
            aiSummary = "Client approved downtown plaza 3D renderings; walkthrough scheduled for Thursday 10 AM. Slides sent via email.",
            aiKeyPoints = "Client gave full approval on commercial plaza design|Investor walkthrough set for Thursday 10:00 AM|Review emailed slide deck",
            urgency = "MEDIUM",
            category = "WORK",
            isRead = false,
            isStarred = false,
            callerAvatarColorHex = 0xFF38BDF8
          ),
          Voicemail(
            callerName = "Sarah Jenkins",
            callerNumber = "+1 (555) 345-6789",
            timestampMillis = yesterday,
            durationSeconds = 22,
            audioFilePath = "sample_personal_family",
            transcript = "Hey Alex! Just checking in about Saturday dinner with Mom and Dave. We booked a table for 7:00 PM at the Italian bistro on 4th street. Let me know if you can make it so I can update the reservation!",
            aiSummary = "Saturday family dinner booked at Italian bistro (4th street) at 7:00 PM; needs headcount confirmation.",
            aiKeyPoints = "Dinner reservation Saturday 7:00 PM|Location: Italian bistro on 4th street|RSVP needed for headcount",
            urgency = "LOW",
            category = "PERSONAL",
            isRead = true,
            isStarred = false,
            callerAvatarColorHex = 0xFF10B981
          ),
          Voicemail(
            callerName = "Suspected Solar Promo",
            callerNumber = "+1 (800) 412-9900",
            timestampMillis = yesterday - 1800 * 1000,
            durationSeconds = 18,
            audioFilePath = "sample_spam_screened",
            transcript = "Urgent message regarding your residential power utility rate. New government incentives qualify your address for zero dollar upfront solar panel installation. Press 1 to speak with an energy specialist now.",
            aiSummary = "Robocall promotional solicitation offering solar panel programs. Screened and flagged as marketing spam.",
            aiKeyPoints = "Automated promotional call|Commercial solar pitch|Recommended action: Keep blocked",
            urgency = "LOW",
            category = "SPAM",
            isRead = true,
            isStarred = false,
            callerAvatarColorHex = 0xFF94A3B8
          )
        )

        initialVoicemails.forEach { voicemailDao.insertVoicemail(it) }
      }
    }
  }
}
