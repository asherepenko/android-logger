package com.sherepenko.android.logger

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.RuntimeException

@RunWith(AndroidJUnit4::class)
class BaseLoggerTest {

    private lateinit var logWriter: LogWriter

    private lateinit var logger: BaseLogger

    @Before
    fun setUp() {
        logWriter = mock()
        logger = BaseLogger(
            logWriter,
            mapOf(
                BaseLoggerParams.APPLICATION_ID to BuildConfig.LIBRARY_PACKAGE_NAME,
                BaseLoggerParams.TAG to "test"
            )
        )
    }

    @Test
    fun shouldWriteLogsWithLogWriter() {
        // DEBUG
        logger.debug("This is debug message")

        argumentCaptor<LogContext> {
            verify(logWriter).write(eq(LogLevel.DEBUG), capture())

            assertThat(firstValue[BaseLoggerParams.APPLICATION_ID])
                .isEqualTo(BuildConfig.LIBRARY_PACKAGE_NAME)
            assertThat(firstValue[BaseLoggerParams.TAG])
                .isEqualTo("test")
            assertThat(firstValue[BaseLoggerParams.MESSAGE])
                .isEqualTo("This is debug message")
            assertThat(firstValue[BaseLoggerParams.TIMESTAMP])
                .isNotEmpty()
        }

        // INFO
        logger.info("This is info message")

        argumentCaptor<LogContext> {
            verify(logWriter).write(eq(LogLevel.INFO), capture())

            assertThat(firstValue[BaseLoggerParams.APPLICATION_ID])
                .isEqualTo(BuildConfig.LIBRARY_PACKAGE_NAME)
            assertThat(firstValue[BaseLoggerParams.TAG])
                .isEqualTo("test")
            assertThat(firstValue[BaseLoggerParams.MESSAGE])
                .isEqualTo("This is info message")
            assertThat(firstValue[BaseLoggerParams.TIMESTAMP])
                .isNotEmpty()
        }

        // WARNING
        logger.warning("This is warning message")

        argumentCaptor<LogContext> {
            verify(logWriter).write(eq(LogLevel.WARNING), capture())

            assertThat(firstValue[BaseLoggerParams.APPLICATION_ID])
                .isEqualTo(BuildConfig.LIBRARY_PACKAGE_NAME)
            assertThat(firstValue[BaseLoggerParams.TAG])
                .isEqualTo("test")
            assertThat(firstValue[BaseLoggerParams.MESSAGE])
                .isEqualTo("This is warning message")
            assertThat(firstValue[BaseLoggerParams.TIMESTAMP])
                .isNotEmpty()
        }

        // ERROR
        logger.error("This is warning message", RuntimeException("Test runtime error"))

        argumentCaptor<LogContext> {
            verify(logWriter).write(eq(LogLevel.ERROR), capture())

            assertThat(firstValue[BaseLoggerParams.APPLICATION_ID])
                .isEqualTo(BuildConfig.LIBRARY_PACKAGE_NAME)
            assertThat(firstValue[BaseLoggerParams.TAG])
                .isEqualTo("test")
            assertThat(firstValue[BaseLoggerParams.MESSAGE])
                .isEqualTo("This is warning message")
            assertThat(firstValue[BaseLoggerParams.TIMESTAMP])
                .isNotEmpty()
            assertThat(firstValue[BaseLoggerParams.EXCEPTION])
                .contains("Test runtime error")
        }
    }

    @Test
    fun shouldExtendLogContext() {
        logger.info("This is info message #1")

        logger
            .withUserId("test_user_id")
            .info("This is info message #2")

        argumentCaptor<LogContext> {
            verify(logWriter, times(2)).write(eq(LogLevel.INFO), capture())

            assertThat(firstValue[BaseLoggerParams.APPLICATION_ID])
                .isEqualTo(BuildConfig.LIBRARY_PACKAGE_NAME)
            assertThat(firstValue[BaseLoggerParams.TAG])
                .isEqualTo("test")
            assertThat(firstValue[BaseLoggerParams.MESSAGE])
                .isEqualTo("This is info message #1")
            assertThat(firstValue[BaseLoggerParams.TIMESTAMP])
                .isNotEmpty()
            assertThat(firstValue[BaseLoggerParams.USER_ID])
                .isNull()

            assertThat(secondValue[BaseLoggerParams.APPLICATION_ID])
                .isEqualTo(BuildConfig.LIBRARY_PACKAGE_NAME)
            assertThat(secondValue[BaseLoggerParams.TAG])
                .isEqualTo("test")
            assertThat(secondValue[BaseLoggerParams.MESSAGE])
                .isEqualTo("This is info message #2")
            assertThat(firstValue[BaseLoggerParams.TIMESTAMP])
                .isNotEmpty()
            assertThat(secondValue[BaseLoggerParams.USER_ID])
                .isEqualTo("test_user_id")
        }
    }

    @Test
    fun shouldScheduleLogUpload() {
        logger.forceLogUpload()

        verify(logWriter).forceUpload()
    }
}
