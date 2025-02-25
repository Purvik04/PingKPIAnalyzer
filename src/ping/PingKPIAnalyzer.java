package ping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import static ping.PingKPIAnalyzerConstants.*;

public class PingKPIAnalyzer
{
    public static void runCommand(String[] args)
    {
        if (args.length == 0)
        {
           LOGGER.warning("Usage: java PingUtility <host1> <host2> ...");

            return;
        }

        if (!isPingAvailable())
        {
            return;
        }

        for (var host : args)
        {
            LOGGER.info(analyzePing(host));
        }
    }

    private static boolean isPingAvailable()
    {
        try
        {
            process = new ProcessBuilder(OS_BASED_PING_CHECK_COMMAND,COMMAND_NAME).start();

            boolean completed = process.waitFor(PING_PROCESS_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS);

            if (!completed)
            {
                process.destroy();

                LOGGER.log(Level.WARNING, PING_CHECK_TIMEOUT_ERROR);

                return false;
            }

            int exitCode = process.exitValue();

            if (exitCode == 0)
            {
                return true;
            }
            else
            {
                LOGGER.log(Level.WARNING, PING_NOT_AVAILABLE_ERROR);
                return false;
            }
        }
        catch (Exception exception)
        {
            LOGGER.log(Level.SEVERE,exception.getMessage());

            return false;
        }
        finally
        {
            if (process != null && process.isAlive())
            {
                process.destroy();
            }
        }
    }

    private static String analyzePing(String host)
    {
        var commandOutput = new StringBuilder();

        try
        {
            process = new ProcessBuilder()
                    .command(COMMAND_NAME, COMMAND_NO_OF_PACKET_FLAG,COMMAND_PACKET_COUNT, host)
                    .redirectErrorStream(true)
                    .start();

            boolean completed = process.waitFor(PING_PROCESS_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS);

            if (!completed)
            {
                process.destroy();

                return String.format("%s%s%s",PING_TIMEOUT_FOR_HOST_ERROR , host , SEPARATOR);
            }

            readStream(process, commandOutput);

            return process.exitValue() == 0
                    ? extractKPI(commandOutput.toString(), host)
                    : handlePingFailure(commandOutput.toString(), host);

        }
        catch (Exception exception)
        {
            return String.format("%s%s%s%s",PING_COMMAND_EXECUTION_EXCEPTION, host, exception.getMessage(),SEPARATOR);
        }
    }

    private static void readStream(Process process, StringBuilder commandOutput)
    {
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
        {
            while ((LINE = reader.readLine()) != null)
            {
                commandOutput.append(LINE).append(NEWLINE_SEPARATOR);
            }
        }
        catch (Exception ex)
        {
            commandOutput.append(PING_OUTPUT_READING_EXCEPTION).append(ex.getMessage()).append(NEWLINE_SEPARATOR);
        }
    }

    private static String extractKPI(String commandOutput, String host)
    {
        if (commandOutput == null || commandOutput.isEmpty())
        {
            return PING_OUTPUT_EMPTY_ERROR + host + SEPARATOR;
        }

        var lossMatcher = LOSS_PATTERN.matcher(commandOutput);

        var rttMatcher = RTT_PATTERN.matcher(commandOutput);

        var result = new StringBuilder(host).append(NEWLINE_SEPARATOR);

        if (lossMatcher.find())
        {
            var packetLoss = Integer.parseInt(lossMatcher.group(1));

            result.append(PACKET_LOSS_INITIAL).append(packetLoss).append(PACKET_LOSS_FORMATOR).append(NEWLINE_SEPARATOR);
        }
        else
        {
            result.append(PACKET_LOSS_NOT_AVAILABLE_ERROR).append(NEWLINE_SEPARATOR);
        }

        if (rttMatcher.find())
        {
            result.append(RTT_OUTPUT.get(1)).append(rttMatcher.group(2)).append(RTT_UNIT).append(NEWLINE_SEPARATOR);

            result.append(RTT_OUTPUT.get(2)).append(rttMatcher.group(3)).append(RTT_UNIT).append(NEWLINE_SEPARATOR);

            result.append(RTT_OUTPUT.get(3)).append(rttMatcher.group(4)).append(RTT_UNIT).append(NEWLINE_SEPARATOR);
        }
        else
        {
            result.append(RTT_NOT_AVAILABLE_ERROR).append(NEWLINE_SEPARATOR);
        }

        result.append(SEPARATOR);

        return result.toString();
    }

    private static String handlePingFailure(String commandOutput, String host)
    {
        if (commandOutput == null || commandOutput.isEmpty())
        {
            return String.format("%s%s%s",PING_OUTPUT_EMPTY_ERROR ,host ,SEPARATOR);
        }

        return String.format("%s%s%s%s",PING_COMMAND_COMMON_ERROR, host , NEWLINE_SEPARATOR , commandOutput ,SEPARATOR);
    }
}
