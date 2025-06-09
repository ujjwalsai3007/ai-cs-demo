#!/bin/bash

# Set JDK 21 for build
export JAVA_HOME="/Users/ujjwalsai/Library/Java/JavaVirtualMachines/jbr-21.0.6/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

# Set environment variable for Gradle to use JDK 21
export GRADLE_OPTS="-Dorg.gradle.java.home=$JAVA_HOME"

echo "=============================================================="
echo "  🚀 Building with JDK 21, Running with JDK 24  🚀"
echo "=============================================================="
echo ""

# Show which Java version we're using for build
echo "📦 Using Java for build:"
java -version
echo ""

# Stop any existing Gradle daemons
./gradlew --stop

# Remind about PostgreSQL
echo "🗄️  Make sure PostgreSQL is running with database 'ai_cs_demo' created"
echo "You can start PostgreSQL with: brew services start postgresql"
echo "Press ENTER to continue or CTRL+C to abort"
read

# Try Flyway reset task which does clean + migrate
echo "🔄 Resetting the database schema..."
./gradlew flywayReset

# Clean and build
echo "🛠️  Building the application with latest Spring Boot and Kotlin..."
./gradlew clean bootJar -x test -x ktlintCheck -x ktlintKotlinScriptCheck -x ktlintMainSourceSetCheck -x ktlintTestSourceSetCheck

# Now switch to JDK 24 for runtime
export JAVA_HOME="/Users/ujjwalsai/Library/Java/JavaVirtualMachines/openjdk-24.0.1/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

# Show which Java version we're using for runtime
echo ""
echo "=============================================================="
echo "🧵 Using Java for runtime with Project Loom Virtual Threads 🧵"
echo "=============================================================="
java -version
echo ""

# Run the application directly with java command
echo "🚀 Starting application with JDK 24 and virtual threads enabled..."
echo "⚙️  Using modern RestClient instead of deprecated RestTemplate"
echo "📊 View system info at http://localhost:8080/system/info"
echo ""

# Run with highlighted JDK 24 and Project Loom features
java --enable-native-access=ALL-UNNAMED \
     --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     -Dspring.threads.virtual.enabled=true \
     -Dspring.flyway.baseline-on-migrate=true \
     -Dspring.flyway.out-of-order=true \
     -Dspring.flyway.validate-on-migrate=false \
     -jar build/libs/ai-cs-demo-0.0.1-SNAPSHOT.jar 