package utils.logger;

import java.time.LocalDateTime;

public class ConsoleLogger implements ILogger {

    public void log(LoggerTypes types, String message){
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("Logging info " + types + " with message " + message + " at " + currentTime);

    }

}
