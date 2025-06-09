import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.spring") version "2.1.21"
    kotlin("plugin.jpa") version "2.1.21"
    id("org.flywaydb.flyway") version "10.20.1"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

// Configure both Java and Kotlin to use JDK 21 for compilation
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xjvm-default=all")
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
	
    // Kotlin support
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	
    // Database
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("com.h2database:h2")
	
    // HTMX support
    implementation("org.webjars:webjars-locator-lite")
    implementation("org.webjars.npm:htmx.org:2.0.4")
	
    // CSV support
    implementation("org.apache.commons:commons-csv:1.12.0")
	
    // Micrometer for metrics
    implementation("io.micrometer:micrometer-registry-prometheus")
	
    // Development tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
	
    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:1.20.4")
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xjvm-default=all")
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    // JDK 24 compatibility settings for running tests
    jvmArgs(
        "--enable-native-access=ALL-UNNAMED",
        "--add-opens",
        "java.base/java.lang=ALL-UNNAMED",
        "--add-opens",
        "java.base/java.util=ALL-UNNAMED",
    )
}

// Configure bootRun to use JDK 24 runtime with proper JVM args
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    // Configure to use JDK 24 for runtime
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(24))
            // No vendor specification
        },
    )

    jvmArgs(
        "--enable-native-access=ALL-UNNAMED",
        "--add-opens",
        "java.base/java.lang=ALL-UNNAMED",
        "--add-opens",
        "java.base/java.util=ALL-UNNAMED",
        "-Dspring.profiles.active=dev",
    )
}

flyway {
    driver = "org.postgresql.Driver"
    url = project.findProperty("flyway.url") as String? ?: "jdbc:postgresql://localhost:5432/ai_cs_demo"
    user = project.findProperty("flyway.user") as String? ?: "postgres"
    password = project.findProperty("flyway.password") as String? ?: "password"
    locations = arrayOf("classpath:db/migration")
    baselineOnMigrate = true
    outOfOrder = true
    validateOnMigrate = false // Skip validation temporarily
    cleanDisabled = false
}

// Add a task to completely reset the database
tasks.register("flywayReset") {
    group = "flyway"
    description = "Clean and migrate the database from scratch"
    dependsOn("flywayClean", "flywayMigrate")
    tasks.findByName("flywayMigrate")?.mustRunAfter("flywayClean")
}

ktlint {
    version.set("1.5.0")
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.JSON)
    }
}
