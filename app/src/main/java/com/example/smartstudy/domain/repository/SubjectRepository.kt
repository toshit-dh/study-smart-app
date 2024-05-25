package com.example.smartstudy.domain.repository

import androidx.room.Query
import androidx.room.Upsert
import com.example.smartstudy.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    suspend fun upsertSubject(subject: Subject)

    fun getSubjectsCount(): Flow<Int>

    fun getGoalHours(): Flow<Float>

    suspend fun getSubject(subjectId: Int): Subject?

    suspend fun deleteSubject(subjectId: Int)

    fun getSubjects(): Flow<List<Subject>>
}