package ping;

import java.util.logging.Logger;
import java.util.regex.Pattern;

// change the class name
public class PingExecutorConstants
{
    // check whole class if you think particular variable should be configurable then add it in properties file.
    protected static final Logger LOGGER = Logger.getLogger(PingExecutor.class.getName());

    protected static final Pattern PACKET_LOSS_PATTERN = Pattern.compile("(\\d+)% packet loss");

    protected static final Pattern PACKET_RTT_PATTERN = Pattern.compile("(rtt|Minimum|Avg|Maximum).*?([0-9.]+).*?([0-9.]+).*?([0-9.]+)");

    protected static final String SECTION_DIVIDER = "\n-----------------------------------";

    protected static final String NEWLINE_SEPARATOR = "\n";

    protected static final String OS = System.getProperty("os.name").toLowerCase();

    protected static final String PING_COMMAND = "ping";

    protected static final String PACKET_COUNT_FLAG = OS.contains("win") ? "-n" : "-c";

    protected static final int DEFAULT_PACKET_COUNT = 5;

    protected static final int DEFAULT_PING_PROCESS_TIMEOUT = 5;

    // it should be in int format
    protected static final int PACKET_COUNT = Integer.parseInt(PingConfigManager.get("ping.packet.count", String.valueOf(DEFAULT_PACKET_COUNT)));

    protected static final int PING_PROCESS_TIMEOUT = Integer.parseInt(PingConfigManager.get("ping.timeout", String.valueOf(DEFAULT_PING_PROCESS_TIMEOUT)));

    protected static final String PING_EXECUTABLE_LOCATOR = OS.contains("win") ? "where" : "which";

    protected static final String PING_NOT_AVAILABLE_ERROR = "Error: 'ping' command not found. Please install it.";

    protected static final String PING_CHECK_TIMEOUT_ERROR = "Ping availability check took too long and was terminated.";

    protected static final String PING_TIMEOUT_FOR_HOST_ERROR = "Error: Ping process timed out for ";

    protected static final String PING_OUTPUT_EMPTY_ERROR= "Error: No output received from ping command for ";

    protected static final String PING_COMMAND_EXECUTION_ERROR = "Error executing ping for ";

    protected static final String PACKET_LOSS_PERENTAGE_LABEL =  "%";

    protected static final String RTT_UNIT_MILISECONDS = " ms";

    protected static final String PACKET_LOSS_LABEL = "Packet Loss: ";

    protected static final String RTT_MIN_LABEL = "RTT MIN : ";

    protected static final String RTT_AVG_LABEL = "RTT AVG : ";

    protected static final String RTT_MAX_LABEL = "RTT MAX : ";

    // change the name
    protected static String EMPTY_STRING = "";

    // it should not be static
//    protected static Process process = null;


}
