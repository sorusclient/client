/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.bootstrap

fun main(args: Array<String>) {
    val classLoader = com.github.sorusclient.client.bootstrap.loader.ClassLoader()
    val wrapperClass = classLoader.loadClass("com.github.sorusclient.client.bootstrap.Launcher")!!
    val mainMethod = wrapperClass.getMethod("main", Array<String>::class.java)
    mainMethod.invoke(null, args)
}