import org.jetbrains.dokka.gradle.DokkaTask
import java.util.*

plugins {
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
}

// Stub secrets to let the project sync and build without the publication values set up
extra.apply {
    set("signing.keyId", null)
    set("signing.password", null)
    set("signing.secretKeyRingFile", null)
    set("ossrhUsername", null)
    set("ossrhPassword", null)
}

// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        extra[name.toString()] = value
    }
} else {
    extra.apply {
        set("signing.keyId", System.getenv("SIGNING_KEY_ID"))
        set("signing.password", System.getenv("SIGNING_PASSWORD"))
        set("signing.secretKeyRingFile", System.getenv("SIGNING_SECRET_KEY_RING_FILE"))
        set("ossrhUsername", System.getenv("OSSRH_USERNAME"))
        set("ossrhPassword", System.getenv("OSSRH_PASSWORD"))
    }
}


val dokkaOutputDir = "$buildDir/dokka"

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.named<DokkaTask>("dokkaHtml"))
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}

fun getExtraString(name: String) = extra[name]?.toString()

configure<PublishingExtension> {
    publications {
        withType<MavenPublication> {
            artifact(javadocJar)
            pom {
                name.set("yandexotp")
                description.set("Just a Kotlin library that validates secrets and generates one-time passwords for Yandex 2FA.")
                url.set("https://github.com/RandomRoot/yandexotp")
                licenses {
                    license {
                        name.set("GNU General Public License v3.0")
                        url.set("https://opensource.org/licenses/GPL-3.0")
                    }
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/RandomRoot/yandexotp/issues")
                }
                scm {
                    connection.set("https://github.com/RandomRoot/yandexotp.git")
                    url.set("https://github.com/RandomRoot/yandexotp")
                }
                developers {
                    developer {
                        id.set("RandomRoot")
                        name.set("Mikhail Prokofev")
                        email.set("mprokofev@divcu.com")
                    }
                }
            }
        }
    }
}

// Signing artifacts. Signing.* extra properties values will be used
configure<SigningExtension> {
    sign(project.the<PublishingExtension>().publications)
}