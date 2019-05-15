package com.example.client;

import java.io.InputStream;
import java.util.Properties;

public class ApplicationConfig {
    private Properties properties = null;

    public String getUrl() {
        load();

        return properties.getProperty("url");
    }

    public String getClientId() {
        load();

        return properties.getProperty("clientId");
    }

    public String getClientSecret() {
        load();

        return properties.getProperty("clientSecret");
    }

    public String getScope() {
        load();

        return properties.getProperty("scope");
    }

    public String getAuthority() {
        load();

        return properties.getProperty("authority");
    }

    private void load() {
        if (properties == null) {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");

            if (stream != null) {
                try {
                    properties = new Properties();
                    properties.load(stream);
                } catch (Exception e) {
                    properties = null;
                    e.printStackTrace();
                }
            }
        }
    }
}
