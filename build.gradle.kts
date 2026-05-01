plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.11.0"
}

group = "com.arkannsk"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // Используем GoLand 2026.1
        goland("2026.1")
        bundledPlugin("org.jetbrains.plugins.go")
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

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    patchPluginXml {
        sinceBuild.set("261")
        untilBuild.set("261.*")
    }
}
