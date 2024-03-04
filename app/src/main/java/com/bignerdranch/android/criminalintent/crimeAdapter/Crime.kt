package com.bignerdranch.android.criminalintent.crimeAdapter

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID
@Entity
data class Crime(
    @PrimaryKey val id: UUID,
    val title: String,
    val date: Date,
    val isSolved: Boolean
//    val requiresPolice: Int
) {
//    companion object {
//        const val NOT_POLICE_TYPE = 0
//        const val POLICE_TYPE = 1
//    }
}