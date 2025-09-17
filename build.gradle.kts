import org.gradle.api.GradleException
import org.gradle.api.tasks.Delete

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

val sonatypeUsername = providers.gradleProperty("sonatypeUsername")
    .orElse(providers.environmentVariable("SONATYPE_USERNAME"))
    .orNull
val sonatypePassword = providers.gradleProperty("sonatypePassword")
    .orElse(providers.environmentVariable("SONATYPE_PASSWORD"))
    .orNull
val sonatypeStagingProfileId = providers.gradleProperty("sonatypeStagingProfileId")
    .orElse(providers.environmentVariable("SONATYPE_STAGING_PROFILE_ID"))
    .orNull
val sonatypePackageGroup = providers.gradleProperty("sonatypePackageGroup")
    .orElse(providers.environmentVariable("SONATYPE_PACKAGE_GROUP"))
    .orElse("io.github.hassanjamil")
    .get()

val missingSonatypeSecrets = buildList {
    if (sonatypeUsername.isNullOrBlank()) add("sonatypeUsername (or SONATYPE_USERNAME)")
    if (sonatypePassword.isNullOrBlank()) add("sonatypePassword (or SONATYPE_PASSWORD)")
    if (sonatypeStagingProfileId.isNullOrBlank()) add("sonatypeStagingProfileId (or SONATYPE_STAGING_PROFILE_ID)")
}

nexusPublishing {
    packageGroup.set(sonatypePackageGroup)
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            if (!sonatypeUsername.isNullOrBlank()) {
                username.set(sonatypeUsername)
            }
            if (!sonatypePassword.isNullOrBlank()) {
                password.set(sonatypePassword)
            }
            if (!sonatypeStagingProfileId.isNullOrBlank()) {
                stagingProfileId.set(sonatypeStagingProfileId)
            }
        }
    }
}

tasks.matching { it.name in setOf("publishToSonatype", "initializeSonatypeStagingRepository") }
    .configureEach {
        doFirst {
            if (missingSonatypeSecrets.isNotEmpty()) {
                throw GradleException(
                    "Missing Sonatype credentials: ${missingSonatypeSecrets.joinToString(", ")}.\n" +
                        "Add them to ~/.gradle/gradle.properties or export the matching environment variables before publishing."
                )
            }
        }
    }

tasks.named("closeAndReleaseSonatypeStagingRepository") {
    mustRunAfter("publishToSonatype")
}

tasks.register("publishToMavenCentral") {
    group = "publishing"
    description = "Publishes the release AAR to Sonatype and auto-releases it to Maven Central."
    dependsOn("clean", ":qibla:publishToSonatype", "closeAndReleaseSonatypeStagingRepository")
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
