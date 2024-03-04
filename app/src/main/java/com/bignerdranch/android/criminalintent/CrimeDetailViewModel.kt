package com.bignerdranch.android.criminalintent

import android.widget.Button
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.criminalintent.crimeAdapter.Crime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class CrimeDetailViewModel(crimeId: UUID): ViewModel() {

    private val crimeRepository = CrimeRepository.get()

    private val _crime: MutableStateFlow<Crime?> = MutableStateFlow(null)
    val crime: StateFlow<Crime?> = _crime.asStateFlow()
    //Represents this mutable state flow as a read-only state flow.

    // It is used to switch the execution context of a coroutine to a different dispatcher,
    // which defines the thread or thread pool on which the coroutine will be executed.
    // Contexts are typically associated with dispatchers,
    // which handle the scheduling of coroutines onto threads.
    // https://medium.com/@humzakhalid94/understanding-the-power-of-withcontext-in-coroutines-15155f19518a
    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _crime.value = crimeRepository.getCrime(crimeId)
            }
        }
    }

    fun updateCrime(onUpdate: (Crime) -> Crime) {
        _crime.update { oldCrime ->
            oldCrime?.let { onUpdate(it) }
        }
    }

    fun updateCrimeInRepository() {
        crime.value?.let { crimeRepository.updateCrime(it) }
    }

    override fun onCleared() {
        super.onCleared()
        updateCrimeInRepository()
    }
}

class CrimeDetailViewModelFactory(
    private val crimeId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CrimeDetailViewModel(crimeId) as T
    }
}