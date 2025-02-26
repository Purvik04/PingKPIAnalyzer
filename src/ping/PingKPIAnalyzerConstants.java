package ping;

import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

// change the class name
public class PingKPIAnalyzerConstants
{

    // check whole class if you think particular variable should be configurable then add it in properties file.
    protected static final Logger LOGGER = Logger.getLogger(PingKPIAnalyzer.class.getName());

    protected static final Pattern LOSS_PATTERN = Pattern.compile("(\\d+)% packet loss");

    protected static final Pattern RTT_PATTERN = Pattern.compile("(rtt|Minimum|Avg|Maximum).*?([0-9.]+).*?([0-9.]+).*?([0-9.]+)");

    protected static final String SEPARATOR = "\n-----------------------------------";

    protected static final String NEWLINE_SEPARATOR = "\n";

    protected static final String OS = System.getProperty("os.name").toLowerCase();

    protected static final String COMMAND_NAME = "ping";

    protected static final String COMMAND_NO_OF_PACKET_FLAG = OS.contains("win") ? "-n" : "-c";

    protected static final int DEFAULT_COMMAND_PACKET_COUNT = 5;

    protected static final int DEFAULT_PING_PROCESS_TIMEOUT = 5;

    // it should be in int format
    protected static final String COMMAND_PACKET_COUNT = ConfigLoader.get("ping.packet.count", String.valueOf(DEFAULT_COMMAND_PACKET_COUNT));

    protected static final int PING_PROCESS_TIMEOUT = Integer.parseInt(ConfigLoader.get("ping.timeout", String.valueOf(DEFAULT_PING_PROCESS_TIMEOUT)));

    protected static final String OS_BASED_PING_CHECK_COMMAND = OS.contains("win") ? "where" : "which";

    protected static final String PING_NOT_AVAILABLE_ERROR = "Error: 'ping' command not found. Please install it.";

    protected static final String PING_CHECK_TIMEOUT_ERROR = "Ping availability check took too long and was terminated.";

    protected static final String PING_TIMEOUT_FOR_HOST_ERROR = "Error: Ping process timed out for ";

    protected static final String PING_OUTPUT_READING_EXCEPTION = "Error reading process output: ";

    protected static final String PING_OUTPUT_EMPTY_ERROR= "Error: No output received from ping command for ";

    protected static final String PING_COMMAND_EXECUTION_EXCEPTION = "Error executing ping for ";

    protected static final String PING_COMMAND_COMMON_ERROR = "Error: Ping process failed for ";

    protected static final String PACKET_LOSS_NOT_AVAILABLE_ERROR = "Packet Loss: Not Available";

    protected static final String RTT_NOT_AVAILABLE_ERROR =  "RTT: Not Available";

    protected static final String PACKET_LOSS_FORMATOR =  "%";

    protected static final String RTT_UNIT = " ms";

    protected static final String PACKET_LOSS_INITIAL = "Packet Loss: ";

    // change the name
    protected static String LINE = "";

    // it should not be static
    protected static Process process = null;

    protected static final Map<Integer, String> RTT_OUTPUT = Map.of(
            1 , "RTT MIN ",
            2 , "RTT AVG ",
            3 , "RTT MAX "
    );
}
