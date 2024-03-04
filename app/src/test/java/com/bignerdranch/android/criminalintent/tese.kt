package com.bignerdranch.android.criminalintent

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


suspend fun main(){
    getUsers().collect { user -> println(user) }
}

fun getUsers(): Flow<String> = flow {
    val database = listOf("Tom", "Bob")  // условная база данных
    var i = 1;
    for (j in 0 .. 5){
        delay(1000L) // имитация продолжительной работы
        println("Emit $i item")
        emit(database[j]) // емитируем значение
        i++
    }
}

//suspend fun main() = coroutineScope<Unit>{
//    launch {
//        getUsers().forEach { user -> println(user) }
//    }
//}
//
//suspend fun getUsers(): List<String> {
//    delay(1000L)  // имитация продолжительной работы
//    return listOf("Tom", "Bob", "Sam")
//}