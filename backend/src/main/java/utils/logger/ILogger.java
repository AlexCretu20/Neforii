package utils.logger;

public interface ILogger {
    default void log(LoggerType type, String message){
    }
}
