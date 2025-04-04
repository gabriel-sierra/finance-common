plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

group = "ca.empire.backoffice.finance"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("com.google.cloud:google-cloud-storage:2.14.0")
}
