package com.example.smartstudy.data.repository

import com.example.smartstudy.data.local.SessionDao
import com.example.smartstudy.data.local.SubjectDao
import com.example.smartstudy.data.local.TaskDao
import com.example.smartstudy.domain.model.Subject
import com.example.smartstudy.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepoImplementation @Inject constructor(
    private val subjectDao: SubjectDao,
    private val taskDao: TaskDao,
    private val sessionDao: SessionDao
): SubjectRepository {
    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsertSubject(subject)
    }

    override fun getSubjectsCount(): Flow<Int> {
        return subjectDao.getSubjectsCount()
    }

    override fun getGoalHours(): Flow<Float> {
        return subjectDao.getGoalHours()
    }

    override suspend fun getSubject(subjectId: Int): Subject? {
        return subjectDao.getSubject(subjectId)
    }

    override suspend fun deleteSubject(subjectId: Int) {
        taskDao.deleteTaskBySID(subjectId)
        sessionDao.deleteSessionForSubject(subjectId)
        subjectDao.deleteSubject(subjectId)
    }

    override fun getSubjects(): Flow<List<Subject>> {
        return subjectDao.getSubjects()
    }
}