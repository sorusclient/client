/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client

object InterfaceManager {

    val interfaces: MutableMap<Class<*>, Any> = HashMap()

    fun <T> register(implementor: T) {
        interfaces[implementor!!::class.java.interfaces[0]] = implementor
    }

    inline fun <reified T> get(): T {
        return interfaces[T::class.java] as T
    }


}