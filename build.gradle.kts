plugins {
  kotlin("jvm") version "1.8.0"
  application
}

repositories {
  mavenCentral()
}

tasks {
  sourceSets.main {
    java.srcDirs("src")
  }

  wrapper {
    gradleVersion = "7.6"
  }
}

kotlin {
  jvmToolchain(11)
}

application {
  mainClass.set("Day23Kt")
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
}
