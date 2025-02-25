package ping;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import static ping.PingKPIAnalyzerConstants.LOGGER;

public class ConfigLoader
{
    private static final Properties properties = new Properties();

    private static final String CONFIG_FILE_NAME = "config.properties";

    static
    {
        try (var fileInputStream = new FileInputStream(CONFIG_FILE_NAME))
        {
            properties.load(fileInputStream);
        }
        catch (IOException ioException)
        {
            LOGGER.severe(ioException.getMessage());
        }
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}