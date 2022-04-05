/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui

import com.github.sorusclient.client.ui.framework.constraint.*
import com.github.sorusclient.client.util.Color

fun Color.toAbsolute(): Absolute {
    return Absolute(this)
}

fun Double.toAbsolute(): Absolute {
    return Absolute(this)
}

fun String?.toAbsolute(): Absolute? {
    return if (this != null) { Absolute(this) } else { null }
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

fun <T: Any> (() -> T).toDependent(): Dependent {
    return Dependent { this() }
}