package deadlock_examples;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates Deadlock Detection.
 * 
 * Strategy:
 * Allow deadlock to happen, but have a monitoring mechanism to detect it.
 * In Java, ThreadMXBean can find deadlocked threads.
 */
public class DeadlockDetection {
    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) {
        // 1. Start the Deadlock Monitor
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            long[] threadIds = bean.findDeadlockedThreads(); // Check for deadlocks

            if (threadIds != null) {
                System.out.println("!!! DEADLOCK DETECTED !!!");
                ThreadInfo[] infos = bean.getThreadInfo(threadIds);
                for (ThreadInfo info : infos) {
                    System.out.println(
                            "Deadlocked Thread: " + info.getThreadName() + " (ID: " + info.getThreadId() + ")");
                    System.out.println("Waiting for lock: " + info.getLockInfo());
                    System.out.println("Lock held by: " + info.getLockOwnerName());
                }
                System.exit(1); // Recovery strategy: Kill the process (simplest recovery)
            }
        }, 2, 5, TimeUnit.SECONDS); // Check every 5 seconds

        // 2. Create a Deadlock
        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("Thread-1: Holding Lock A...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                System.out.println("Thread-1: Waiting for Lock B...");
                synchronized (lockB) {
                    System.out.println("Thread-1: Acquired Lock B!");
                }
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("Thread-2: Holding Lock B...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                System.out.println("Thread-2: Waiting for Lock A...");
                synchronized (lockA) {
                    System.out.println("Thread-2: Acquired Lock A!");
                }
            }
        }, "Thread-2");

        t1.start();
        t2.start();
    }
}
