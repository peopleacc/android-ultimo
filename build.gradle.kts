// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Update this line
    id("com.android.application") version "8.6.0" apply false
    // And this line
    id("com.android.library") version "8.6.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

