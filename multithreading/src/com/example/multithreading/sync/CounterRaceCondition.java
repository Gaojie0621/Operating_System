package com.example.multithreading.sync;

/**
 * Demonstrates a Race Condition.
 * Multiple threads access a shared resource (counter) without synchronization.
 * The final count will likely be less than expected (20000) because operations
 * are not atomic.
 */
public class CounterRaceCondition {
    private int count = 0;

    public void increment() {
        count++; // Not atomic! Read -> Modify -> Write
    }

    public static void main(String[] args) throws InterruptedException {
        CounterRaceCondition counter = new CounterRaceCondition();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                counter.increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                counter.increment();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Expected count: 20000");
        System.out.println("Actual count: " + counter.count);
        if (counter.count != 20000) {
            System.out.println("Race condition occurred!");
        } else {
            System.out.println("No race condition occurred (lucky run, try again).");
        }
    }
}
