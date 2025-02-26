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

            // what is the use of this checking as you can not consider the output ...?
            // i.e. which hello then it will execute successfully.
            // it is very heavy process try to complete in one process spawn.
            // change the variable name OS_BASED_PING_CHECK_COMMAND
            process = new ProcessBuilder(OS_BASED_PING_CHECK_COMMAND,COMMAND_NAME).start();

            // use var keyword
            // do not use java.util.concurrent. -- use import statement if needed.

            // think about a generic logic here --- spawnProcess() that accepts needed argument and gives you output

//            boolean completed = process.waitFor(PING_PROCESS_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS);
            // this is duplicate code

            boolean completed = process.waitFor(PING_PROCESS_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS);

            if (!completed)
            {

                // see other methods also i.e. destroyForcibly
                process.destroy();

                LOGGER.log(Level.WARNING, PING_CHECK_TIMEOUT_ERROR);

                return false;
            }

            int exitCode = process.exitValue();

            // wrong logic
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
                // check for child processes
                process.destroy();
            }
        }
    }

    // change the method name
    private static String analyzePing(String host)
    {
        var commandOutput = new StringBuilder();

        try
        {
            // make it generic as per above comment
            // change the name -- COMMAND_NAME, COMMAND_NO_OF_PACKET_FLAG,COMMAND_PACKET_COUNT
            process = new ProcessBuilder()
                    .command(COMMAND_NAME, COMMAND_NO_OF_PACKET_FLAG,COMMAND_PACKET_COUNT, host)
                    // why we need this -- you should know?
                    .redirectErrorStream(true)
                    .start();

            // use var
            // do not use java.util.concurrent.
            boolean completed = process.waitFor(PING_PROCESS_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS);

            // wrong code if we are reading from process then in which case it is going to true.
            if (!completed)
            {
                process.destroy();

                // use proper name -- which SEPARATOR is this?
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
            // why we need to initialize this variable with ""?
            // wrong code
            while ((LINE = reader.readLine()) != null)
            {
                // use trim if needed.
                commandOutput.append(LINE).append(NEWLINE_SEPARATOR);
            }
        }
        // use proper name
        catch (Exception ex)
        {
            // add logger
            commandOutput.append(PING_OUTPUT_READING_EXCEPTION).append(ex.getMessage()).append(NEWLINE_SEPARATOR);
        }
    }

    private static String extractKPI(String commandOutput, String host)
    {
        if (commandOutput == null || commandOutput.isEmpty())
        {
            // use string.format
            return PING_OUTPUT_EMPTY_ERROR + host + SEPARATOR;
        }

        // change variable names
        var lossMatcher = LOSS_PATTERN.matcher(commandOutput);

        var rttMatcher = RTT_PATTERN.matcher(commandOutput);

        var result = new StringBuilder(host).append(NEWLINE_SEPARATOR);

        // add null check
        if (lossMatcher.find())
        {
            // use trim if needed.
            // do not create useless variables
            var packetLoss = Integer.parseInt(lossMatcher.group(1));

            // change name PACKET_LOSS_INITIAL PACKET_LOSS_FORMATOR
            result.append(PACKET_LOSS_INITIAL).append(packetLoss).append(PACKET_LOSS_FORMATOR).append(NEWLINE_SEPARATOR);
        }
        else
        {
            // make the generic message
            result.append(PACKET_LOSS_NOT_AVAILABLE_ERROR).append(NEWLINE_SEPARATOR);
        }

        // add null check
        if (rttMatcher.find())
        {
            // change variable name RTT_UNIT RTT_OUTPUT
            result.append(RTT_OUTPUT.get(1)).append(rttMatcher.group(2)).append(RTT_UNIT).append(NEWLINE_SEPARATOR);

            result.append(RTT_OUTPUT.get(2)).append(rttMatcher.group(3)).append(RTT_UNIT).append(NEWLINE_SEPARATOR);

            result.append(RTT_OUTPUT.get(3)).append(rttMatcher.group(4)).append(RTT_UNIT).append(NEWLINE_SEPARATOR);
        }
        else
        {
            // make the generic message
            result.append(RTT_NOT_AVAILABLE_ERROR).append(NEWLINE_SEPARATOR);
        }

        result.append(SEPARATOR);

        return result.toString();
    }

    // there is no need of this method, made a generic message and return it.
    private static String handlePingFailure(String commandOutput, String host)
    {
        if (commandOutput == null || commandOutput.isEmpty())
        {
            return String.format("%s%s%s",PING_OUTPUT_EMPTY_ERROR ,host ,SEPARATOR);
        }

        // change name PING_COMMAND_COMMON_ERROR
        return String.format("%s%s%s%s",PING_COMMAND_COMMON_ERROR, host , NEWLINE_SEPARATOR , commandOutput ,SEPARATOR);
    }
}
