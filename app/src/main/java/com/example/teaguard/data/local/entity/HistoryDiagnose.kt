package com.example.teaguard.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class HistoryDiagnose(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name : String,
    val imageUri : String,
    val diagnosis : String,
    val recommendation : String,
    val date : String
) : Parcelable