plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ktlint)
}

kotlin {
    jvm()
}

dependencies {
    commonMainImplementation(libs.kotlinx.coroutines)
    commonMainImplementation(projects.client)
}
