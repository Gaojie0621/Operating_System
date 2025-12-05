package deadlock_examples;

/**
 * Demonstrates a classic Deadlock situation (Circular Wait).
 * 
 * Scenario:
 * Thread-1 holds Lock A and waits for Lock B.
 * Thread-2 holds Lock B and waits for Lock A.
 * 
 * Both threads will be blocked forever.
 */
public class DeadlockDemo {
    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("Thread-1: Holding Lock A...");
                
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                
                System.out.println("Thread-1: Waiting for Lock B...");
                synchronized (lockB) {
                    System.out.println("Thread-1: Acquired Lock B!");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("Thread-2: Holding Lock B...");
                
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                
                System.out.println("Thread-2: Waiting for Lock A...");
                synchronized (lockA) {
                    System.out.println("Thread-2: Acquired Lock A!");
                }
            }
        });

        t1.start();
        t2.start();
        
        // Main thread just waits to show they are stuck
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Finished!"); // This will never be reached
    }
}
