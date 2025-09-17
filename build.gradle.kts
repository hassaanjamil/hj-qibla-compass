import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Delete

plugins {
    id("com.android.application") version "8.5.0" apply false
    id("com.android.library") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.vanniktech.maven.publish") version "0.34.0" apply false
}

fun ProviderFactory.lazyProperty(vararg keys: String) = keys.asSequence()
    .mapNotNull { key -> gradleProperty(key).orElse(environmentVariable(key)).orNull }
    .firstOrNull()

val centralUsername = providers.lazyProperty("mavenCentralUsername", "sonatypeUsername", "MAVEN_CENTRAL_USERNAME", "SONATYPE_USERNAME")
val centralPassword = providers.lazyProperty("mavenCentralPassword", "sonatypePassword", "MAVEN_CENTRAL_PASSWORD", "SONATYPE_PASSWORD")
val centralProfileId = providers.lazyProperty("mavenCentralStagingProfileId", "sonatypeStagingProfileId", "MAVEN_CENTRAL_STAGING_PROFILE_ID", "SONATYPE_STAGING_PROFILE_ID")

centralUsername?.let { extra.set("mavenCentralUsername", it) }
centralPassword?.let { extra.set("mavenCentralPassword", it) }
centralProfileId?.let { extra.set("mavenCentralStagingProfileId", it) }

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
