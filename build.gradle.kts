plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "dev.rebelonion"
version = "1.1.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test", "2.1.0"))

    implementation(kotlin("stdlib-jdk8", "2.1.0"))

    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(17)
}

tasks {
    test {
        testLogging.showStandardStreams = true
        useJUnitPlatform()
    }
    named<Jar>("javadocJar") {
        from(named("dokkaJavadoc"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
