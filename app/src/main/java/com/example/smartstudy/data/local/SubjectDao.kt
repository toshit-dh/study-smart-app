package com.example.smartstudy.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.smartstudy.domain.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Upsert
    suspend fun upsertSubject(subject: Subject)

    @Query(
        "SELECT COUNT(*) FROM Subject"
    )
    fun getSubjectsCount(): Flow<Int>

    @Query(
        "SELECT SUM(goalHours) FROM Subject"
    )
    fun getGoalHours(): Flow<Float>

    @Query(
        "SELECT * FROM Subject WHERE subjectId = :subjectId"
    )
    suspend fun getSubject(subjectId: Int): Subject?

    @Query(
        "DELETE FROM Subject WHERE subjectId = :subjectId"
    )
    suspend fun deleteSubject(subjectId: Int)

    @Query(
        "SELECT * FROM Subject"
    )
    fun getSubjects(): Flow<List<Subject>>
}