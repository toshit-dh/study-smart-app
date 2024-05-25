package com.example.smartstudy.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.smartstudy.domain.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Upsert
    suspend fun upsertTask(task: Task)

    @Query(
        "SELECT * FROM Task WHERE taskId = :taskId"
    )
    suspend fun getTask(taskId: Int): Task?

    @Query(
        "DELETE FROM Task WHERE taskId = :taskId"
    )
    suspend fun deleteTask(taskId: Int)

    @Query(
        "DELETE FROM Task WHERE taskSubjectId = :subjectId"
    )
    suspend fun deleteTaskBySID(subjectId: Int)

    @Query(
        "SELECT * FROM Task WHERE taskSubjectId = :subjectId"
    )
    fun getTasksForSubject(subjectId: Int): Flow<List<Task>>

    @Query(
        "SELECT * FROM Task"
    )
    fun getTasks(): Flow<List<Task>>
}