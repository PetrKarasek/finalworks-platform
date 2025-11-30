package com.finalworks.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import com.finalworks.model.ErrorLog;
import com.finalworks.repository.ErrorLogRepository;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;

public class DatabaseAppender extends AppenderBase<ILoggingEvent> {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    @Override
    protected void append(ILoggingEvent event) {

        // Logovat pouze události úrovně ERROR
        if (event.getLevel().levelInt < Level.ERROR.levelInt) {
            return;
        }

        try {
            if (applicationContext == null) {
                return;
            }

            ErrorLogRepository errorLogRepository =
                    applicationContext.getBean(ErrorLogRepository.class);

            ErrorLog errorLog = new ErrorLog();

            errorLog.setMessage(event.getFormattedMessage());
            errorLog.setLoggerName(event.getLoggerName());
            errorLog.setThreadName(event.getThreadName());
            errorLog.setLevel(event.getLevel().toString());
            errorLog.setTimestamp(LocalDateTime.now());

            // Stack trace
            if (event.getThrowableProxy() != null) {
                ThrowableProxy throwableProxy =
                        (ThrowableProxy) event.getThrowableProxy();

                StringBuilder stackTrace = new StringBuilder();

                stackTrace.append(throwableProxy.getClassName())
                        .append(": ")
                        .append(throwableProxy.getMessage())
                        .append("\n");

                if (throwableProxy.getStackTraceElementProxyArray() != null) {
                    Arrays.stream(throwableProxy.getStackTraceElementProxyArray())
                            .limit(50)
                            .forEach(element ->
                                    stackTrace.append("\tat ")
                                            .append(element.getStackTraceElement())
                                            .append("\n")
                            );
                }

                errorLog.setStackTrace(stackTrace.toString());
            }

            // Název třídy a metody
            if (event.getCallerData() != null && event.getCallerData().length > 0) {
                StackTraceElement caller = event.getCallerData()[0];
                errorLog.setClassName(caller.getClassName());
                errorLog.setMethodName(caller.getMethodName());
            }

            errorLogRepository.save(errorLog);

        } catch (Exception e) {
            System.err.println("Failed to write error log to database: " + e.getMessage());
            System.err.println("Original error log: " + event.getFormattedMessage());
            e.printStackTrace();
        }
    }
}
