package synchronization_examples;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * DiningPhilosophersMonitor.java
 * 
 * Implements the Monitor-based solution (Tanenbaum's solution) to the Dining
 * Philosophers problem.
 * 
 * Key Concepts:
 * - States: THINKING, HUNGRY, EATING.
 * - A philosopher only moves to EATING if both neighbors are NOT EATING.
 * - Uses a global mutex (Lock) to protect state transitions.
 * - Uses an array of Semaphores (one per philosopher) to block if chopsticks
 * are unavailable.
 * 
 * This solution avoids Deadlock but does not guarantee Starvation-freedom.
 */
public class DiningPhilosophersMonitor {

    private static final int N = 5;

    // States
    private static final int THINKING = 0;
    private static final int HUNGRY = 1;
    private static final int EATING = 2;

    private static final int[] state = new int[N];

    // Semaphores for blocking philosophers
    private static final Semaphore[] self = new Semaphore[N];

    // Mutex for protecting critical sections (state array)
    private static final Lock mutex = new ReentrantLock();

    // Helper to get neighbor indices
    private static int left(int i) {
        return (i + N - 1) % N;
    }

    private static int right(int i) {
        return (i + 1) % N;
    }

    // Initialization
    static {
        for (int i = 0; i < N; i++) {
            state[i] = THINKING;
            self[i] = new Semaphore(0); // Initially 0 so they block when calling acquire()
        }
    }

    private static void test(int i) {
        if (state[i] == HUNGRY && state[left(i)] != EATING && state[right(i)] != EATING) {
            state[i] = EATING;
            System.out.println("Philosopher " + i + " picks up chopsticks and starts EATING.");
            self[i].release(); // Unblock the philosopher
        }
    }

    private static void pickup(int i) throws InterruptedException {
        mutex.lock();
        try {
            state[i] = HUNGRY;
            System.out.println("Philosopher " + i + " is HUNGRY.");
            test(i); // Try to eat
        } finally {
            mutex.unlock();
        }
        self[i].acquire(); // Block if unable to eat
    }

    private static void putdown(int i) {
        mutex.lock();
        try {
            state[i] = THINKING;
            System.out.println("Philosopher " + i + " puts down chopsticks and is THINKING.");

            // Check if neighbors can now eat
            test(left(i));
            test(right(i));
        } finally {
            mutex.unlock();
        }
    }

    static class Philosopher extends Thread {
        private int id;

        public Philosopher(int id) {
            this.id = id;
        }

        public void run() {
            try {
                while (true) {
                    // Think
                    Thread.sleep((long) (Math.random() * 1000));

                    pickup(id);

                    // Eat
                    Thread.sleep((long) (Math.random() * 1000));

                    putdown(id);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < N; i++) {
            new Philosopher(i).start();
        }
    }
}
