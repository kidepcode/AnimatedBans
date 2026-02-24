import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "9.3.1"
}

group = "dev.kidepcode"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xjvm-default=all"
        )
    }
}

tasks.processResources {
    filteringCharset = "UTF-8"
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveClassifier.set("")
    // Внутрь кладём только Kotlin stdlib — остальное есть на сервере (Paper).
    dependencies {
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}