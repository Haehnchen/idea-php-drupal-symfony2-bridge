import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.4.0"
    id("org.jetbrains.intellij.platform") version "2.16.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        val version = providers.gradleProperty("platformVersion")
        val type = providers.gradleProperty("platformType")

        create(type, version) {
            useInstaller.set(true)
            useCache.set(true)
        }

        bundledPlugins(
            "com.intellij.java",
            "com.intellij.modules.json",
            "com.intellij.css",
            "com.jetbrains.plugins.webDeployment",
            "org.jetbrains.plugins.yaml",
            "JavaScript",
            "com.intellij.microservices.ui",
            "com.intellij.database",
            "org.jetbrains.plugins.terminal",
        )

        compatiblePlugins(
            "com.jetbrains.php",
            "com.jetbrains.twig",
            "com.jetbrains.php.dql",
            "fr.adrienbrault.idea.symfony2plugin",
            "de.espend.idea.php.annotation",
        )

        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Plugin.Java)
    }

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {
    test {
        resources {
            srcDir("src/test/java")
            include("**/fixtures/**")
        }
    }
}

intellijPlatform {
    val version = providers.gradleProperty("platformVersion")
    val type = providers.gradleProperty("platformType")

    pluginConfiguration {
        name = properties("pluginName")
    }

    buildSearchableOptions = false

    pluginVerification {
        ides {
            create(type, version) {
                useInstaller.set(true)
                useCache.set(true)
            }
        }
    }
}

tasks {
    properties("javaVersion").let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
        withType<KotlinCompile> {
            compilerOptions {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(it))
            }
        }
    }

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    patchPluginXml {
        version = properties("pluginVersion")
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))
        changeNotes.set(file("src/main/resources/META-INF/change-notes.html").readText().replace("<html>", "").replace("</html>", ""))
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }

    test {
        useJUnitPlatform {
            includeEngines("junit-vintage", "junit-jupiter")
        }

        jvmArgs("-Xshare:off")
        systemProperty("idea.ui.icons.svg.disabled", "true")
        systemProperty("java.awt.headless", "true")
    }
}
