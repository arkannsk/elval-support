group = "com.arkannsk"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.3.21"
    id("org.jetbrains.intellij.platform") version "2.11.0"
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    google()

    // Этот вызов добавляет репозитории, необходимые для загрузки IDE (GoLand)
    // Он работает благодаря PREFER_SETTINGS в settings.gradle.kts
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))

    intellijPlatform {
        goland("2026.1")
    }
}

intellijPlatform {
    buildSearchableOptions = false

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "261"
            untilBuild = "261.*"
        }
    }
}


tasks.test {
    useJUnitPlatform() // Required to run JUnit 5 tests
}

tasks {
    withType<JavaCompile> {
        options.release.set(21)
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    patchPluginXml {
        sinceBuild.set("261")
        untilBuild.set("261.*")
    }
}
