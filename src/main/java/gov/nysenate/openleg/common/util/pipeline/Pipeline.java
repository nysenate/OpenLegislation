package gov.nysenate.openleg.common.util.pipeline;

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
 */
public class Pipeline<T> {

    private static final Logger logger = LoggerFactory.getLogger(Pipeline.class);

    private final LinkedList<PipelineTask> tasks;
    private final Executor executor;
    private CompletableFuture<ImmutableList<T>> result = null;

    Pipeline(Collection<PipelineTask> tasks, Executor executor) {
        this.tasks = new LinkedList<>(tasks);
        this.executor = executor;
    }

    /**
     * Sets the pipeline in motion, returning a future that is completed when everything is processed.
     *
     * @return CompletableFuture<ImmutableList<R>>
     */
    @SuppressWarnings("unchecked")
    public CompletableFuture<ImmutableList<T>> run() {
        if (tasks.isEmpty()) {
            return CompletableFuture.completedFuture(ImmutableList.of());
        }

        LinkedList<CompletableFuture<Void>> futures = new LinkedList<>();
        for (PipelineTask task : tasks) {
            CompletableFuture<Void> cf = CompletableFuture.runAsync(task, executor)
                    .exceptionally(ex -> handleTaskException(ex, tasks));
            futures.add(cf);
        }
        // Set the result future to return the result of the last task when its future completes
        PipelineTask<?, T> lastTask = tasks.getLast();
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
     * @param tasks {@link List<PipelineTask>}
     * @return Void
     */
    private Void handleTaskException(Throwable ex, LinkedList<PipelineTask> tasks) {
        // Cause the result future to terminate and throw an exception.
        Optional.ofNullable(result)
                .ifPresent(r -> r.completeExceptionally(ex));
        // Set the termination flag for each of the pipeline tasks
        tasks.descendingIterator().forEachRemaining(PipelineTask::terminate);
        return null;
    }
}
