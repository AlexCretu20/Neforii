package utils.logger;

import java.time.LocalDateTime;

public class FileLogger {
    public void log(LoggerType types, String message){
        LocalDateTime currentTime = LocalDateTime.now();
//        System.out.println("Logging info " + types + " with message " + message + " at " + currentTime);
        // inca nu e implementat, nu sunt sigur unde sa pun fisierul de loguri

    }
}
