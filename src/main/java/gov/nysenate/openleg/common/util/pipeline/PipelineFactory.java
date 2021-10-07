package gov.nysenate.openleg.common.util.pipeline;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A service used to create {@link Pipeline}s using the configured async Executor.
 */
@Service
public class PipelineFactory {

    @Resource(name = "openlegAsync") private ThreadPoolTaskExecutor executor;

    /**
     * Build a pipeline with a fixed input.
     *
     * @param input {@link Collection}
     * @param <T> Input type
     * @return {@link PipelineBuilder}
     */
    public <T> PipelineBuilder<T> pipelineBuilder(Collection<T> input) {
        BlockingQueue<T> inputQueue = new ArrayBlockingQueue<>(input.size(), false, input);
        return new PipelineBuilder<>(executor, inputQueue, new AtomicBoolean(true));
    }

    /**
     * Build a pipeline with a dynamic input.
     *
     * Will continue processing until the given AtomicBoolean is set to false.
     *
     * @param inputQueue {@link BlockingQueue}
     * @param inputFinal {@link AtomicBoolean}
     * @param <T> input type
     * @return {@link PipelineBuilder}
     */
    public <T> PipelineBuilder<T> pipelineBuilder(BlockingQueue<T> inputQueue, AtomicBoolean inputFinal) {
        return new PipelineBuilder<>(executor, inputQueue, inputFinal);
    }
}
