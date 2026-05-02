plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("org.jetbrains.intellij.platform") version "2.11.0"
}

group = "com.arkannsk"
version = "1.0.0"

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

    intellijPlatform {
        goland("2026.1")
    }
}

intellijPlatform {
    buildSearchableOptions = false

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "243"
            untilBuild = "251.*"
        }
    }
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