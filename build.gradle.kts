group = Library.groupId
version = Library.version

plugins {
    kotlin("multiplatform") version Versions.kotlin
    id("org.jetbrains.kotlinx.kover") version Versions.kover
    id("io.github.gradle-nexus.publish-plugin") version Versions.nexusPublish
    id("java-library")
    id("publication-conventions")
}

repositories {
    mavenCentral()
}

tasks.test {
    extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {
        isDisabled = false
        includes = listOf("${Library.groupId}.${Library.artifact}.*")
        excludes = listOf("${Library.groupId}.${Library.artifact}.test.*")
    }
}

kover {
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.JACOCO)
    jacocoEngineVersion.set("0.8.7")

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
