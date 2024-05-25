package com.example.smartstudy.data.repository

import com.example.smartstudy.data.local.TaskDao
import com.example.smartstudy.domain.model.Task
import com.example.smartstudy.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepoImplementation @Inject constructor(
    private val taskDao: TaskDao
): TaskRepository {
    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun getTask(taskId: Int): Task? {
        return taskDao.getTask(taskId)
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun deleteTaskBySID(subjectId: Int) {
        taskDao.deleteTaskBySID(subjectId)
    }

    override fun getTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId)
    }

    override fun getTasks(): Flow<List<Task>> {
        return taskDao.getTasks()
    }

    override fun getAllIncomingTasks(): Flow<List<Task>> {
        return taskDao.getTasks()
            .map { it ->
                it.filter {task ->
                    task.isCompleted.not()
                }
                    .sortedWith(
                        compareBy<Task> {
                            it.dueDate
                        }
                            .thenByDescending {
                                it.priority
                            }
                    )
            }
    }

    override fun getAllUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId)
            .map { it ->
                it.filter {task ->
                    task.isCompleted.not()
                }
                    .sortedWith(
                        compareBy<Task> {
                            it.dueDate
                        }
                            .thenByDescending {
                                it.priority
                            }
                    )
            }
    }

    override fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId)
            .map { it ->
                it.filter {task ->
                    task.isCompleted
                }
                    .sortedWith(
                        compareBy<Task> {
                            it.dueDate
                        }
                            .thenByDescending {
                                it.priority
                            }
                    )
            }
    }

}