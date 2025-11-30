package com.finalworks.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import com.finalworks.model.FatalLog;
import com.finalworks.repository.FatalLogRepository;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

public class DatabaseAppender extends AppenderBase<ILoggingEvent> {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    @Override
    protected void append(ILoggingEvent event) {
        // Only log FATAL level events
        if (event.getLevel().levelInt >= ch.qos.logback.classic.Level.FATAL.levelInt) {
            try {
                if (applicationContext != null) {
                    FatalLogRepository fatalLogRepository = applicationContext.getBean(FatalLogRepository.class);
                    
                    if (fatalLogRepository != null) {
                        FatalLog fatalLog = new FatalLog();
                        fatalLog.setMessage(event.getFormattedMessage());
                        fatalLog.setLoggerName(event.getLoggerName());
                        fatalLog.setThreadName(event.getThreadName());
                        fatalLog.setLevel(event.getLevel().toString());
                        fatalLog.setTimestamp(java.time.LocalDateTime.now());

                        // Extract stack trace if exception exists
                        if (event.getThrowableProxy() != null) {
                            ThrowableProxy throwableProxy = (ThrowableProxy) event.getThrowableProxy();
                            StringBuilder stackTrace = new StringBuilder();
                            stackTrace.append(throwableProxy.getClassName())
                                    .append(": ")
                                    .append(throwableProxy.getMessage())
                                    .append("\n");
                            
                            if (throwableProxy.getStackTraceElementProxyArray() != null) {
                                Arrays.stream(throwableProxy.getStackTraceElementProxyArray())
                                        .limit(50) // Limit stack trace to 50 lines
                                        .forEach(element -> {
                                            stackTrace.append("\tat ")
                                                    .append(element.getStackTraceElement())
                                                    .append("\n");
                                        });
                            }
                            fatalLog.setStackTrace(stackTrace.toString());
                        }

                        // Extract class and method name from caller data
                        if (event.getCallerData() != null && event.getCallerData().length > 0) {
                            StackTraceElement caller = event.getCallerData()[0];
                            fatalLog.setClassName(caller.getClassName());
                            fatalLog.setMethodName(caller.getMethodName());
                        }

                        fatalLogRepository.save(fatalLog);
                    }
                }
            } catch (Exception e) {
                // Fallback to console if database logging fails
                System.err.println("Failed to write fatal log to database: " + e.getMessage());
                System.err.println("Original fatal log: " + event.getFormattedMessage());
                e.printStackTrace();
            }
        }
    }
}

