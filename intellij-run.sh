#!/bin/bash

# This script is used to run the application from IntelliJ IDEA
# It uses JDK 21 for Gradle build and JDK 24 for runtime

echo "=============================================================="
echo "  🚀 IntelliJ: Building with JDK 21, Running with JDK 24  🚀"
echo "=============================================================="
echo ""

# Use JDK 21 for Gradle
export JAVA_HOME="/Users/ujjwalsai/Library/Java/JavaVirtualMachines/jbr-21.0.6/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
export GRADLE_OPTS="-Dorg.gradle.java.home=$JAVA_HOME"

# Show which Java version we're using for build
echo "📦 Using Java for Gradle build:"
java -version
echo ""

# Build the application
echo "🛠️ Building the application..."
./gradlew clean bootJar -x test -x ktlintCheck

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

# Run the application
echo "🚀 Starting application with JDK 24 and virtual threads enabled..."
echo "📊 View system info at http://localhost:8080/system/info"
echo ""

# Run with JDK 24 and Project Loom features
java --enable-native-access=ALL-UNNAMED \
     --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     -Dspring.threads.virtual.enabled=true \
     -Dspring.flyway.baseline-on-migrate=true \
     -Dspring.flyway.out-of-order=true \
     -Dspring.flyway.validate-on-migrate=false \
     -jar build/libs/ai-cs-demo-0.0.1-SNAPSHOT.jar 