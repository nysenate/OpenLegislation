package gov.nysenate.openleg.util.pipeline;

/**
 * Exception wrapper that is thrown when an exception occurs in a pipeline task.
 */
public class PipelineException extends RuntimeException {
    PipelineException(Throwable ex) {
        super("An exception occurred in a pipeline task.  See nested ex.", ex);
    }
}
