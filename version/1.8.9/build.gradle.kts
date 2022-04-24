/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
}

group = "com.github.sorusclient.client"
version = parent!!.parent!!.version

apply(plugin = "kiln-standard")

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io/")
    }
}

dependencies {
    compileOnly(project(":"))
    compileOnly("net.minecraft:client-1.8.9:yarn")
}