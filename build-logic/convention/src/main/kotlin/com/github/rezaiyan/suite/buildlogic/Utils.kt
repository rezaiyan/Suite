@file:Suppress("UnstableApiUsage")

package com.github.rezaiyan.suite.buildlogic

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.LibraryDefaultConfig
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.util.*

internal fun Project.Properties(name: String): Properties {
    return file(name).inputStream().use {
        Properties().apply { load(it) }
    }
}

internal fun DefaultConfig.targetSdk(version: Int?) {
    when (this) {
        is ApplicationDefaultConfig -> targetSdk = version
        is LibraryDefaultConfig -> targetSdk = version
        else -> error("invalid DefaultConfig=${this}")
    }
}

internal fun CommonExtension<*, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}

internal fun KotlinProjectExtension.sourceSets(configure: Action<NamedDomainObjectContainer<KotlinSourceSet>>): Unit =
    (this as ExtensionAware).extensions.configure("sourceSets", configure)

internal fun Project.hasProductFlavor(name: String): Boolean {
    return extensions.findByType<BaseExtension>()
        ?.productFlavors?.any { it.name == name }
        ?: false
}

internal fun Project.fileTree(
    baseDir: String,
    includes: Collection<String>,
    excludes: Collection<String>,
): ConfigurableFileTree {
    return fileTree(baseDir).apply {
        include(includes)
        exclude(excludes)
    }
}

internal fun String.cptlz(): String = capitalize(Locale.ROOT)

internal fun Project.configureLibrary(block: LibraryExtension.() -> Unit) {
    extensions.configure(LibraryExtension::class.java, block)
}

internal fun Project.configureApp(block: BaseAppModuleExtension.() -> Unit) {
    extensions.configure(BaseAppModuleExtension::class.java, block)
}

internal fun Project.configureAndroid(block: CommonExtension<*, *, *, *>.() -> Unit) {
    if (extensions.findByType(LibraryExtension::class.java) != null) {
        extensions.configure(LibraryExtension::class.java, block)
        return
    }
    if (extensions.findByType(BaseAppModuleExtension::class.java) != null) {
        extensions.configure(BaseAppModuleExtension::class.java, block)
        return
    }
    error("$displayName is not an android module")
}

internal val BaseExtension.variants: DomainObjectSet<out BaseVariant>?
    get() = when (this) {
        is AppExtension -> applicationVariants
        is LibraryExtension -> libraryVariants
        is TestExtension -> applicationVariants
        else -> null
    }