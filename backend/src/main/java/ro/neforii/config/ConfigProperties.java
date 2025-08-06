package ro.neforii.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "file-upload.ssh")
public class ConfigProperties {
    private String keyPath;
    private String username;
    private String host;
    private String remoteDir;

}
