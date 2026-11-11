package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Greeting
import com.example.data.model.Voicemail
import kotlinx.coroutines.flow.Flow

@Dao
interface VoicemailDao {
  @Query("SELECT * FROM voicemails ORDER BY timestampMillis DESC")
  fun getAllVoicemails(): Flow<List<Voicemail>>

  @Query("SELECT * FROM voicemails WHERE id = :id")
  suspend fun getVoicemailById(id: Long): Voicemail?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertVoicemail(voicemail: Voicemail): Long

  @Update
  suspend fun updateVoicemail(voicemail: Voicemail)

  @Delete
  suspend fun deleteVoicemail(voicemail: Voicemail)

  @Query("DELETE FROM voicemails WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("UPDATE voicemails SET isRead = :isRead WHERE id = :id")
  suspend fun updateReadStatus(id: Long, isRead: Boolean)

  @Query("UPDATE voicemails SET isStarred = NOT isStarred WHERE id = :id")
  suspend fun toggleStar(id: Long)

  @Query("DELETE FROM voicemails")
  suspend fun clearAll()
}

@Dao
interface GreetingDao {
  @Query("SELECT * FROM greetings ORDER BY id ASC")
  fun getAllGreetings(): Flow<List<Greeting>>

  @Query("SELECT * FROM greetings WHERE isActive = 1 LIMIT 1")
  fun getActiveGreeting(): Flow<Greeting?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGreeting(greeting: Greeting): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(greetings: List<Greeting>)

  @Update
  suspend fun updateGreeting(greeting: Greeting)

  @Delete
  suspend fun deleteGreeting(greeting: Greeting)

  @Transaction
  suspend fun setActive(greetingId: Long) {
    clearActiveStatus()
    markActive(greetingId)
  }

  @Query("UPDATE greetings SET isActive = 0")
  suspend fun clearActiveStatus()

  @Query("UPDATE greetings SET isActive = 1 WHERE id = :id")
  suspend fun markActive(id: Long)

  @Query("SELECT COUNT(*) FROM greetings")
  suspend fun count(): Int
}
