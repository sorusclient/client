/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

import com.github.glassmc.kiln.standard.KilnStandardExtension
import com.github.glassmc.kiln.standard.environment.Environment

buildscript {
    repositories {
        mavenCentral()

        maven {
            url = uri("https://glassmc.ml/repository")
        }
        maven {
            url = uri("https://jitpack.io/")
        }
    }

    dependencies {
        classpath("com.github.glassmc:kiln:0.8.6")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.20"
}

apply(plugin = "kiln-main")

allprojects {
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java)
        .all {
            kotlinOptions {
                if (System.getProperty("release") == "true") {
                    freeCompilerArgs += arrayOf(
                        "-Xno-call-assertions",
                        "-Xno-receiver-assertions",
                        "-Xno-param-assertions"
                    )
                }
            }
        }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io/")
    }
}

dependencies {
    "shadowApi"("org.jetbrains.kotlin:kotlin-stdlib:1.6.20")
    "shadowApi"("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")

    "shadowApi"("org.ow2.asm:asm-tree:9.2")
    "shadowApi"("commons-io:commons-io:2.11.0")
    "shadowApi"("com.google.guava:guava:31.1-jre")
    "shadowApi"("org.json:json:20220320")

    "shadowApi"("org.java-websocket:Java-WebSocket:1.5.2")
    "shadowApi"("io.ktor:ktor-client-cio:1.6.8")
}

configure<KilnStandardExtension> {
    environment = SorusEnvironment()
}

class SorusEnvironment: Environment {

    override fun getMainClass(): String {
        return "com.github.sorusclient.client.bootstrap.MainKt"
    }

    override fun getProgramArguments(environment: String, version: String): Array<String> {
        return arrayOf("--minecraftVersion", version)
    }

    override fun getRuntimeDependencies(file: File): Array<String> {
        return emptyArray()
    }

    override fun getVersion(version: String): String {
        return "Sorus-$version"
    }

}