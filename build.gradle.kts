plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("androidx.navigation.safeargs.kotlin") version "2.8.8" apply false
}