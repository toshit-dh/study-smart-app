package com.example.smartstudy.data.repository

import com.example.smartstudy.data.local.SessionDao
import com.example.smartstudy.domain.model.Session
import com.example.smartstudy.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class SessionRepoImplementation @Inject constructor(
     private val sessionDao: SessionDao
): SessionRepository
{
    override suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session)
    }

    override fun getSessions(): Flow<List<Session>> {
        return sessionDao.getSessions()
    }

    override fun getSessionsForSubject(subjectId: Int): Flow<List<Session>> {
        return sessionDao.getSessionsForSubject(subjectId)
    }

    override fun getSessionDuration(): Flow<Long> {
        return sessionDao.getSessionDuration()
    }

    override fun getSessionDurationForSubject(subjectId: Int): Flow<Long> {
        return sessionDao.getSessionDurationForSubject(subjectId)
    }

    override fun deleteSessionForSubject(subjectId: Int) {
        sessionDao.deleteSessionForSubject(subjectId)
    }

    override fun getRecentFiveSessions(): Flow<List<Session>> {
        return sessionDao.getSessions().take(5)
    }

    override fun getRecentSessionsForSubject(subjectId: Int): Flow<List<Session>> {
        return sessionDao.getSessionsForSubject(subjectId).take(5)
    }

}