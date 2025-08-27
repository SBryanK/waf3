# WAF3 - EdgeOne Security Tester

WAF3 is an Android application designed for security testing and analysis of web applications, specifically focusing on Web Application Firewall (WAF) detection, bot protection, HTTP/TLS configuration, and CDN behavior analysis.

## 🛡️ What This App Does

WAF3 provides comprehensive security testing capabilities through an intuitive Android interface:

### Core Features

- **WAF Detection Testing**: Tests for common web application vulnerabilities including:
  - SQL Injection attacks
  - Cross-Site Scripting (XSS) payloads  
  - Local/Remote File Inclusion (LFI/RFI)
  - Command injection attempts
  - Path traversal attacks

- **Bot Detection Analysis**: Evaluates anti-bot measures using:
  - Suspicious user agent strings
  - Crawler pattern detection
  - Headless browser identification

- **HTTP/TLS Security Assessment**: Analyzes:
  - HTTPS redirect enforcement
  - HTTP/2 protocol support
  - TLS version negotiation (1.2/1.3)
  - Cipher suite analysis

- **CDN Behavior Testing**: Examines:
  - Cache bypass mechanisms
  - Edge header analysis
  - Cache purge functionality

### Key Capabilities

- **Real-time Testing**: Execute security tests against target URLs with configurable timeouts and request parameters
- **Comprehensive Reporting**: Detailed test results including status codes, response headers, latency metrics, and TLS information
- **Quota Management**: Built-in rate limiting and usage tracking
- **Test History**: Persistent storage of test results for analysis and comparison
- **Export Functionality**: Export test results in JSON or CSV formats
- **Connection Monitoring**: Real-time connection status and latency monitoring

## 📱 Screenshots & Interface

The app features a modern Material Design 3 interface with:
- Target URL and path configuration
- Test selection with categorized grouping
- Real-time progress tracking
- Detailed results visualization
- Historical test data management

## 🚀 Installation & Setup

### Prerequisites

- Android Studio Arctic Fox (2020.3.1) or newer
- Android SDK with API level 24+ (Android 7.0)
- Kotlin 1.8.0 or newer
- Gradle 8.0+

### Clone and Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/SBryanK/waf3.git
   cd waf3
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned `waf3` directory
   - Click "OK" to open the project

3. **Gradle Sync:**
   - Android Studio will automatically trigger a Gradle sync
   - Wait for all dependencies to download and sync

4. **Configure SDK (if needed):**
   - If prompted, install the required Android SDK versions
   - Set your local SDK path in `local.properties`

### Build and Run

1. **Build the project:**
   ```bash
   ./gradlew build
   ```
   *On Windows:*
   ```cmd
   gradlew.bat build
   ```

2. **Run on device/emulator:**
   - Connect an Android device via USB with USB debugging enabled, OR
   - Start an Android Virtual Device (AVD) from Android Studio
   - Click the "Run" button in Android Studio, or use:
   ```bash
   ./gradlew installDebug
   ```

### Testing

Run the unit tests:
```bash
./gradlew test
```

Run instrumented tests (requires connected device/emulator):
```bash
./gradlew connectedAndroidTest
```

## 🏗️ Project Structure

```
waf3/
├── app/
│   ├── src/main/java/com/example/waf3/
│   │   ├── core/                    # Core business logic
│   │   │   ├── db/                  # Room database entities & DAOs
│   │   │   ├── executor/            # Test execution engine
│   │   │   ├── model/               # Data models and enums
│   │   │   ├── quota/               # Usage quota management
│   │   │   ├── repo/                # Repository pattern implementations
│   │   │   └── template/            # UI template rendering
│   │   ├── ui/                      # User interface components
│   │   │   ├── navigation/          # Navigation graph
│   │   │   ├── screens/             # Compose UI screens
│   │   │   └── theme/               # Material Design theme
│   │   ├── EdgeOneApp.kt            # Application class
│   │   └── MainActivity.kt          # Main activity
│   ├── src/main/assets/             # Static assets (test templates)
│   └── src/main/res/                # Android resources
├── gradle/                          # Gradle wrapper and version catalog
└── build.gradle.kts                 # Project build configuration
```

## 🔧 Dependencies

- **Jetpack Compose**: Modern UI toolkit
- **Room Database**: Local data persistence  
- **OkHttp**: HTTP client for network requests
- **Kotlinx Serialization**: JSON serialization
- **Navigation Compose**: Screen navigation
- **Lifecycle Components**: ViewModel and state management

## ⚠️ Important Security Note

This application is designed for **defensive security testing purposes only**. It should only be used to test web applications that you own or have explicit permission to test. The app includes various attack payloads for WAF detection - ensure you comply with all applicable laws and regulations when using this tool.

## 📄 License

This project is open source. Please ensure you use it responsibly and in accordance with applicable security testing guidelines and legal requirements.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

If you encounter any issues or have questions, please open an issue on the GitHub repository.

---

**Disclaimer**: This tool is for educational and legitimate security testing purposes only. Users are responsible for ensuring they have proper authorization before testing any systems.