plugins {
    id("java")
    id("application")
}

repositories {
    mavenCentral() // 외부 라이브러리 다운로드를 위한 중앙 저장소
}

dependencies {
    // JAR 파일을 lib 디렉토리에서 불러옴
    implementation(fileTree(mapOf("dir" to "../lib", "include" to "*.jar")))
    testImplementation(fileTree(mapOf("dir" to "../lib", "include" to "*.jar")))
}

application {
    // 메인 클래스를 설정합니다.
    mainClass.set("engine.Core") // 메인 클래스의 경로
}

sourceSets {
    main {
        java {
            // 애플리케이션 소스 경로를 src로 설정
            setSrcDirs(listOf("../src"))
        }
        resources {
            // 리소스 경로를 설정
            setSrcDirs(listOf("../res"))
        }
    }
    test {
        java {
            // 테스트 소스 경로를 설정
            setSrcDirs(listOf("../src/Test"))
        }
        resources {
            // 테스트 리소스 경로를 설정
            setSrcDirs(listOf("../res"))
        }
    }
}
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
tasks.test {
    useJUnitPlatform() // JUnit 5 플랫폼 사용
    testLogging {
        events("passed", "skipped", "failed") // 테스트 상태 출력
        exceptionFormat = TestExceptionFormat.FULL // 예외의 전체 스택 트레이스 출력
        showStandardStreams = true // 표준 출력 및 표준 에러 출력
    }
}
