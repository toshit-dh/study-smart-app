package com.example.smartstudy.domain.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.smartstudy.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun insertSession(session: Session)

    suspend fun deleteSession(session: Session)

    fun getSessions(): Flow<List<Session>>

    fun getSessionsForSubject(subjectId: Int): Flow<List<Session>>

    fun getSessionDuration(): Flow<Long>

    fun getSessionDurationForSubject(subjectId: Int): Flow<Long>

    fun deleteSessionForSubject(subjectId: Int)

    fun getRecentFiveSessions(): Flow<List<Session>>

    fun getRecentSessionsForSubject(subjectId: Int): Flow<List<Session>>

}