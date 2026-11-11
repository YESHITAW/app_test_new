package com.example.data.repository

import com.example.data.local.GreetingDao
import com.example.data.local.VoicemailDao
import com.example.data.model.CallerScenario
import com.example.data.model.Greeting
import com.example.data.model.Voicemail
import kotlinx.coroutines.flow.Flow

class AnsweringMachineRepository(
  private val voicemailDao: VoicemailDao,
  private val greetingDao: GreetingDao
) {
  val voicemails: Flow<List<Voicemail>> = voicemailDao.getAllVoicemails()
  val greetings: Flow<List<Greeting>> = greetingDao.getAllGreetings()
  val activeGreeting: Flow<Greeting?> = greetingDao.getActiveGreeting()

  suspend fun insertVoicemail(voicemail: Voicemail): Long {
    return voicemailDao.insertVoicemail(voicemail)
  }

  suspend fun updateVoicemail(voicemail: Voicemail) {
    voicemailDao.updateVoicemail(voicemail)
  }

  suspend fun deleteVoicemail(voicemail: Voicemail) {
    voicemailDao.deleteVoicemail(voicemail)
  }

  suspend fun deleteVoicemailById(id: Long) {
    voicemailDao.deleteById(id)
  }

  suspend fun setReadStatus(id: Long, isRead: Boolean) {
    voicemailDao.updateReadStatus(id, isRead)
  }

  suspend fun toggleStarred(id: Long) {
    voicemailDao.toggleStar(id)
  }

  suspend fun clearAllVoicemails() {
    voicemailDao.clearAll()
  }

  suspend fun insertGreeting(greeting: Greeting): Long {
    return greetingDao.insertGreeting(greeting)
  }

  suspend fun updateGreeting(greeting: Greeting) {
    greetingDao.updateGreeting(greeting)
  }

  suspend fun deleteGreeting(greeting: Greeting) {
    greetingDao.deleteGreeting(greeting)
  }

  suspend fun setActiveGreeting(id: Long) {
    greetingDao.setActive(id)
  }

  fun getCallerScenarios(): List<CallerScenario> {
    return listOf(
      CallerScenario(
        id = "scenario_doctor",
        callerName = "Dr. Emily Carter",
        callerNumber = "+1 (555) 234-8901",
        callerCategory = "MEDICAL",
        initialSpokenMessage = "Hi Alex, this is Dr. Emily Carter from Metro Health Clinic following up on your recent bloodwork. Everything looks very positive overall, but I would like to adjust your vitamin D dosage slightly. Please give our nursing desk a callback before 4:30 PM today at extension 402 to confirm your preferred pharmacy.",
        expectedTranscript = "Hi Alex, this is Dr. Emily Carter from Metro Health Clinic following up on your recent bloodwork. Everything looks very positive overall, but I would like to adjust your vitamin D dosage slightly. Please give our nursing desk a callback before 4:30 PM today at extension 402 to confirm your preferred pharmacy.",
        expectedSummary = "Doctor Carter reviewed lab results (positive overall) and needs callback before 4:30 PM to confirm vitamin D prescription update.",
        expectedKeyPoints = listOf(
          "Lab results are normal overall",
          "Prescription adjustment needed for Vitamin D",
          "Callback requested before 4:30 PM today (ext. 402)"
        ),
        urgency = "HIGH",
        avatarColorHex = 0xFFEF4444
      ),
      CallerScenario(
        id = "scenario_work",
        callerName = "Marcus Vance",
        callerNumber = "+1 (555) 892-4112",
        callerCategory = "WORK",
        initialSpokenMessage = "Hey Alex, Marcus here from the architecture team. The client just approved the revised 3D renderings for the downtown commercial plaza! We are scheduling the final investor walkthrough for Thursday at 10:00 AM. I sent the slide deck to your email. Check it out when you have a moment.",
        expectedTranscript = "Hey Alex, Marcus here from the architecture team. The client just approved the revised 3D renderings for the downtown commercial plaza! We are scheduling the final investor walkthrough for Thursday at 10:00 AM. I sent the slide deck to your email. Check it out when you have a moment.",
        expectedSummary = "Client approved downtown plaza 3D renderings; walkthrough scheduled for Thursday 10 AM. Slides sent via email.",
        expectedKeyPoints = listOf(
          "Client gave full approval on commercial plaza design",
          "Investor walkthrough set for Thursday 10:00 AM",
          "Review emailed slide deck"
        ),
        urgency = "MEDIUM",
        avatarColorHex = 0xFF38BDF8
      ),
      CallerScenario(
        id = "scenario_courier",
        callerName = "Apex Express Courier",
        callerNumber = "+1 (555) 762-0988",
        callerCategory = "DELIVERY",
        initialSpokenMessage = "Hello, this is Dispatch driver Marco from Apex Express. We have a signature-required package for delivery today between 2:00 PM and 4:00 PM. If no one is home, we will leave a locker security pickup code. Call back if you want to reschedule delivery.",
        expectedTranscript = "Hello, this is Dispatch driver Marco from Apex Express. We have a signature-required package for delivery today between 2:00 PM and 4:00 PM. If no one is home, we will leave a locker security pickup code. Call back if you want to reschedule delivery.",
        expectedSummary = "Signature-required package arriving today between 2:00 PM - 4:00 PM. Locker code if unattended.",
        expectedKeyPoints = listOf(
          "Signature package delivery scheduled 2 PM - 4 PM today",
          "Locker code will be left if recipient unavailable",
          "Option to call dispatch to reschedule"
        ),
        urgency = "MEDIUM",
        avatarColorHex = 0xFFF59E0B
      ),
      CallerScenario(
        id = "scenario_family",
        callerName = "Sarah Jenkins",
        callerNumber = "+1 (555) 345-6789",
        callerCategory = "PERSONAL",
        initialSpokenMessage = "Hey Alex! Just checking in about Saturday dinner with Mom and Dave. We booked a table for 7:00 PM at the Italian bistro on 4th street. Let me know if you can make it so I can update the reservation!",
        expectedTranscript = "Hey Alex! Just checking in about Saturday dinner with Mom and Dave. We booked a table for 7:00 PM at the Italian bistro on 4th street. Let me know if you can make it so I can update the reservation!",
        expectedSummary = "Saturday family dinner booked at Italian bistro (4th street) at 7:00 PM; needs headcount confirmation.",
        expectedKeyPoints = listOf(
          "Dinner reservation Saturday 7:00 PM",
          "Location: Italian bistro on 4th street",
          "RSVP needed for headcount"
        ),
        urgency = "LOW",
        avatarColorHex = 0xFF10B981
      ),
      CallerScenario(
        id = "scenario_spam",
        callerName = "Automated Utility Service",
        callerNumber = "+1 (800) 412-9900",
        callerCategory = "SPAM",
        initialSpokenMessage = "Urgent message regarding your residential power utility rate. New government incentives qualify your address for zero dollar upfront solar panel installation. Press 1 to speak with an energy specialist now.",
        expectedTranscript = "Urgent message regarding your residential power utility rate. New government incentives qualify your address for zero dollar upfront solar panel installation. Press 1 to speak with an energy specialist now.",
        expectedSummary = "Robocall promotional solicitation offering solar panel programs. Screened and flagged as marketing spam.",
        expectedKeyPoints = listOf(
          "Automated promotional call",
          "Commercial solar pitch",
          "Recommended action: Keep blocked"
        ),
        urgency = "LOW",
        avatarColorHex = 0xFF94A3B8
      )
    )
  }
}
