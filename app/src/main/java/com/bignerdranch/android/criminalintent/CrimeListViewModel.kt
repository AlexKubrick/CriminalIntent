package com.bignerdranch.android.criminalintent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.criminalintent.crimeAdapter.Crime
import com.bignerdranch.android.criminalintent.CrimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrimeListViewModel: ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    private val _crimes: MutableStateFlow<List<Crime>> = MutableStateFlow(emptyList())
    val crimes: StateFlow<List<Crime>>
        get() = _crimes.asStateFlow()


    init {
        viewModelScope.launch {
//            delay(5000)
//            for (i in 0 until 100) {
//                val crime = Crime(
//                    id = UUID.randomUUID(),
//                    title = "Crime #$i",
//                    date = Date(),
//                    isSolved = i % 2 == 0
//                )
//
//                crimes += crime
//            }

            crimeRepository.getCrimes().collect {
                _crimes.value = it
            }
        }
    }

    suspend fun addCrime(crime: Crime) = withContext(Dispatchers.IO) {
        crimeRepository.addCrime(crime)
    }





//    suspend fun loadCrimes(): List<Crime> {
////        val result = mutableListOf<Crime>()
////        delay(5000)
////        for (i in 0 until 100) {
////            val crime = Crime(
////                id = UUID.randomUUID(),
////                title = "Crime #$i",
////                date = Date(),
////                isSolved = i % 2 == 0
////            )
////
////            result += crime
////        }
//    }
}
