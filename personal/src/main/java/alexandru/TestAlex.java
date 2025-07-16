package alexandru;

import utils.logger.*;

public class TestAlex {
    public static void main(String[] args) {
        LoggerManager.getInstance().register(new ConsoleLogger(LoggerType.INFO));
        LoggerManager.getInstance().register(new FileLogger(LoggerType.INFO, ".", "info.log" ));
        Logger.log(LoggerType.WARNING, "Build success");
    }
}
