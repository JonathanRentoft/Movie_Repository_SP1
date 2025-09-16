package app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {
    public static String getPropertyValue(String propName, String ressourceName) {
        Properties prop = new Properties();
        try (InputStream stream = Utils.class.getClassLoader().getResourceAsStream(ressourceName)) {
            prop.load(stream);
            return prop.getProperty(propName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}