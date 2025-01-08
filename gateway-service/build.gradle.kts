dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
}

val springCloudVersion = "2024.0.0"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
    }
}

//docker {
//    // 이미지 이름 지정
//    val rootProjectName = rootProject.name.toLowerCase()
//    name = "$rootProjectName-${project.name}:${version}"
//
//    // 어떤 Dockerfile을 사용할지 지정
//    dockerfile = file("../Dockerfile")
//
//    // 복사할 파일 (bootJar 작업의 결과물)
//    files(tasks.named<Jar>("bootJar").get().outputs.files)
//
//    // Dockerfile에 전달할 빌드 인자
//    buildArgs(mapOf("JAR_FILE" to tasks.named<Jar>("bootJar").get().outputs.files.singleFile.name))
//}

docker {
    // 이미지 이름 지정
    val rootProjectName = rootProject.name.lowercase()
    name = "$rootProjectName-${project.name}:${version}"

    // Dockerfile 경로 지정
    files("../Dockerfile")

    // 복사할 파일 설정 (bootJar 결과물)
    val bootJar = tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar").get()
    files(bootJar.archiveFile.get().asFile)

    // Dockerfile에 전달할 빌드 인자 설정
    buildArgs(mapOf("JAR_FILE" to bootJar.archiveFileName.get()))
}
