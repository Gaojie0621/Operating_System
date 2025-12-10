package synchronization_examples;

import java.util.concurrent.Semaphore;

/**
 * ProducerConsumerSemaphore.java
 * 
 * Implements the Bounded-Buffer Problem (Producer-Consumer).
 * 
 * Components:
 * - Circular Buffer of size N
 * - Producers: Generate data and put into buffer.
 * - Consumers: Remove data from buffer.
 * 
 * Synchronization Primitives:
 * - Semaphore empty (initialized to N): Counts empty slots.
 * - Semaphore full (initialized to 0): Counts filled slots.
 * - Semaphore mutex (initialized to 1): Protects index manipulation.
 */
public class ProducerConsumerSemaphore {

    private static final int BUFFER_SIZE = 5;
    private static final int[] buffer = new int[BUFFER_SIZE];
    private static int head = 0; // Index to take from (Consumer)
    private static int tail = 0; // Index to put into (Producer)

    // Semaphores
    private static final Semaphore empty = new Semaphore(BUFFER_SIZE);
    private static final Semaphore full = new Semaphore(0);
    private static final Semaphore mutex = new Semaphore(1);

    static class Producer extends Thread {
        public void run() {
            try {
                while (true) {
                    int item = (int) (Math.random() * 100); // Produce item

                    empty.acquire(); // Wait for empty slot
                    mutex.acquire(); // Enter critical section

                    buffer[tail] = item;
                    System.out.println("Producer produced: " + item + " at index " + tail);
                    tail = (tail + 1) % BUFFER_SIZE;

                    mutex.release(); // Leave critical section
                    full.release(); // Signal a full slot

                    Thread.sleep((long) (Math.random() * 1000));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Consumer extends Thread {
        public void run() {
            try {
                while (true) {
                    full.acquire(); // Wait for full slot
                    mutex.acquire(); // Enter critical section

                    int item = buffer[head];
                    System.out.println("Consumer consumed: " + item + " from index " + head);
                    head = (head + 1) % BUFFER_SIZE;

                    mutex.release(); // Leave critical section
                    empty.release(); // Signal an empty slot

                    Thread.sleep((long) (Math.random() * 1000));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        new Producer().start();
        new Consumer().start();
        // You can add more producers/consumers here
    }
}
