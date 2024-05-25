package com.example.smartstudy.domain.repository

import androidx.room.Query
import androidx.room.Upsert
import com.example.smartstudy.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun upsertTask(task: Task)

    suspend fun getTask(taskId: Int): Task?

    suspend fun deleteTask(taskId: Int)

    suspend fun deleteTaskBySID(subjectId: Int)

    fun getTasksForSubject(subjectId: Int): Flow<List<Task>>

    fun getTasks(): Flow<List<Task>>

    fun getAllIncomingTasks(): Flow<List<Task>>

     fun getAllUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>>

     fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>>
}