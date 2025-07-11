package utils.logger;

public class Logger {

    private static final LoggerManager loggerManager = LoggerManager.getInstance();

    // putem adauga lazy loading (sa bage in loggermanager consolelogger daca nu s-a dat register
    // la nimic, asta cand dau logAll cred o fac), functiile debug/info.., poate o functie sa afiseze doar ERROR sau ce e important
    // si terminat fileloggerul
    public static void log(LoggerType type, String message){
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
