import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"

	kotlin("kapt") version "1.6.21"
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

allprojects {
	group = "com.beomsic"
	version = "0.0.1"
	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "kotlin")
	apply(plugin = "kotlin-spring")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "kotlin-kapt")

	dependencies {
		// 로깅 (코틀린 로깅)
		implementation("io.github.microutils:kotlin-logging:1.12.5")

		// kotlin
		implementation("org.springframework.boot:spring-boot-starter")
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
		implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
		implementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable")

		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	}

	dependencyManagement {
		imports {
			mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
		}
	}

	kotlin {
		compilerOptions {
			freeCompilerArgs.addAll("-Xjsr305=strict")
			jvmTarget.set(JvmTarget.JVM_17)
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}

//val springCloudVersion = "2024.0.0"
//
//dependencies {
//	implementation("org.springframework.boot:spring-boot-starter")
//	implementation("org.jetbrains.kotlin:kotlin-reflect")
//	testImplementation("org.springframework.boot:spring-boot-starter-test")
//	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
//	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//}
//
//
//dependencyManagement {
//	imports {
//		mavenBom ("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
//	}
//}
//
//kotlin {
//	compilerOptions {
//		freeCompilerArgs.addAll("-Xjsr305=strict")
//	}
//}
//
//tasks.withType<Test> {
//	useJUnitPlatform()
//}
