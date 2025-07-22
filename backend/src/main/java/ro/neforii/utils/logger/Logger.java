package ro.neforii.utils.logger;

public class Logger {
    private static final LoggerManager loggerManager = LoggerManager.getInstance();

    private Logger() {
    }


    public static void log(LoggerType type, String message) {
        loggerManager.logAll(type, message);
    }

}
