package ro.neforii.utils.logger;

public enum LoggerType {
    DEBUG(1),
    INFO(2),
    ERROR(3),
    WARNING(4),
    FATAL(5);

    private final int priority;

    LoggerType(int priority) {
        this.priority = priority;
    }
    public int getPriority() {
        return priority;
    }
}
