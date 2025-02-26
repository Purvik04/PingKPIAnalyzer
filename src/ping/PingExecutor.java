package ping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import static ping.PingExecutorConstants.*;
import java.util.concurrent.TimeUnit;

public class PingExecutor
{
    private static Process process = null;

    public static void runCommand(String[] args)
    {
        if (args.length == 0)
        {
            LOGGER.warning("Usage: java PingUtility <host1> <host2> ...");

            return;
        }

        if (!isPingAvailable())
        {
            LOGGER.log(Level.WARNING, PING_NOT_AVAILABLE_ERROR);

            return;
        }

        for (var host : args) {
            LOGGER.info(excutePing(host));
        }
    }

    private static Process spawnProcess(ArrayList<String> command) throws Exception
    {
        return new ProcessBuilder(command).redirectErrorStream(true).start();
    }

    private static boolean isPingAvailable()
    {
        var commandOutput = new StringBuilder();

        try {
            process = spawnProcess(new ArrayList<String>(Arrays.asList(PING_EXECUTABLE_LOCATOR, PING_COMMAND)));

            readStream(process, commandOutput);

            var completed = process.waitFor(PING_PROCESS_TIMEOUT, TimeUnit.SECONDS);

            if (!completed) {
                // see other methods also i.e. destroyForcibly
                process.destroyForcibly();

                LOGGER.log(Level.WARNING, PING_CHECK_TIMEOUT_ERROR);

                return false;
            }

            int exitCode = process.exitValue();

            if (exitCode == 0) {
                System.out.println(commandOutput);
                return commandOutput.toString().contains("ping");
            } else {
                return false;
            }
        }
        catch (Exception exception)
        {
            LOGGER.log(Level.SEVERE, exception.getMessage());

            return false;
        }
        finally
        {
            if (process != null && process.isAlive())
            {
                // check for child processes
                process.destroyForcibly();
            }
        }
    }

    // change the method name
    private static String excutePing(String host)
    {
        var commandOutput = new StringBuilder();

        try
        {
            process = spawnProcess(new ArrayList<>(Arrays.asList(PING_COMMAND, PACKET_COUNT_FLAG, String.valueOf(PACKET_COUNT), host)));

            readStream(process, commandOutput);

            var completed = process.waitFor(PING_PROCESS_TIMEOUT, TimeUnit.SECONDS);

            // wrong code if we are reading from process then in which case it is going to true.
            if (!completed) {
                process.destroy();

                return String.format("%s%s%s", PING_TIMEOUT_FOR_HOST_ERROR, host, SECTION_DIVIDER);
            }

            if (process.exitValue() == 0) {
                return extractKPI(commandOutput.toString(), host);
            }

            return String.format("%s%s%s", PING_COMMAND_EXECUTION_ERROR,host,SECTION_DIVIDER);

        }
        catch (Exception exception)
        {
            return String.format("%s%s%s%s", PING_COMMAND_EXECUTION_ERROR, host, exception.getMessage(), SECTION_DIVIDER);
        }
    }

    private static void readStream(Process process, StringBuilder commandOutput)
    {
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
        {
            var readLine = EMPTY_STRING;
            while ((readLine = reader.readLine()) != null)
            {
                commandOutput.append(readLine.trim()).append(NEWLINE_SEPARATOR);
            }

        }
        catch (Exception exception)
        {
            LOGGER.log(Level.SEVERE,exception.getMessage());
        }
    }

    private static String extractKPI(String commandOutput, String host)
    {
        if (commandOutput == null || commandOutput.isEmpty())
        {
            return String.format("%s%s%s",PING_OUTPUT_EMPTY_ERROR,host, SECTION_DIVIDER);
        }

        var packetLossMatcher = PACKET_LOSS_PATTERN.matcher(commandOutput);

        var packetRTTMatcher = PACKET_RTT_PATTERN.matcher(commandOutput);

        var result = new StringBuilder(host).append(NEWLINE_SEPARATOR);

        if (packetLossMatcher.find() && packetRTTMatcher.find())
        {
            result.append(PACKET_LOSS_LABEL).append(Integer.parseInt(packetLossMatcher.group(1))).append(PACKET_LOSS_PERENTAGE_LABEL).append(NEWLINE_SEPARATOR);

            result.append(RTT_MIN_LABEL).append(packetRTTMatcher.group(2)).append(RTT_UNIT_MILISECONDS).append(NEWLINE_SEPARATOR);

            result.append(RTT_AVG_LABEL).append(packetRTTMatcher.group(3)).append(RTT_UNIT_MILISECONDS).append(NEWLINE_SEPARATOR);

            result.append(RTT_MAX_LABEL).append(packetRTTMatcher.group(4)).append(RTT_UNIT_MILISECONDS).append(SECTION_DIVIDER);
        }
        else
        {
            result.append(PING_OUTPUT_EMPTY_ERROR).append(host).append(NEWLINE_SEPARATOR);
        }

        return result.toString();
    }
}
