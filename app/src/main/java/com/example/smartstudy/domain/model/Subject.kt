package com.example.smartstudy.domain.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.compose.gradient1
import com.example.compose.gradient2
import com.example.compose.gradient3
import com.example.compose.gradient4
import com.example.compose.gradient5
import com.example.smartstudy.data.utils.ColorListConverter

@Entity
@TypeConverters(ColorListConverter::class)
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val subjectId: Int? = null,
    val name: String,
    val goalHours: String,
    val color: List<Int>
){
    companion object {
        val subjectColors = listOf(gradient1, gradient2, gradient3, gradient4, gradient5)
    }
}
