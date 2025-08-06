package ro.neforii.utils.logger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class LoggerManager {
    private static LoggerManager instance;
    private final List<AbstractLogger> loggers = new CopyOnWriteArrayList<>();
    private final BlockingQueue<LogEntry> logQueue = new LinkedBlockingQueue<>();

    private static class LogEntry {
        private final LoggerType type;
        private final String message;
        LogEntry(LoggerType type, String message) {
            this.type = type;
            this.message = message;
        }
    }

    private LoggerManager() {
        startWorkerThread();
    }

    public static synchronized LoggerManager getInstance() {
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

    private void startWorkerThread() {
        Thread logThread = new Thread(() -> {
            try {
                while (true) {
                    LogEntry entry = logQueue.take();
                    if (loggers.isEmpty()) {
                        loggers.add(new ConsoleLogger());
                    }
                    for (AbstractLogger logger : loggers) {
                        if (entry.type.getPriority() >= logger.getLogLevel().getPriority()) {
                            logger.log(entry.type, entry.message);
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        logThread.setDaemon(true);
        logThread.setName("LoggerThread");
        logThread.start();
    }

    public void logAll(LoggerType loggerType, String message) {
        if (message == null) return;
        try {
            logQueue.put(new LogEntry(loggerType, message));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Logging thread interrupted while queueing message: " + e.getMessage());
        }
    }
}
