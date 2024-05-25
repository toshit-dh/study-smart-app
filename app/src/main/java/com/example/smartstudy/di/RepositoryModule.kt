package com.example.smartstudy.di

import com.example.smartstudy.data.repository.SessionRepoImplementation
import com.example.smartstudy.data.repository.SubjectRepoImplementation
import com.example.smartstudy.data.repository.TaskRepoImplementation
import com.example.smartstudy.domain.repository.SessionRepository
import com.example.smartstudy.domain.repository.SubjectRepository
import com.example.smartstudy.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindSubjectRepo(
        impl: SubjectRepoImplementation
    ): SubjectRepository

    @Singleton
    @Binds
    abstract fun bindSessionRepo(
        impl: SessionRepoImplementation
    ): SessionRepository

    @Singleton
    @Binds
    abstract fun bindTaskRepo(
        impl: TaskRepoImplementation
    ): TaskRepository

}