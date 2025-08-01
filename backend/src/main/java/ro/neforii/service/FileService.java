package ro.neforii.service;


import com.jcraft.jsch.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

//@Service
//public class FileService {
//
//    //local pentru test
////    private static final String uploadDir = "uploads";
////    //calea spre ec2
//    private static final String uploadDir = "/home/ubuntu/images_uploads";
//
//
//
//
//    public void save(String file, byte[] bytes) throws IOException {
//        Path dir = Paths.get(uploadDir);
//
//        if (!Files.exists(dir)) {
//            Files.createDirectories(dir);
//        }
//
//        Path filePath = dir.resolve(file);
//        Files.write(filePath, bytes);
//    }
//
//}

@Service
public class FileService {

    private static final String host = "13.48.203.214";
    private static final int port = 22;
    private static String username = "ubuntu";
    private static final String privateKey = "C:/Users/Alexandra/Desktop/Neforii/.ssh/ZTH-Database-key.pem";
    private static final String dir = "/home/ubuntu/images_uploads";

    public void save(String fileName, byte[] bytes){
        try{
            JSch.setLogger(new com.jcraft.jsch.Logger() {
                public boolean isEnabled(int level) { return true; }
                public void log(int level, String message) { System.out.println(message); }
            });

            JSch jSch = new JSch();
            byte[] privateKeyBytes = Files.readAllBytes(Paths.get(privateKey));
            jSch.addIdentity(privateKey);

            Session session = jSch.getSession(username, host, port);

            Properties properties = new Properties();
            // ma contectez automat cu orice host key fara verificare
            properties.put("StrictHostKeyChecking", "no");
            session.setConfig(properties);
            session.connect();

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            channelSftp.cd(dir);
            channelSftp.put(new ByteArrayInputStream(bytes), fileName);
            channelSftp.disconnect();
            session.disconnect();


        } catch (JSchException e) {
            throw new RuntimeException("Error to jsch: " + e.getMessage());
        } catch (SftpException e) {
            throw new RuntimeException("Error to stpf channel: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Error to load the .pem key: " + e.getMessage());
        }
    }

}