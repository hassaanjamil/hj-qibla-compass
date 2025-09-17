import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("signing")
}

fun Project.stringProperty(name: String, fallback: String): String =
    (findProperty(name) as? String)?.takeIf { it.isNotBlank() } ?: fallback

group = stringProperty("POM_GROUP_ID", "com.hassanjamil")
version = stringProperty("qiblaVersion", "1.0.0")

android {
    namespace = "com.hassanjamil.hqibla"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.02.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = project.group.toString()
                artifactId = stringProperty("POM_ARTIFACT_ID", "qibla")
                version = project.version.toString()
                pom {
                    name.set(stringProperty("POM_NAME", "hjQiblaCompass"))
                    description.set(stringProperty("POM_DESCRIPTION", "Jetpack Compose Qibla compass component for Android."))
                    url.set(stringProperty("POM_URL", "https://github.com/hassanjamil/hj-qibla-compass"))
                    licenses {
                        license {
                            name.set(stringProperty("POM_LICENSE_NAME", "Apache License 2.0"))
                            url.set(stringProperty("POM_LICENSE_URL", "https://www.apache.org/licenses/LICENSE-2.0.txt"))
                            distribution.set(stringProperty("POM_LICENSE_DIST", "repo"))
                        }
                    }
                    developers {
                        developer {
                            id.set(stringProperty("POM_DEVELOPER_ID", "hassanjamil"))
                            name.set(stringProperty("POM_DEVELOPER_NAME", "Muhammad Hassan Jamil"))
                            email.set(stringProperty("POM_DEVELOPER_EMAIL", "hassanjamil91@gmail.com"))
                        }
                    }
                    scm {
                        url.set(stringProperty("POM_SCM_URL", "https://github.com/hassanjamil/hj-qibla-compass"))
                        connection.set(stringProperty("POM_SCM_CONNECTION", "scm:git:git://github.com/hassanjamil/hj-qibla-compass.git"))
                        developerConnection.set(stringProperty("POM_SCM_DEV_CONNECTION", "scm:git:ssh://github.com:hassanjamil/hj-qibla-compass.git"))
                    }
                }
            }
        }
    }

    signing {
        val signingKey = findProperty("signingKey") as String?
        val signingPassword = findProperty("signingPassword") as String?
        if (!signingKey.isNullOrBlank()) {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publishing.publications)
        }
    }
}
