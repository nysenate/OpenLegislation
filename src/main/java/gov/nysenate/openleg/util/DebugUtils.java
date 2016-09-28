package gov.nysenate.openleg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Methods that are useful for debugging.
 */
public abstract class DebugUtils {

    private static final Logger logger = LoggerFactory.getLogger(DebugUtils.class);

    private static final String thisClassName = DebugUtils.class.getCanonicalName();

    /** Template for printing line info.  Inputs are class name, method name, and line number */
    private static final String lineInfoTemplate = "%s#%s:%d";

    /**
     * Gets the first stack frame that is not a method in {@link DebugUtils this class}
     * @return {@link StackTraceElement}
     */
    public static StackTraceElement getStackFrame() {
        StackTraceElement[] frames = Thread.currentThread().getStackTrace();
        return Arrays.stream(frames, 2, frames.length)
                .filter((stackTraceEle) ->
                        !thisClassName.equals(stackTraceEle.getClassName()))
                .findFirst().get();
    }

    /**
     * Generates a string containing the class, method and line number where this method was called
     * @see #lineInfoTemplate
     * Does not work for methods in {@link DebugUtils this class}
     * @see #getStackFrame()
     * @return String
     */
    public static String getLineInfo() {
        StackTraceElement frame = getStackFrame();
        return String.format(lineInfoTemplate,
                frame.getClassName(), frame.getMethodName(), frame.getLineNumber());
    }
}
