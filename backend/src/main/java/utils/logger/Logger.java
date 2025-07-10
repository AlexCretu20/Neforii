package utils.logger;

public class Logger {

    private static final LoggerManager loggerManager = LoggerManager.getInstance();

    static void log(LoggerTypes type, String message){
        System.out.println("Sunt in logger class");
        loggerManager.logAll(type, message);

    }

//    static void debug(LoggerTypes type,  String message){
//        loggerManager.logAll(type, message);
//
//    }
//
//    static void info(LoggerTypes type,  String message){
//        loggerManager.logAll(type, message);
//
//    }
//
//    static void error(LoggerTypes type,  String message){
//        loggerManager.logAll(type, message);
//
//    }
//
//    static void warning(LoggerTypes type,  String message){
//        loggerManager.logAll(type, message);
//
//    }
//
//    static void fatal(LoggerTypes type,  String message){
//        loggerManager.logAll(type, message);
//
//    }

}
