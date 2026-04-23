package com.authunidate.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FirebaseConfig {
    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);
    private static final String DEFAULT_CREDENTIALS_FILE = "firebase-service-account.json";

    @Value("${firebase.enabled:true}")
    private boolean firebaseEnabled;

    @Value("${firebase.credentials.path:}")
    private String credentialsPath;

    @PostConstruct
    public void init() throws IOException {
        if (!firebaseEnabled || !FirebaseApp.getApps().isEmpty()) {
            return;
        }

        try (InputStream serviceAccount = resolveCredentialsStream()) {
            if (serviceAccount == null) {
                log.warn("Firebase credentials not found. Firebase login endpoint will not work.");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("Firebase initialized successfully.");
        }
    }

    private InputStream resolveCredentialsStream() throws IOException {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            ClassPathResource defaultResource = new ClassPathResource(DEFAULT_CREDENTIALS_FILE);
            return defaultResource.exists() ? defaultResource.getInputStream() : null;
        }

        if (credentialsPath.startsWith("classpath:")) {
            String resourcePath = credentialsPath.substring("classpath:".length());
            ClassPathResource resource = new ClassPathResource(resourcePath);
            return resource.exists() ? resource.getInputStream() : null;
        }

        Path path = Paths.get(credentialsPath);
        if (!path.isAbsolute()) {
            path = path.toAbsolutePath().normalize();
        }
        return Files.exists(path) ? new FileInputStream(path.toFile()) : null;
    }
}
