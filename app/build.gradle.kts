plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")

}

android {
    namespace = "com.example.freshauctionapp"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Java 17 권장
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Material 디자인 라이브러리 (이게 있어야 Material3 테마를 인식합니다)
    implementation("com.google.android.material:material:1.11.0")

    // 핵심 라이브러리 최신화
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-ktx:1.8.2") // enableEdgeToEdge 지원

    // Room (최신 안정버전)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // 네트워크 (누락되었던 부분)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Paging 라이브러리 (DataSource를 인식하게 해줍니다)
    val paging_version = "3.3.0" // 최신 안정 버전
    implementation("androidx.paging:paging-runtime:$paging_version")

    // 만약 DataSource.Factory(Paging 2 방식)를 계속 쓰려면 아래 라이브러리도 필요할 수 있습니다
    implementation("androidx.paging:paging-common:$paging_version")

    // Room과 Paging을 함께 쓰기 위한 라이브러리
    implementation("androidx.room:room-paging:2.6.1")// Navigation
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // Firebase (로그인 기능을 쓰신다면)
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
}