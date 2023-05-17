package com.github.rezaiyan.suite.buildlogic

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.project

internal class LibrariesContainer(
    project: Project
) : DependencyHandler by project.dependencies {

    private val catalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
    private val scope = DependencyHandlerScope.of(this)

    fun project(
        configurationName: String,
        dependency: String
    ) {
        scope.add(
            configurationName = configurationName,
            dependencyNotation = scope.project(dependency)
        )
    }

    fun implementation(
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = "implementation",
            dependencyNotation = findLibrary(dependency),
            configurationAction = configurationAction,
        )
    }

    fun debugImplementation(
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = "debugImplementation",
            dependencyNotation = findLibrary(dependency),
            configurationAction = configurationAction,
        )
    }

    fun testImplementation(
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = "testImplementation",
            dependencyNotation = findLibrary(dependency),
            configurationAction = configurationAction,
        )
    }

    fun androidTestImplementation(
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = "androidTestImplementation",
            dependencyNotation = findLibrary(dependency),
            configurationAction = configurationAction,
        )
    }

    fun kapt(
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = "kapt",
            dependencyNotation = findLibrary(dependency),
            configurationAction = configurationAction,
        )
    }

    fun kaptAndroidTest(
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = "kaptAndroidTest",
            dependencyNotation = findLibrary(dependency),
            configurationAction = configurationAction,
        )
    }

    fun add(
        configurationName: String,
        dependency: String,
        configurationAction: Action<Dependency>? = null,
    ) {
        addDependencyTo(
            configuration = configurationName,
            dependencyNotation = findLibrary(dependency),
            configurationAction = configurationAction,
        )
    }

    fun findLibrary(dependency: String): Provider<MinimalExternalModuleDependency> {
        return catalog.findLibrary(dependency).get()
    }

    @Suppress("UNCHECKED_CAST")
    private fun addDependencyTo(
        configuration: String,
        dependencyNotation: Provider<MinimalExternalModuleDependency>,
        configurationAction: Action<Dependency>?
    ) {
        if (configurationAction == null) {
            scope.add(configuration, dependencyNotation)
            return
        }
        val dependency = scope.create(dependencyNotation.get())
        configurationAction.execute(dependency)
        scope.add(configuration, dependency)
    }

}

internal fun Project.libraries(block: LibrariesContainer.() -> Unit) {
    block(LibrariesContainer(this))
}