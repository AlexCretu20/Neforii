package ro.neforii.utils.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractLogger {
    private LoggerType logLevel;

    public AbstractLogger() {
        this.logLevel = LoggerType.DEBUG;
    }

    public AbstractLogger(LoggerType logLevel) {
        this.logLevel = logLevel;
    }

    public LoggerType getLogLevel() {
        return logLevel;
    }

    public void log(LoggerType type, String message) {
    }

    protected String formatMessage(LoggerType type, String message) {
        DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = LocalDateTime.now().format(formatter);
        return "[" + type + "]" + "[" + currentTime + "]" + ": " + message;
    }

}
