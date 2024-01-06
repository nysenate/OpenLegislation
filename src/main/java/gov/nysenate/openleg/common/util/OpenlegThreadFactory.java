package gov.nysenate.openleg.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Nearly a copy of Executors.DefaultThreadFactory, but with more useful naming
 * and the deprecated SecurityManager removed.
 */
public class OpenlegThreadFactory implements ThreadFactory {
    private final ThreadGroup group = Thread.currentThread().getThreadGroup();
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public OpenlegThreadFactory(String prefix) {
        this.namePrefix = prefix + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
