group = Library.groupId
version = Library.version

plugins {
    kotlin("multiplatform") version Versions.kotlin
    id("java-library")
    id("publication-conventions")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    jacoco

}

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    val coverageSourceDirs = arrayOf(
        "src/commonMain",
        "src/jvmMain"
    )

    val classFiles = File("${buildDir}/classes/kotlin/jvm/")
        .walkBottomUp()
        .toSet()

    classDirectories.setFrom(classFiles)
    sourceDirectories.setFrom(files(coverageSourceDirs))

    executionData
        .setFrom(files("${buildDir}/jacoco/jvmTest.exec"))

    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
    }
}

val dokkaOutputDir = "$buildDir/dokka"

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    outputDirectory.set(file(dokkaOutputDir))
    dokkaSourceSets {
        configureEach {
            perPackageOption {
                includeNonPublic.set(false)
                // Do not output deprecated members. Applies globally, can be overridden by packageOptions
                skipDeprecated.set(true)
                // Emit warnings about not documented members. Applies globally, also can be overridden by packageOptions
                reportUndocumented.set(true)
                // Do not create index pages for empty packages
                skipEmptyPackages.set(true)
                // Used for linking to JDK documentation
                jdkVersion.set(8)
            }
        }
        register("JVM") { // Different name, so source roots must be passed explicitly
            displayName.set("JVM")
            platform.set(org.jetbrains.dokka.Platform.jvm)
            sourceRoots.from(kotlin.sourceSets.getByName("jvmMain").kotlin.srcDirs)
            sourceRoots.from(kotlin.sourceSets.getByName("commonMain").kotlin.srcDirs)
        }
    }
}

nexusPublishing {
    repositories {
        create("Sonatype") {
            stagingProfileId.set(extra["stagingProfileId"]?.toString())
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"))
            snapshotRepositoryUrl.set(
                uri(
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                )
            )
            username.set(extra["ossrhUsername"]?.toString())
            password.set(extra["ossrhPassword"]?.toString())
        }
    }
}
