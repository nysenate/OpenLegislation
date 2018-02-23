package gov.nysenate.openleg.util.pipeline;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

/**
 * Constructs a {@link Pipeline} by adding tasks one at a time.
 *
 * @param <T>
 * @param <R>
 */
public class PipelineBuilder<T, R> {

    private Executor executor;

    private List<PipelineTask> tasks;

    private PipelineTask<?, R> lastTask = null;

    PipelineBuilder(Executor executor) {
        this.executor = executor;
        this.tasks = new LinkedList<>();
    }

    /**
     * Create a new pipeline builder from an existing one and a new set of pipeline tasks.
     *
     * @param pb {@link PipelineBuilder}
     * @param newTasks {@link PipelineTask}
     */
    private PipelineBuilder(PipelineBuilder<T, ?> pb, LinkedList<PipelineTask<?, R>> newTasks) {
        this.executor = pb.executor;
        this.tasks = pb.tasks;
        this.tasks.addAll(newTasks);
        this.lastTask = newTasks.getLast();
    }

    /**
     * Adds a new task to the pipeline builder.
     * The task must consume the objects produced by the last added task.
     * The task can be limited in output capacity to manage memory footprint.
     * Multiple instances of the task can be requested,
     * all consuming from the same set of inputs,
     * and writing to the same set of outputs.
     *
     * @param task Function<R, Collection<E>> - the task to be performed
     * @param outputCapacity int - sets an upper limit for the number of results in the output queue
     * @param instances int - specifies number of instances of the task that run
     * @param <E>
     * @return {@link PipelineBuilder}
     */
    public <E> PipelineBuilder<T, E> addTask(Function<R, Collection<E>> task,
                                             int outputCapacity, int instances) {
        if (instances < 1) {
            throw new IllegalStateException("You must create at least one instance of a task.");
        }
        BlockingQueue<R> inputQueue;
        if (lastTask == null) {
            inputQueue = new LinkedBlockingQueue<>();
        } else {
            inputQueue = lastTask.getOutputQueue();
        }
        BlockingQueue<E> outputQueue;
        if (outputCapacity > 0) {
            outputQueue = new ArrayBlockingQueue<>(outputCapacity);
        } else {
            outputQueue = new LinkedBlockingQueue<>();
        }
        LinkedList<PipelineTask<?, E>> newTasks = new LinkedList<>();
        PipelineTask prevTask = lastTask;
        for (int i = 0; i < instances; i++) {
            PipelineTask<R, E> newTask = new PipelineTask<>(task, inputQueue, outputQueue);
            if (prevTask != null) {
                newTask.registerPreviousTask(lastTask);
            }
            prevTask = newTask;
            newTasks.add(newTask);
        }
        return new PipelineBuilder<>(this, newTasks);
    }

    /**
     * @see #addTask(Function, int, int)
     * Overload that only creates 1 instance of the task.
     *
     * @param task Function<R, Collection<E>> - the task to be performed
     * @param outputCapacity int - sets an upper limit for the number of results in the output queue
     * @param <E>
     * @return {@link PipelineBuilder}
     */
    public <E> PipelineBuilder<T, E> addTask(Function<R, Collection<E>> task,
                                             int outputCapacity) {
        return addTask(task, outputCapacity, 1);
    }

    /**
     * @see #addTask(Function, int)
     * Overload that has no output limit.
     *
     * @param task Function<R, Collection<E>> - the task to be performed
     * @param <E>
     * @return {@link PipelineBuilder}
     */
    public <E> PipelineBuilder<T, E> addTask(Function<R, Collection<E>> task) {
        return addTask(task, -1);
    }

    /**
     * Constructs the pipeline and returns it
     *
     * @return {@link Pipeline}
     */
    public Pipeline<T, R> build() {
        return new Pipeline<>(tasks, executor);
    }

}
