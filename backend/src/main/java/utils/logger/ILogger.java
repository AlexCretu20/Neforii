package utils.logger;

public interface ILogger {
    default void log(LoggerTypes types, String message){
    }
}
