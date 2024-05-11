plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "kr.ac.yuhan.cs.qradmin"
    compileSdk = 34

    defaultConfig {
        applicationId = "kr.ac.yuhan.cs.qradmin"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // 뉴모피즘 추가
    implementation("com.github.fornewid:neumorphism:0.3.2")

    // flex
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    // Glide 라이브러리 추가
    implementation("com.github.bumptech.glide:glide:4.13.2");
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2");
}