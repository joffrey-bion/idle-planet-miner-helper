plugins {
    kotlin("jvm") version "1.3.61"
    application
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

application {
    mainClassName = "org.hildan.ipm.helper.MainKt"
    applicationDefaultJvmArgs = listOf("-XX:+UnlockExperimentalVMOptions", "-XX:+UseZGC", "-Xlog:gc")
}
