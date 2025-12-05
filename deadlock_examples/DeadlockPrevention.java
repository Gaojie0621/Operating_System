package deadlock_examples;

/**
 * Demonstrates Deadlock Prevention by breaking the "Circular Wait" condition.
 * 
 * Strategy:
 * Impose a total ordering on resources.
 * All threads must acquire locks in the same order (e.g., Lock A then Lock B).
 * 
 * Result:
 * No cycle can exist, so deadlock is impossible.
 */
public class DeadlockPrevention {
    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            // Order: A -> B
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
            // Order: A -> B (SAME ORDER AS T1)
            // Even though T2 might want B first conceptually, it must follow the global order.
            // Or, if it needs both, it must grab A first.
            
            // Note: If T2 only needed B, it would just grab B.
            // But since it needs both A and B, it must grab A first to avoid deadlock with T1.
            
            synchronized (lockA) {
                System.out.println("Thread-2: Holding Lock A...");
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                
                System.out.println("Thread-2: Waiting for Lock B...");
                synchronized (lockB) {
                    System.out.println("Thread-2: Acquired Lock B!");
                }
            }
        });

        t1.start();
        t2.start();
    }
}
