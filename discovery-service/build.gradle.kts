dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
}

val springCloudVersion = "2024.0.0"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
    }
}

docker {
    // 이미지 이름 지정
    val rootProjectName = rootProject.name.lowercase()
    name = "$rootProjectName-${project.name}:${version}"

    // Dockerfile 경로 지정
//    files("../Dockerfile")

    // 복사할 파일 설정 (bootJar 결과물)
    val bootJar = tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar").get()
    files(bootJar.archiveFile.get().asFile)

    // Dockerfile에 전달할 빌드 인자 설정
    buildArgs(mapOf("JAR_FILE" to bootJar.archiveFileName.get()))
}
