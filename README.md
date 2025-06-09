# AI CS Demo Application

A modern Spring Boot application showcasing the latest technologies in the Java ecosystem.

## Key Technologies

### 🚀 JDK 24
- Latest JDK with improved performance and features
- Bytecode compiled with JVM 23 target for optimal compatibility
- Running on JDK 24 for best performance

### 🧵 Project Loom Virtual Threads
- "Reactive programming is 'dead'. There is very little use for reactive programming now that Java has virtual threads." - CTO
- Virtual threads enabled via `spring.threads.virtual.enabled=true`
- Massively scalable thread creation without the overhead of platform threads
- No more complex reactive programming models needed

### ⚙️ Modern HTTP Client
- Using RestClient instead of deprecated RestTemplate
- "The new RestClient is what you want to use instead of RestTemplate in most cases." - CTO
- Built on JDK HTTP Client which supports HTTP/2
- Clean, fluent API for making HTTP requests

## Framework Versions

- **Spring Boot**: 3.4.5
- **Kotlin**: 2.1.21
- **Gradle**: 8.12
- **JDK**: 24.0.1

## Running the Application

To ensure proper JDK configuration:

```bash
./run-with-jdk24.sh
```

This script:
1. Uses JDK 21 for Gradle build
2. Uses JDK 24 for application runtime
3. Enables all required JVM flags for optimal performance

## IntelliJ IDEA Configuration

For running in IntelliJ, see [run-in-intellij.md](run-in-intellij.md) to configure Gradle to use JDK 21 while running the application with JDK 24.

## Key Features

- Product management with database storage
- External API integration using RestClient
- Modern UI with HTMX
- System information page showing JDK and runtime details
- Virtual threads for improved scalability

## System Information

Visit http://localhost:8080/system/info after starting the application to see detailed information about:

- Java version
- Virtual thread status
- RestClient implementation
- Framework versions 