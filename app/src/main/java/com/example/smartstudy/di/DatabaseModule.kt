package com.example.smartstudy.di

import android.app.Application
import androidx.room.Room
import com.example.smartstudy.data.local.AppDatabase
import com.example.smartstudy.data.local.SessionDao
import com.example.smartstudy.data.local.SubjectDao
import com.example.smartstudy.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase (
        application: Application
    ): AppDatabase{
        return Room
            .databaseBuilder(
                application,
                AppDatabase::class.java,
                "smartstudy.db"
            ).build()
    }

    @Provides
    @Singleton
    fun provideSubjectDao(
        appDatabase: AppDatabase
    ): SubjectDao {
        return appDatabase.subjectDao()
    }

    @Provides
    @Singleton
    fun provideSessionDao(
        appDatabase: AppDatabase
    ): SessionDao {
        return appDatabase.sessionDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(
        appDatabase: AppDatabase
    ): TaskDao {
        return appDatabase.taskDao()
    }
}