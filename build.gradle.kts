plugins {
    kotlin("jvm") version "2.0.0"
    application
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("com.malinskiy:adam:0.2.5")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

application {
    mainClass.set("org.hildan.ipm.helper.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
