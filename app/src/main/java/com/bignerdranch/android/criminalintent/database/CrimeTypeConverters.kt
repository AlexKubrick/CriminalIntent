package com.bignerdranch.android.criminalintent.database

import androidx.room.TypeConverter
import java.util.Date

class CrimeTypeConverters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
        // time -- Returns the number of milliseconds
        // since January 1, 1970, 00:00:00 GMT represented by this Date object.
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
        // Allocates a Date object and initializes it to represent
        // the specified number of milliseconds
        // since the standard base time known as "the epoch",
        // namely January 1, 1970, 00:00:00 GMT.
    }

}