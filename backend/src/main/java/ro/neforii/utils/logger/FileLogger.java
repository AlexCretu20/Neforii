package ro.neforii.utils.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger extends AbstractLogger {
    private final String filePath;
    private final String fileName;

    public FileLogger(LoggerType logLevel, String filePath, String fileName) {
        super(logLevel);
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public void log(LoggerType type, String message) {
        // modific aici si path-uriile cand bagam spring boot ca sa vedem structura exacta
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = LocalDateTime.now().format(formatter);
        String logMessage = "[" + type + "][" + currentTime + "]: " + message;

        String cwd = System.getProperty("user.dir");
        String filePath = cwd + File.separator + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
