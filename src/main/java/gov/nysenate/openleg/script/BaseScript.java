package gov.nysenate.openleg.script;

import gov.nysenate.openleg.config.ConsoleApplicationConfig;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Provides a base wrapping layer for scripts that logs all uncaught exceptions
 * and provides utility functions for processing options and help messages.
 */
abstract public class BaseScript
{
    private static final Logger logger = LoggerFactory.getLogger(BaseScript.class);

    /** Used as the script name when printing help. */
    protected static String SCRIPT_NAME = "BaseScript";

    /** Used instead of default usage string when non-empty. */
    protected static String USAGE = "";

    /**
     * Boots up the Spring Application and returns the context.
     * @return AnnotationConfigApplicationContext
     */
    public static AnnotationConfigApplicationContext init() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        String profile = System.getProperty("spring.profiles.active");
        logger.info("Using spring profile: {}", profile);
        ctx.getEnvironment().setActiveProfiles(profile);
        ctx.register(ConsoleApplicationConfig.class);
        ctx.refresh();
        ctx.start();
        return ctx;
    }

    /**
     * Shutdown the given application context.
     * @param ctx
     */
    public static void shutdown(AnnotationConfigApplicationContext ctx) {
        ctx.stop();
    }

    /**
     * Returns the command line options given the spec 'opts' and the actual command line args 'args'
     *
     * @param opts Options - The specification for the options
     * @param args String[] - Args from the main method
     * @return CommandLine
     * @throws ParseException
     */
    public static CommandLine getCommandLine(Options opts, String[] args) throws ParseException {
        return new PosixParser().parse(opts, args);
    }

    /**
     * Print script usage based on the parse command line options.
     *
     * @param opts
     */
    protected static void printUsage(CommandLine opts) {
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
    protected static void printUsage(Options options) {
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
    protected Options getOptions(){
        return new Options();
    }

    /**
     * Executes the actual script with the parsed command line options.
     *
     * @param opts
     * @throws Exception
     */
    abstract protected void execute(CommandLine opts) throws Exception;
}