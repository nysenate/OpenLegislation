package gov.nysenate.openleg.common.util.pipeline;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Constructs a {@link Pipeline} by adding tasks one at a time.
 *
 * @param <T>
 */
public class PipelineBuilder<T> {

    private final Executor executor;

    private List<PipelineTask> tasks;

    private PipelineTask<?, T> lastTask = null;

    private final BlockingQueue inputQueue;

    private final AtomicBoolean inputFinal;

    PipelineBuilder(Executor executor, BlockingQueue<T> inputQueue, AtomicBoolean inputFinal) {
        this.executor = executor;
        this.tasks = new LinkedList<>();
        this.inputQueue = inputQueue;
        this.inputFinal = inputFinal;
    }

    /**
     * Create a new pipeline builder from an existing one and a new set of pipeline tasks.
     *
     * @param pb {@link PipelineBuilder}
     * @param newTasks {@link PipelineTask}
     */
    private PipelineBuilder(PipelineBuilder<?> pb, LinkedList<PipelineTask<?, T>> newTasks) {
        this.executor = pb.executor;
        this.tasks = pb.tasks;
        this.inputQueue = pb.inputQueue;
        this.inputFinal = pb.inputFinal;

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
    @SuppressWarnings("unchecked")
    public <E> PipelineBuilder<E> addTask(Function<T, Collection<E>> task,
                                             int outputCapacity, int instances) {
        if (instances < 1) {
            throw new IllegalStateException("You must create at least one instance of a task.");
        }
        final BlockingQueue<T> inputQueue;
        if (lastTask == null) {
            inputQueue = (BlockingQueue<T>) this.inputQueue;
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
            final AtomicBoolean prevFinished = prevTask == null ? this.inputFinal : prevTask.getFinished();
            PipelineTask<T, E> newTask = new PipelineTask<>(task, inputQueue, outputQueue, prevFinished);
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
    public <E> PipelineBuilder<E> addTask(Function<T, Collection<E>> task,
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
    public <E> PipelineBuilder<E> addTask(Function<T, Collection<E>> task) {
        return addTask(task, -1);
    }

    /**
     * Constructs the pipeline and returns it
     *
     * @return {@link Pipeline}
     */
    public Pipeline<T> build() {
        return new Pipeline<>(tasks, executor);
    }

}
