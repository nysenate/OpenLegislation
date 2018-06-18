package gov.nysenate.openleg.util.pipeline;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * A node representing a discrete task in a {@link Pipeline}.
 * Contains references to previous nodes in the pipeline
 * and queues for consuming from and passing to other tasks.
 * @param <T>
 * @param <R>
 */
class PipelineTask<T, R> implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PipelineTask.class);

    /** The amount of time in ms the task should wait for a new input before checking if the previous task is done */
    private static final long inputTimeout = 50;

    private Function<T, Collection<R>> task;
    private BlockingQueue<T> inputQueue;
    private BlockingQueue<R> outputQueue;

    /** Latch used to delay processing until the task is officially started */
    private final CountDownLatch startCountDown = new CountDownLatch(1);

    /**
     * Boolean set to true when this task is finished.
     * A task cannot be finished unless previous tasks have indicated they are finished.
     */
    private final AtomicBoolean finished = new AtomicBoolean(false);

    /** Reference to previous task in the pipeline */
    private PipelineTask previousTask;

    PipelineTask(Function<T, Collection<R>> task,
                         BlockingQueue<T> inputQueue,
                         BlockingQueue<R> outputQueue) {
        this.task = task;
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    /**
     * Run continuously, pulling object results from previous tasks, running this task on
     * those objects, writing the results to the output queue
     */
    @Override
    public void run() {
        try {
            // Wait for start signal to begin processing
            startCountDown.await();
            while (true) {
                boolean prevFinished = isPrevFinished();

                T inputValue = inputQueue.poll(inputTimeout, TimeUnit.MILLISECONDS);

                if (inputValue != null) {
                    Collection<R> outputValues = task.apply(inputValue);
                    addToQueue(outputValues, outputQueue);
                } else if (prevFinished) {
                    // End this task if the previous task is finished and the input poll timed out
                    break;
                }
            }
        } catch (InterruptedException e) {
            throw new PipelineException(e);
        }
        this.finished.set(true);
    }

    /**
     * Allows the task to start.
     */
    void startTask() {
        startCountDown.countDown();
    }

    /**
     * @return true iff this task is finished processing
     */
    boolean isFinished() {
        return finished.get();
    }

    /**
     * Register another task as the preceeding task.
     * This task watches for the previous task to be finished before finishing itself.
     *
     * @param previousTask {@link PipelineTask}
     */
    void registerPreviousTask(PipelineTask previousTask) {
        this.previousTask = previousTask;
    }

    BlockingQueue<R> getOutputQueue() {
        return outputQueue;
    }

    void addInputs(Collection<T> inputs) {
        addToQueue(inputs, inputQueue);
    }

    /**
     * Remove and return the results in the output queue.
     */
    ImmutableList<R> getOutputs() {
        List<R> list = new LinkedList<>();
        outputQueue.drainTo(list);
        return ImmutableList.copyOf(list);
    }

    Function<T, Collection<R>> getTask() {
        return task;
    }

    /**
     * Returns true if the previous node is done
     */
    private boolean isPrevFinished() {
        return Optional.ofNullable(previousTask)
                .map(PipelineTask::isFinished)
                .orElse(true);
    }

    /**
     * Method for adding a collection of values to a blocking queue
     */
    private <E> void addToQueue(Collection<E> values, BlockingQueue<E> queue) {
        for (E value : values) {
            try {
                queue.put(value);
            } catch (InterruptedException e) {
                throw new PipelineException(e);
            }
        }
    }
}
