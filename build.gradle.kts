import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    kotlin("jvm") version "1.9.22"
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.1" apply false
    id("com.google.protobuf") version "0.8.18" apply false
    jacoco
    `maven-publish`
}

repositories {
    mavenCentral()
}

allprojects {
    apply(plugin = "com.google.cloud.artifactregistry.gradle-plugin")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "ca.empire.backoffice.finance"
    version = "1.0.0"

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://packages.confluent.io/maven/")
        maven("artifactregistry://northamerica-northeast1-maven.pkg.dev/empire-stage-registry/java-repo")
    }

    kotlin {
        jvmToolchain("${project.property("jdkTarget")}".toInt())
    }

    dependencies {
        implementation("com.google.protobuf:protobuf-kotlin:${property("protobufVersion")}")
        implementation("com.google.protobuf:protobuf-java:${property("protobufVersion")}")
        implementation("com.google.protobuf:protobuf-java-util:${property("protobufVersion")}")

    }
}

subprojects {
    apply(plugin = "idea")
    apply(plugin = "com.google.protobuf")
    apply(plugin = "jacoco")
    apply(plugin = "maven-publish")

    kotlin {
        jvmToolchain("${project.property("jdkTarget")}".toInt())
    }

    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:3.25.0"
        }

        generateProtoTasks {
            all().forEach {
                it.builtins {
                    create("kotlin")
                }
            }
        }
    }

    dependencies {
        implementation("ca.empire.pu:processingunit:25.1.0.4")
        implementation("ca.empire.pu:open-telemetry:25.1.0.4")
        implementation("com.google.auth:google-auth-library-oauth2-http:${property("googleAuthLibraryVersion")}")
        implementation("io.arrow-kt:arrow-core:${property("arrowVersion")}")
        implementation("org.slf4j:slf4j-api:2.0.7")
        implementation("com.typesafe:config:1.4.2")

        testImplementation("org.junit.jupiter:junit-jupiter:${property("jupiterVersion")}")
    }

    publishing {
        repositories {
            maven {
                url = uri(property("privateMavenUrl")!!)
            }
        }
        publications {
            create<MavenPublication>("mavenJava") {
                groupId = project.group.toString()
                artifactId = project.name
                from(components["java"])
            }
        }
    }
}

// Prevent the root project from generating a JAR
tasks.withType<Jar> {
    enabled = false
}