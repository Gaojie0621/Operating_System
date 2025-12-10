package synchronization_examples;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MutexSolution.java
 * 
 * Solves the Race Condition using a Mutex Lock (ReentrantLock in Java).
 * 
 * Concept:
 * Before accessing the shared 'counter', a thread must ACQUIRE the lock.
 * After modifying 'counter', the thread must RELEASE the lock.
 * 
 * This ensures "Mutual Exclusion": Only one thread acts on the critical section
 * at a time.
 */
public class MutexSolution {

    static int counter = 0;
    static final int ITERATIONS = 100000;

    // Mutex Lock
    static Lock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                lock.lock(); // acquire()
                try {
                    counter++; // critical section
                } finally {
                    lock.unlock(); // release()
                }
            }
        }, "Thread-Increment");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                lock.lock(); // acquire()
                try {
                    counter--; // critical section
                } finally {
                    lock.unlock(); // release()
                }
            }
        }, "Thread-Decrement");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Expected counter: 0");
        System.out.println("Actual counter:   " + counter);
        System.out.println("Result is consistent due to Mutual Exclusion.");
    }
}
