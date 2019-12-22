plugins {
    kotlin("jvm") version "1.3.61"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")

    testImplementation(kotlin("test-junit"))
    testImplementation("junit:junit:4.12")
}
