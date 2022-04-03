plugins {
    id("org.jetbrains.kotlin.jvm")
}

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