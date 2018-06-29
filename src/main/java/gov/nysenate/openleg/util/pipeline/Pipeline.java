package gov.nysenate.openleg.util.pipeline;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Runs a sequence of tasks on a number of objects,
 * with each task executing in parallel.
 *
 * @see PipelineFactory for instantiation details.
 *
 * @param <T>
 * @param <R>
 */
public class Pipeline<T, R> {

    private static final Logger logger = LoggerFactory.getLogger(Pipeline.class);

    private LinkedList<PipelineTask> tasks;
    private Executor executor;
    private CompletableFuture<ImmutableList<R>> result = null;

    Pipeline(Collection<PipelineTask> tasks, Executor executor) {
        this.tasks = new LinkedList<>(tasks);
        this.executor = executor;
    }

    /**
     * Adds the given input items to the incoming queue for the first pipeline task.
     *
     * @param input Collection<T>
     */
    public void addInput(Collection<T> input) {
        tasks.getFirst().addInputs(input);
    }

    /**
     * Sets the pipeline in motion, returning a future that is completed when everything is processed.
     *
     * @return CompletableFuture<ImmutableList<R>>
     */
    public CompletableFuture<ImmutableList<R>> run() {
        if (tasks.isEmpty()) {
            return CompletableFuture.completedFuture(ImmutableList.of());
        }

        LinkedList<CompletableFuture<Void>> futures = new LinkedList<>();
        for (PipelineTask task : tasks) {
            CompletableFuture<Void> cf = CompletableFuture.runAsync(task, executor)
                    .exceptionally(ex -> handleTaskException(ex, futures));
            futures.push(cf);
        }
        // Set the result future to return the result of the last task when its future completes
        PipelineTask<?, R> lastTask = tasks.getLast();
        CompletableFuture<Void> lastTaskFuture = futures.getLast();
        result = lastTaskFuture.thenApply(v -> lastTask.getOutputs());

        // Send the signal for tasks to start and return the result future
        tasks.descendingIterator().forEachRemaining(PipelineTask::startTask);
        return result;
    }

    /* --- Internal Methods --- */

    /**
     * Handles an exception thrown by one of the pipeline tasks.
     *
     * @param ex Throwable
     * @param allTaskFutures List<CompletableFuture<Void>> - list of futures for all tasks
     * @return Void
     */
    private Void handleTaskException(Throwable ex, List<CompletableFuture<Void>> allTaskFutures) {
        // Start a new thread that cancels all of the active futures
        // (a new thread is needed since this method is run from one of the active future threads)
        logger.warn("Canceling pipeline execution due to exception");
        Optional.ofNullable(result)
                .ifPresent(r -> r.completeExceptionally(ex));
        executor.execute(() -> {
            logger.info("killing pipeline tasks due to exception");
            for (CompletableFuture future : allTaskFutures) {
                Optional.ofNullable(future).ifPresent(f -> f.cancel(true));
            }
        });
        return null;
    }
}
