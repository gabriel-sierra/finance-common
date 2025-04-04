
plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.3")
}

group = "ca.empire.backoffice.finance"
version = "1.0-SNAPSHOT"