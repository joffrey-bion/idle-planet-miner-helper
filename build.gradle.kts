plugins {
    kotlin("jvm") version "1.4.21"
    application
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("com.malinskiy:adam:0.2.5")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

application {
    mainClass.set("org.hildan.ipm.helper.MainKt")
}

tasks {
    val compilerArgs = listOf(
        "-Xopt-in=kotlin.RequiresOptIn",
        "-Xopt-in=kotlin.time.ExperimentalTime",
        "-Xinline-classes"
    )
    compileKotlin {
        kotlinOptions.freeCompilerArgs += compilerArgs
    }
    compileTestKotlin {
        kotlinOptions.freeCompilerArgs += compilerArgs
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
