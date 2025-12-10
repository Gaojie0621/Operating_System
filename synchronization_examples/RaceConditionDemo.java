package synchronization_examples;

/**
 * RaceConditionDemo.java
 * 
 * Demonstrates a Race Condition where two threads access a shared variable
 * (counter)
 * without synchronization.
 * 
 * Concept:
 * Thread 1 does: counter++ (read, modify, write)
 * Thread 2 does: counter-- (read, modify, write)
 * 
 * Expected Result: 0 (if ops cancel out perfectly)
 * Actual Result: Unpredictable (often non-zero) because steps interleave.
 */
public class RaceConditionDemo {

    // Shared resource
    static int counter = 0;
    static final int ITERATIONS = 100000;

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                counter++;
            }
        }, "Thread-Increment");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                counter--;
            }
        }, "Thread-Decrement");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Expected counter: 0");
        System.out.println("Actual counter:   " + counter);

        if (counter != 0) {
            System.out.println("Race condition occurred! (Data inconsistency)");
        } else {
            System.out.println("No race condition observed (Lucky run or optimized out).");
        }
    }
}
