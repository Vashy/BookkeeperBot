package it.vashykator

typealias Matrix<T> = List<List<T>>

fun <T : Any> emptyMatrix(): Matrix<T> = listOf(listOf())
