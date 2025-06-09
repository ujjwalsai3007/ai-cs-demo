# Running the AI CS Demo in IntelliJ IDEA

## Fix for "Unsupported class file major version 68" Error

This error occurs because Gradle needs Java 21 to run its build scripts, but our project is configured to use Java 24 for runtime.

## Solution 1: Configure IntelliJ Gradle Settings

1. Open IntelliJ IDEA preferences (⌘+,)
2. Navigate to **Build, Execution, Deployment → Build Tools → Gradle**
3. Set **Gradle JVM** to **Java 21** (not Java 24)
4. Apply and close settings

![IntelliJ Gradle Settings](https://i.imgur.com/JL2XywK.png)

## Solution 2: Create a Custom Run Configuration (Application)

1. Click on the dropdown in the top-right corner (near the run button)
2. Select "Edit Configurations..."
3. Click the "+" button and select "Application"
4. Configure as follows:
   - Name: `AI CS Demo (JDK 24)`
   - Module: `ai-cs-demo.main`
   - JRE: Select Java 24
   - Main class: `org.example.aicsdemo.AiCsDemoApplication`
   - VM options: 
     ```
     --enable-native-access=ALL-UNNAMED
     --add-opens java.base/java.lang=ALL-UNNAMED
     --add-opens java.base/java.util=ALL-UNNAMED
     -Dspring.threads.virtual.enabled=true
     ```
   - Working directory: `$PROJECT_DIR$`

5. Apply and close
6. Now you can run this configuration directly

## Solution 3: Create a Shell Script Run Configuration (RECOMMENDED)

We've created a special script `intellij-run.sh` that handles both the build with JDK 21 and runs with JDK 24:

1. Click on the dropdown in the top-right corner (near the run button)
2. Select "Edit Configurations..."
3. Click the "+" button and select "Shell Script"
4. Configure as follows:
   - Name: `Run with JDK 24 (Script)`
   - Script path: `$PROJECT_DIR$/intellij-run.sh`
   - Working directory: `$PROJECT_DIR$`

5. Apply and close
6. Now you can run this configuration directly

This approach is the simplest as it automatically:
1. Uses JDK 21 for Gradle build
2. Uses JDK 24 for application runtime
3. Sets all necessary JVM flags

## Solution 4: Use the Terminal

The simplest solution is to use the provided shell scripts from the terminal:

```bash
chmod +x run-with-jdk24.sh
./run-with-jdk24.sh
```

Or:

```bash
chmod +x intellij-run.sh
./intellij-run.sh
```

## Verifying the Setup

1. After starting the application, visit: http://localhost:8080/system/info
2. Confirm that:
   - Java version shows 24
   - Virtual Threads are enabled
   - The application is using RestClient (not RestTemplate) 