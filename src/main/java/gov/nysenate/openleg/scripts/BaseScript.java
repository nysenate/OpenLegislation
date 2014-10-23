package gov.nysenate.openleg.scripts;

import gov.nysenate.openleg.util.Application;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

/**
 * Provides a base wrapping layer for scripts that logs all uncaught exceptions
 * and provides utility functions for processing options and help messages.
 *
 * @author graylinkim
 *
 */
abstract public class BaseScript
{
    protected static Logger logger = Logger.getLogger(BaseScript.class);

    /**
     * Used as the script name when printing help.
     */
    protected String SCRIPT_NAME = "BaseScript";

    /**
     * Used instead of default usage string when non-empty.
     */
    protected String USAGE = "";

    /**
     * Initial entry point for all scripts.
     * <p>
     * Wraps calls in a try/catch that logs all uncaught exceptions before re-throwing. Useful
     * for making sure that log4j also reports uncaught exceptions for automated mailing.
     *
     * @param args
     */
    public void run(String[] args) throws Exception
    {
        Options options = getOptions();
        options.addOption("h", "help", false, "Print this message");
        Option environment = new Option("e", "environment", true, "Path to a configuration file for this environment");
        environment.setRequired(true);
        options.addOption(environment);

        try {
            CommandLine opts = new PosixParser().parse(options, args);
            if(opts.hasOption("-h")) {
                printUsage(options);
                System.exit(0);
            } else {
                File propertiesFile = new File(opts.getOptionValue("environment"));
                if (!propertiesFile.canRead()) {
                    logger.fatal("Cannot read: "+propertiesFile);
                    System.exit(1);
                }
                else {
                    Application.bootstrap(propertiesFile.getAbsolutePath(), luceneReadOnly());
                }

                execute(opts);
            }
        }
        catch (MissingOptionException e) {
            logger.fatal(e.getMessage());
            printUsage(options);
            System.exit(1);
        }
        catch (ParseException e) {
            logger.fatal("Error parsing arguments: ", e);
            System.exit(1);
        }
        catch (Exception e) {
            logger.fatal("Unexpected Exception.",e);
            throw e;
        }
        finally {
            Application.shutdown();
        }
    }

    /**
     * Print script usage based on the parse command line options.
     *
     * @param opts
     */
    protected void printUsage(CommandLine opts)
    {
        Options options = new Options();
        for (Option option : opts.getOptions()) {
            options.addOption(option);
        }
        printUsage(options);
    }

    /**
     * Print script usage based on options passed in. Uses SCRIPT_NAME and
     * USAGE class variables when available.
     *
     * @param options
     */
    protected void printUsage(Options options)
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(SCRIPT_NAME, options, USAGE.isEmpty());
    }

    /**
     * Returns the fully configured Options object used to parse the script
     * command line arguments.
     *
     * @return
     * @throws ParseException
     */
    protected Options getOptions()
    {
        return new Options();
    }


    /**
     * Executes the actual script with the parsed command line options.
     *
     * @param opts
     * @throws Exception
     */
    abstract protected void execute(CommandLine opts) throws Exception;

    /**
     * Returns true if the implementing script should not attempt to obtain a lucene write lock
     * Defaults to true unless overridden
     *
     * @return
     */
    protected boolean luceneReadOnly() {
        return true;
    }
}
