package ro.neforii.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ro.neforii.config.ConfigProperties;
import ro.neforii.exception.MissingPemKeyException;

import java.io.*;


@Service
public class FileService {

    private final ConfigProperties config;

    public FileService(ConfigProperties config) {
        this.config = config;
    }

    public void save(MultipartFile file) throws IOException, InterruptedException {

        // "java.io.tmpdir" = calea director temporal
        String tempDir = System.getProperty("java.io.tmpdir");
        File tempFile = new File(tempDir + "/" + file.getOriginalFilename());
        file.transferTo(tempFile);

        File pemFile = new File(config.getKeyPath());
        if (!pemFile.exists()) {
            throw new MissingPemKeyException("Ec2 key not found :" + pemFile.getAbsolutePath());

        }

        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win")) {
            Process chmod = Runtime.getRuntime().exec(new String[]{"chmod", "400", pemFile.getAbsolutePath()});
            chmod.waitFor();

        }

        String command = String.format("scp -i %s -o StrictHostKeyChecking=no %s %s@%s:%s", pemFile.getAbsolutePath(), tempFile.getAbsolutePath(), config.getUsername(), config.getHost(), config.getRemoteDir());
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

    }

}

