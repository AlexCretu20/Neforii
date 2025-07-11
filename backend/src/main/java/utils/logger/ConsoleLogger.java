package utils.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleLogger implements ILogger {

    public void log(LoggerType type, String message){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = LocalDateTime.now().format(formatter);
        System.out.println("[" + type +  "]" + "[" + currentTime + "]" + ": " + message);
    }

}
