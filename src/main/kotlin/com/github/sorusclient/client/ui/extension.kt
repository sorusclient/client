package com.github.sorusclient.client.ui

import com.github.sorusclient.client.ui.framework.constraint.*
import com.github.sorusclient.client.util.Color
import java.util.function.Function

inline fun <T> T.apply2(block: T.() -> Unit): T {
    this.run(block)
    return this
}

fun Color.toAbsolute(): Absolute {
    return Absolute(this)
}

fun Double.toAbsolute(): Absolute {
    return Absolute(this)
}

fun String.toAbsolute(): Absolute {
    return Absolute(this)
}

fun Double.toRelative(): Relative {
    return Relative(this)
}

fun Int.toSide(): Side {
    return Side(this)
}

fun Double.toCopy(): Copy {
    return Copy(this)
}

fun <T: Any> ((Map<String, Any>) -> T).toDependent(): Dependent {
    return Dependent(this)
}