package ping;

import java.io.IOException;
import java.util.Properties;
import static ping.PingExecutorConstants.LOGGER;

public class PingConfigManager
{
    private static final Properties properties = new Properties();

    private static final String CONFIG_FILE_NAME = "config.properties";

    static
    {
        try (var inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE_NAME))
        {
            if (inputStream == null)
            {
                LOGGER.severe("Configuration file not found: " + CONFIG_FILE_NAME);
            }
            else
            {
                properties.load(inputStream);
            }
        }
        catch (IOException ioException)
        {
            LOGGER.severe("Error loading configuration file: " + ioException.getMessage());
        }
    }

    public static String get(String key, String defaultValue)
    {
        return properties.getProperty(key, defaultValue);
    }
}
