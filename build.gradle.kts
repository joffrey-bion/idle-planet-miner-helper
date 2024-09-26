plugins {
    kotlin("jvm") version "2.0.20"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("com.malinskiy.adam:adam:0.5.8")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.1")
}

application {
    mainClass.set("org.hildan.ipm.helper.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
