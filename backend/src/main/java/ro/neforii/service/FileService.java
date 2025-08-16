package ro.neforii.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ro.neforii.config.CloudinaryProperties;
import ro.neforii.config.ConfigProperties;
import ro.neforii.exception.MissingPemKeyException;
import ro.neforii.utils.logger.Logger;
import ro.neforii.utils.logger.LoggerType;

import java.io.*;
import java.util.Map;


//@Service
//public class FileService {
//
//    private static final String LOG_PREFIX = "FileService: ";
//
//    private final ConfigProperties config;
//
//    public FileService(ConfigProperties config) {
//        this.config = config;
//    }
//
//    public String save(MultipartFile file) throws IOException, InterruptedException {
//        Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Saving file: " + file.getOriginalFilename());
//
//        try {
//            // "java.io.tmpdir" = calea director temporal
//            String tempDir = System.getProperty("java.io.tmpdir");
//            File tempFile = new File(tempDir + "/" + file.getOriginalFilename());
//            file.transferTo(tempFile);
//            Logger.log(LoggerType.DEBUG, LOG_PREFIX + "File transferred to temp location: " + tempFile.getAbsolutePath());
//
//            File pemFile = new File(config.getKeyPath());
//            if (!pemFile.exists()) {
//                Logger.log(LoggerType.ERROR, LOG_PREFIX + "EC2 key not found at: " + pemFile.getAbsolutePath());
//                throw new MissingPemKeyException("Ec2 key not found :" + pemFile.getAbsolutePath());
//            }
//
//            String os = System.getProperty("os.name").toLowerCase();
//            if (!os.contains("win")) {
//                Logger.log(LoggerType.DEBUG, LOG_PREFIX + "Setting file permissions for key on non-Windows OS");
//                Process chmod = Runtime.getRuntime().exec(new String[]{"chmod", "400", pemFile.getAbsolutePath()});
//                chmod.waitFor();
//            }
//
//        String command = String.format("scp -i %s -o StrictHostKeyChecking=no %s %s@%s:%s", pemFile.getAbsolutePath(), tempFile.getAbsolutePath(), config.getUsername(), config.getHost(), config.getRemoteDir());
//        Process process = Runtime.getRuntime().exec(command);
//        process.waitFor();
//
//        return config.getRemoteDir() + "/" + file.getOriginalFilename();
//
//        } catch (IOException | InterruptedException e) {
//            Logger.log(LoggerType.ERROR, LOG_PREFIX + "Failed to upload file: " + e.getMessage());
//            throw e;
//        }
//    }
//}

@Service
public class FileService {
    private final Cloudinary cloudinary;

    public FileService(CloudinaryProperties properties) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", properties.getCloudName(),
                "api_key", properties.getApiKey(),
                "api_secret", properties.getApiSecret()
        ));
    }

    public String save(MultipartFile file) throws IOException {
        Map uploadUrl = (Map) cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
        return uploadUrl.get("secure_url").toString();

    }

    public String save(byte[] bytes) throws IOException {
        Map upload = (Map) cloudinary.uploader()
                .upload(bytes, ObjectUtils.asMap("resource_type", "auto"));
        return upload.get("secure_url").toString();
    }
}
