package org.scuola.bot;

import java.io.InputStream;
import java.util.Properties;

public class MyConfiguration {

    private static Properties props = new Properties();

    static {
        try {
            InputStream is = MyConfiguration.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties");

            if (is == null) {
                throw new RuntimeException("config.properties non trovato in resources");
            }

            props.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
