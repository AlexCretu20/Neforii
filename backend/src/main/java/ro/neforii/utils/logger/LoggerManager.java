package ro.neforii.utils.logger;

import java.util.ArrayList;
import java.util.List;

public class LoggerManager {
    private static LoggerManager instance;
    List<AbstractLogger> loggers = new ArrayList<>();

    private LoggerManager() {
    }

    public static LoggerManager getInstance() {
        if (instance == null) {
            instance = new LoggerManager();
        }
        return instance;
    }



    public void register(AbstractLogger logger) {
        loggers.add(logger);
    }

    public void remove(AbstractLogger logger) {
        loggers.remove(logger);
    }


    public void logAll(LoggerType loggerType, String message) {
        if (loggers.isEmpty()) {
            loggers.add(new ConsoleLogger()); // un fel de lazy loading, default daca nu e nimic altceva
        }

        for (AbstractLogger logger : loggers) {
            if (loggerType.getPriority() >= logger.getLogLevel().getPriority()) {
                logger.log(loggerType, message);
            }
        }
    }
}
