package com.example.services;

import java.io.InputStream;
import java.util.Properties;

class ApplicationConfig {
    private Properties properties = null;

    String getAuthority() {
        load();

        return properties.getProperty("authority");
    }

    private void load() {
        if (properties == null) {
            InputStream stream = getClass().getClassLoader().getResourceAsStream("config.properties");

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
