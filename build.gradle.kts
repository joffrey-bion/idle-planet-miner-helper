plugins {
    kotlin("jvm") version "2.1.10"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("com.malinskiy.adam:adam:0.5.10")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.12.0")
}

application {
    mainClass.set("org.hildan.ipm.helper.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
