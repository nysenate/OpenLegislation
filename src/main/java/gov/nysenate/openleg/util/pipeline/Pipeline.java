package gov.nysenate.openleg.util.pipeline;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
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
        Iterator<PipelineTask> taskItr = tasks.descendingIterator();
        if (!taskItr.hasNext()) {
            return CompletableFuture.completedFuture(ImmutableList.of());
        }
        PipelineTask<?, R> lastTask = taskItr.next();
        CompletableFuture<Void> lastTaskFuture = CompletableFuture.runAsync(lastTask, executor);
        while (taskItr.hasNext()) {
            PipelineTask task = taskItr.next();
            executor.execute(task);
        }
        return lastTaskFuture.thenApply(v -> lastTask.getOutputs());
    }
}
