dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    // spring cloud
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

//    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // common module
    implementation(project(":common-service"))

    // kafka
    implementation("org.springframework.kafka:spring-kafka:3.3.2")

    // database
    implementation("io.asyncer:r2dbc-mysql:1.0.4")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    testRuntimeOnly("io.r2dbc:r2dbc-h2")
    testImplementation("io.r2dbc:r2dbc-h2")

    // test
    testImplementation("io.mockk:mockk:1.13.14")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest:kotest-framework-engine:5.9.1")
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
