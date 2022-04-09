/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

plugins {
    id("org.jetbrains.kotlin.jvm")
}

apply(plugin = "kiln-standard")

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io/")
    }
    maven { url = uri("https://projectlombok.org/edge-releases") }
}

dependencies {
    compileOnly(project(":"))
    compileOnly("net.minecraft:client-1.18.2:yarn")

    compileOnly("org.projectlombok:lombok:edge-SNAPSHOT")
    annotationProcessor("org.projectlombok:lombok:edge-SNAPSHOT")
}