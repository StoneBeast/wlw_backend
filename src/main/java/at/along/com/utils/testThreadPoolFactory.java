package at.along.com.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class testThreadPoolFactory implements ThreadFactory {
    private AtomicInteger threadIdx = new AtomicInteger(0);

    private String threadNamePrefix;

    public testThreadPoolFactory(String Prefix) {
        threadNamePrefix = Prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(threadNamePrefix + "-xxljob-" + threadIdx.getAndIncrement());
        return thread;
    }
}
