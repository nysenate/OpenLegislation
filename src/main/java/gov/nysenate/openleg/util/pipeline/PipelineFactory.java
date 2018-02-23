package gov.nysenate.openleg.util.pipeline;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * A service used to create {@link Pipeline}s using the configured async Executor.
 */
@Service
public class PipelineFactory {

    @Resource(name = "openlegAsync") private ThreadPoolTaskExecutor executor;

    public <T> PipelineBuilder<T, T> pipelineBuilder() {
        return new PipelineBuilder<>(executor);
    }
}
