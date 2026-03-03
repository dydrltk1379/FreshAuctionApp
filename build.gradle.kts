// Project-level build.gradle.kts
plugins {
    id("com.android.application") version "8.2.2" apply false // AGP 업데이트
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false // Kotlin 1.9대
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false // Kotlin 버전과 매칭
    id("com.google.gms.google-services") version "4.4.0" apply false
}

// 만약 아래에 clean 태스크가 있다면 이것도 Kotlin 문법으로 바꿔야 합니다
tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}