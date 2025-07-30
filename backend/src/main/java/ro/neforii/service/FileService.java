package ro.neforii.service;


import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    //local pentru test
    private static final String uploadDir = "uploads";
//    //calea spre ec2
//    private static final String uploadDir = "/home/ubuntu/images_uploads";


    public void save(String file, byte[] bytes) throws IOException {
        Path dir = Paths.get(uploadDir);

        if (Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Path filePath = dir.resolve(file);
        Files.write(filePath, bytes);
    }

}
