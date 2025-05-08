// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.android.library") version libs.versions.agp apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}