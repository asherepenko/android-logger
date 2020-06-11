# Logger

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![](https://jitci.com/gh/asherepenko/android-logger/svg)](https://jitci.com/gh/asherepenko/android-logger)
[![](https://jitpack.io/v/asherepenko/android-logger.svg)](https://jitpack.io/#asherepenko/android-logger) 

A tiny key-value Logger implementation for Android.

## How to

**Step 1.** Add the JitPack repository to your build file

Add it in your root `build.gradle.kts` at the end of repositories:

```kotlin
allprojects {
    repositories {
        maven(url = "https://jitpack.io")
    }
}
```

**Step 2.** Add the dependency

```kotiln
dependencies {
    implementation("com.github.asherepenko:android-logger:x.y.z")
}
```

## Features

- `Logger` defines base context in `key-value` format
- `LogWriter` defines the way where to store logs
- Can be easily used with [Archivarius](https://github.com/asherepenko/android-archivarius)

## Format

Required fields list:
- `message`
- `timestamp` ([RFC 3339](https://tools.ietf.org/html/rfc3339), with fractional seconds, nanoseconds when possible)
- `log_level` (one of `debug`, `info`, `warning`, `error`)
- `application_id`

## Contextual Data

Depending on the context, log records may include the following fields:
- `tag`
- `user_id`
- `app_install_id`
- `device_serial`
- `device_id`
- `job_id` (for background tasks)
- `exception` (this field might contain error stacktrace)

## Usage examples

Create or extend `BaseLogger` instance with `LogWriter` implementation:

```kotlin
// Create logger instance
val logger = BaseLogger(
    BuildConfig.APPLICATION_ID,
    object : LogWriter() {
        override fun write(logLevel: LogLevel, logContext: LogContext) {
            val tag = extendedContext[BaseLoggerParams.TAG]
            val message = extendedContext[BaseLoggerParams.MESSAGE]

            val contextString = extendedContext.entries.joinToString()

            when (logLevel) {
                LogLevel.DEBUG -> Log.d(tag, "$message \n Context: $contextString")
                LogLevel.INFO -> Log.i(tag, "$message \n Context: $contextString")
                LogLevel.WARNING -> Log.w(tag, "$message \n Context: $contextString")
                LogLevel.ERROR -> Log.e(tag, "$message \n Context: $contextString")
            }
        }
        
        fun forceUpload(): Completable = Completable.complete()
    })

// Write logs with Logger
logger.info("This is a test info message")
```
