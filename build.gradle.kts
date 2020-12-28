plugins {
    kotlin("jvm") version "1.4.20"
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

application {
    mainClass.set("org.hildan.ipm.helper.MainKt")
    applicationDefaultJvmArgs = listOf("-XX:+UnlockExperimentalVMOptions", "-XX:+UseZGC", "-Xlog:gc")
}

tasks {
    compileKotlin {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("failed", "standardOut", "standardError")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showStackTraces = true
        }
    }
}
