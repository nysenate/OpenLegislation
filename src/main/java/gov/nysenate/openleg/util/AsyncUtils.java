package gov.nysenate.openleg.util;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class AsyncUtils {

    @Resource(name = "openlegAsync") private ThreadPoolTaskExecutor executor;

    @Async
    public void run(Runnable runnable) {
        runnable.run();
    }

    public <T> CompletableFuture<T> get(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executor);
    }
}
