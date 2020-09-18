plugins {
    java
    kotlin("jvm") version "1.4.10"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.hbase:hbase-client:1.4.13")
    implementation("org.apache.hbase:hbase-protocol:1.4.13")
    implementation("org.apache.hbase:hbase-hadoop2-compat:1.4.13")
    implementation("org.apache.hbase:hbase-server:1.4.13")
    //implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.10")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "app.load.Load"
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.withType(Jar::class) {
    manifest {
        attributes["Class-Path"] = configurations.runtimeClasspath.get().files.joinToString(" ") { it.name }
        attributes["Main-Class"] = "app.load.LoadKt"
    }
}
