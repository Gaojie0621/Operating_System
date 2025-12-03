package com.example.multithreading.communication;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Demonstrates Inter-thread Communication using wait() and notify().
 * Implements a Producer-Consumer pattern with a shared buffer.
 */
public class ProducerConsumer {
    private static final int CAPACITY = 5;
    private final Queue<Integer> buffer = new LinkedList<>();
    private final Object lock = new Object();

    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            synchronized (lock) {
                while (buffer.size() == CAPACITY) {
                    System.out.println("Buffer full. Producer waiting...");
                    lock.wait();
                }

                System.out.println("Producer produced: " + value);
                buffer.add(value++);

                // Notify consumer that data is available
                lock.notify();

                Thread.sleep(500); // Simulate work
            }
        }
    }

    public void consume() throws InterruptedException {
        while (true) {
            synchronized (lock) {
                while (buffer.isEmpty()) {
                    System.out.println("Buffer empty. Consumer waiting...");
                    lock.wait();
                }

                int value = buffer.poll();
                System.out.println("Consumer consumed: " + value);

                // Notify producer that space is available
                lock.notify();

                Thread.sleep(800); // Simulate work
            }
        }
    }

    public static void main(String[] args) {
        ProducerConsumer pc = new ProducerConsumer();

        Thread producerThread = new Thread(() -> {
            try {
                pc.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumerThread = new Thread(() -> {
            try {
                pc.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producerThread.start();
        consumerThread.start();

        // Let it run for a while then exit
        try {
            Thread.sleep(10000);
            System.out.println("Stopping threads...");
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
