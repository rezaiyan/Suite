package com.github.rezaiyan.suite.buildlogic

import org.gradle.api.JavaVersion

@Suppress("ClassName")
internal object config {
    const val minSdkVersion = 26
    const val compileSdkVersion = 33
    const val targetSdkVersion = 32
    val javaVersion = JavaVersion.VERSION_17

    object test {
        const val instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        const val animationDisabled = true
        const val returnDefaultValues = true
        const val execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
}