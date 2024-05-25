package com.example.smartstudy.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Int? = null,
    val sessionSubjectId: Int,
    val relatedToSubject: String,
    val date: Long,
    val duration: Long
)
