package com.github.sorusclient.client.util

import java.util.*

class Pair<A, B>(val first: A, val second: B) {

    override fun equals(other: Any?): Boolean {
        return if (other !is Pair<*, *>) false else first == other.first && second == other.second
    }

    override fun hashCode(): Int {
        return Objects.hash(first, second)
    }

}