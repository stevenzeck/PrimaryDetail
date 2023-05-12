@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.agp) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}