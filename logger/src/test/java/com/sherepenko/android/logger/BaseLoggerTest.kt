package com.sherepenko.android.logger

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.lang.RuntimeException
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaseLoggerTest {

    private lateinit var logWriter: LogWriter

    private lateinit var logger: BaseLogger

    @Before
    fun setUp() {
        logWriter = mockk(relaxed = true)
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

        val logContextSlot = slot<LogContext>()

        verify { logWriter.write(LogLevel.DEBUG, capture(logContextSlot)) }

        logContextSlot.captured.apply {
            assertThat(this[BaseLoggerParams.APPLICATION_ID])
                .isEqualTo(BuildConfig.LIBRARY_PACKAGE_NAME)
            assertThat(this[BaseLoggerParams.TAG]).isEqualTo("test")
            assertThat(this[BaseLoggerParams.MESSAGE]).isEqualTo("This is debug message")
            assertThat(this[BaseLoggerParams.TIMESTAMP]).isNotEmpty()
        }

        // INFO
        logger.info("This is info message")

        verify { logWriter.write(LogLevel.INFO, capture(logContextSlot)) }

        logContextSlot.captured.apply {
            assertThat(this[BaseLoggerParams.APPLICATION_ID])
                .isEqualTo(BuildConfig.LIBRARY_PACKAGE_NAME)
            assertThat(this[BaseLoggerParams.TAG]).isEqualTo("test")
            assertThat(this[BaseLoggerParams.MESSAGE]).isEqualTo("This is info message")
            assertThat(this[BaseLoggerParams.TIMESTAMP]).isNotEmpty()
        }

        // WARNING
        logger.warning("This is warning message")

        verify { logWriter.write(LogLevel.WARNING, capture(logContextSlot)) }

        logContextSlot.captured.apply {
            assertThat(this[BaseLoggerParams.APPLICATION_ID])
                .isEqualTo(BuildConfig.LIBRARY_PACKAGE_NAME)
            assertThat(this[BaseLoggerParams.TAG]).isEqualTo("test")
            assertThat(this[BaseLoggerParams.MESSAGE]).isEqualTo("This is warning message")
            assertThat(this[BaseLoggerParams.TIMESTAMP]).isNotEmpty()
        }

        // ERROR
        logger.error("This is warning message", RuntimeException("Test runtime error"))

        verify { logWriter.write(LogLevel.ERROR, capture(logContextSlot)) }

        logContextSlot.captured.apply {
            assertThat(this[BaseLoggerParams.APPLICATION_ID])
                .isEqualTo(BuildConfig.LIBRARY_PACKAGE_NAME)
            assertThat(this[BaseLoggerParams.TAG]).isEqualTo("test")
            assertThat(this[BaseLoggerParams.MESSAGE]).isEqualTo("This is warning message")
            assertThat(this[BaseLoggerParams.TIMESTAMP]).isNotEmpty()
            assertThat(this[BaseLoggerParams.EXCEPTION]).contains("Test runtime error")
        }
    }

    @Test
    fun shouldExtendLogContext() {
        logger.info("This is info message #1")

        logger
            .withUserId("test_user_id")
            .info("This is info message #2")

        val logContextSlot = mutableListOf<LogContext>()

        verify(exactly = 2) { logWriter.write(LogLevel.INFO, capture(logContextSlot)) }

        logContextSlot.apply {
            assertThat(this[0][BaseLoggerParams.APPLICATION_ID])
                .isEqualTo(BuildConfig.LIBRARY_PACKAGE_NAME)
            assertThat(this[0][BaseLoggerParams.TAG]).isEqualTo("test")
            assertThat(this[0][BaseLoggerParams.MESSAGE]).isEqualTo("This is info message #1")
            assertThat(this[0][BaseLoggerParams.TIMESTAMP]).isNotEmpty()
            assertThat(this[0][BaseLoggerParams.USER_ID]).isNull()

            assertThat(this[1][BaseLoggerParams.APPLICATION_ID])
                .isEqualTo(BuildConfig.LIBRARY_PACKAGE_NAME)
            assertThat(this[1][BaseLoggerParams.TAG]).isEqualTo("test")
            assertThat(this[1][BaseLoggerParams.MESSAGE]).isEqualTo("This is info message #2")
            assertThat(this[1][BaseLoggerParams.TIMESTAMP]).isNotEmpty()
            assertThat(this[1][BaseLoggerParams.USER_ID]).isEqualTo("test_user_id")
        }
    }

    @Test
    fun shouldScheduleLogUpload() {
        logger.forceLogUpload()

        verify { logWriter.forceUpload() }
    }
}
