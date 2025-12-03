package com.example.multithreading.sync;

/**
 * Demonstrates how to fix a Race Condition using 'synchronized'.
 * The 'synchronized' keyword ensures that only one thread can execute the
 * method at a time.
 */
public class SynchronizedCounter {
    private int count = 0;

    // synchronized keyword ensures atomicity
    public synchronized void increment() {
        count++;
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();

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
    }
}
