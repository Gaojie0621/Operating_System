package com.example.multithreading.sync;

/**
 * Demonstrates a Deadlock.
 * Thread 1 holds Lock A and waits for Lock B.
 * Thread 2 holds Lock B and waits for Lock A.
 * Both wait forever.
 */
public class DeadlockDemo {
    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("Thread 1: Holding lock 1...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                /**
                 * Thread 1 wakes up and tries to enter synchronized (lock2).
                 *  Problem: It cannot enter because Thread 2 is already holding Lock B.
                 *  Result: Thread 1 waits...
                 */
                System.out.println("Thread 1: Waiting for lock 2...");
                synchronized (lock2) {
                    System.out.println("Thread 1: Holding lock 1 & 2");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("Thread 2: Holding lock 2...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                /**
                 * Thread 2 wakes up and tries to enter synchronized (lock1).
                 *  Problem: It cannot enter because Thread 1 is already holding Lock A.
                 *  Result: Thread 2 waits...
                 */
                System.out.println("Thread 2: Waiting for lock 1...");
                synchronized (lock1) {
                    System.out.println("Thread 2: Holding lock 2 & 1");
                }
            }
        });

        t1.start();
        t2.start();

        // Note: This program will hang (deadlock). You will need to terminate it
        // manually.
        // In a real scenario, you would use tools like jstack to analyze the deadlock.
    }
}
