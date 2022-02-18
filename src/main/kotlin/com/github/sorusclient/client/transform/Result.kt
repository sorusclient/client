package com.github.sorusclient.client.transform

import java.util.function.Consumer

class Result<T>(private val results: List<T>) {

    fun apply(consumer: Consumer<out T>) {
        for (result in results) {
            (consumer as Consumer<T>).accept(result)
        }
    }

    fun nth(index: Int): Result<T> {
        return Result(listOf(results[index]))
    }

    fun nth(indices: IntRange)  {
        Result(listOf(results.subList(indices.first, indices.last)))
    }

}