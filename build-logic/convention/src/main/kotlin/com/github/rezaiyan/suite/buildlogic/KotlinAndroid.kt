package com.github.rezaiyan.suite.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *>, ) {
    commonExtension.apply {
        compileSdk = config.compileSdkVersion
        defaultConfig {
            minSdk = config.minSdkVersion
            targetSdk(config.targetSdkVersion)
            testInstrumentationRunner = config.test.instrumentationRunner
        }
        compileOptions {
            sourceCompatibility = config.javaVersion
            targetCompatibility = config.javaVersion
            isCoreLibraryDesugaringEnabled = true
        }

        testOptions {
            animationsDisabled = config.test.animationDisabled
            unitTests.isReturnDefaultValues = config.test.returnDefaultValues
            execution = config.test.execution
        }

        configureKotlin()

        // https://developer.android.com/reference/tools/gradle-api/4.1/com/android/build/api/dsl/LintOptions
        lint {
            abortOnError = true // use --continue if you don't want to abort on the first error report
            checkReleaseBuilds = false // don't run automatically lintVitalRelease on assembleRelease
            xmlReport = true // needed for danger
            htmlReport = true // human readable
            warningsAsErrors = false
            baseline = file("lint-baseline.xml")
            checkGeneratedSources = false
        }

        libraries {
            implementation("kotlin-stdlib")
            implementation("tools-timber")
            add("coreLibraryDesugaring", "tools-desugar")
        }

        buildTypes {
            getByName("release") {
                manifestPlaceholders["screenOrientation"] = "portrait"
            }
            getByName("debug") {
                manifestPlaceholders["screenOrientation"] = "unspecified"
            }
        }
    }

}

/**
 * Configure base Kotlin options for JVM (non-Android)
 */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = config.javaVersion
        targetCompatibility = config.javaVersion
    }

    configureKotlin()
}

/**
 * Configure base Kotlin options
 */
private fun Project.configureKotlin() {
    // Use withType to workaround https://youtrack.jetbrains.com/issue/KT-55947
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = config.javaVersion.toString()
            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
            )
        }
    }
}

/*
@file:Suppress("UnstableApiUsage")

package com.github.rezaiyan.suite.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinJvm() {

    tasks.withType(JavaCompile::class.java) {
        sourceCompatibility = config.javaVersion.toString()
        targetCompatibility = config.javaVersion.toString()
    }
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = config.javaVersion
        targetCompatibility = config.javaVersion
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = config.javaVersion.toString()
            // Treat all Kotlin warnings as errors (disabled by default)
            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
            )
        }
    }

}

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *>,
) = with(commonExtension) {
    compileSdk = config.compileSdkVersion

    defaultConfig {
        minSdk = config.minSdkVersion
        targetSdk(config.targetSdkVersion)

        testInstrumentationRunner = config.test.instrumentationRunner
    }

    compileOptions.isCoreLibraryDesugaringEnabled = true
    configureKotlinJvm()

    testOptions {
        animationsDisabled = config.test.animationDisabled
        unitTests.isReturnDefaultValues = config.test.returnDefaultValues
        execution = config.test.execution
    }

    // https://developer.android.com/reference/tools/gradle-api/4.1/com/android/build/api/dsl/LintOptions
    lint {
        abortOnError = true // use --continue if you don't want to abort on the first error report
        checkReleaseBuilds = false // don't run automatically lintVitalRelease on assembleRelease
        xmlReport = true // needed for danger
        htmlReport = true // human readable
        warningsAsErrors = false
        baseline = file("lint-baseline.xml")
        checkGeneratedSources = false
    }

    libraries {
        // core and mandatory tooling
        implementation("kotlin-stdlib")
        implementation("tools-timber")
        add("coreLibraryDesugaring", "tools-desugar")
    }


    buildTypes {
        getByName("release") {
            manifestPlaceholders["screenOrientation"] = "portrait"
        }
        getByName("debug") {
            manifestPlaceholders["screenOrientation"] = "unspecified"
        }
    }

}*/
