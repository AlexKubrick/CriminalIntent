package com.bignerdranch.android.criminalintent

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    suspend fun main(){
        getUsers().collect { user -> println(user) }
    }

    fun getUsers(): Flow<String> = flow {
        val database = listOf("Tom", "Bob", "Sam")  // условная база данных
        var i = 1;
        for (item in database){
            delay(4000L) // имитация продолжительной работы
            println("Emit $i item")
            emit(item) // емитируем значение
            i++
        }
    }
}