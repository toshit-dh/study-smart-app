package com.example.smartstudy.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.smartstudy.domain.model.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)

    @Query(
        "SELECT * FROM Session"
    )
    fun getSessions(): Flow<List<Session>>

    @Query(
        "SELECT * FROM Session WHERE sessionSubjectId = :subjectId"
    )
    fun getSessionsForSubject(subjectId: Int): Flow<List<Session>>

    @Query(
        "SELECT SUM(duration) FROM Session"
    )
    fun getSessionDuration(): Flow<Long>

    @Query(
        "SELECT SUM(duration) FROM Session WHERE sessionSubjectId = :subjectId"
    )
    fun getSessionDurationForSubject(subjectId: Int): Flow<Long>

    @Query(
        "DELETE FROM Session WHERE sessionSubjectId = :subjectId"
    )
    fun deleteSessionForSubject(subjectId: Int)
}