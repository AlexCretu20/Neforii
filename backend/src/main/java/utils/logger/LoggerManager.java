package utils.logger;

import java.util.ArrayList;
import java.util.List;

public class LoggerManager {
    static LoggerManager instance;
    List<ILogger> loggers = new ArrayList<>();

    private LoggerManager() {
        loggers.add(new ConsoleLogger()); // default logger
    }

    public static LoggerManager getInstance() {
        if (instance == null) {
            instance = new LoggerManager();
        }
        return instance;
    }

    public void register(ILogger logger) {
        loggers.add(logger);
    }

    public void remove(ILogger logger) {
        loggers.remove(logger);
    }


    public void logAll(LoggerType type, String message) {

        for (ILogger logger : loggers) {
            logger.log(type, message);
        }
    }
}
