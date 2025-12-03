package com.example.multithreading.basics;

/**
 * Demonstrates different ways to create and start threads in Java.
 */
public class BasicThreadCreation {

    public static void main(String[] args) {
        System.out.println("Main thread starting: " + Thread.currentThread().getName());

        // 1. Extending the Thread class
        Thread thread1 = new MyThread();
        thread1.setName("Worker-Thread");
        thread1.start();

        // 2. Implementing the Runnable interface
        Runnable myRunnable = new MyRunnable();
        Thread thread2 = new Thread(myRunnable, "Runnable-Worker");
        thread2.start();

        // 3. Using Lambda Expression (Functional Interface) - The modern way
        Thread thread3 = new Thread(() -> {
            System.out.println("Lambda thread running: " + Thread.currentThread().getName());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Lambda thread finished: " + Thread.currentThread().getName());
        }, "Lambda-Worker");
        thread3.start();

        System.out.println("Main thread finished.");
    }
}

class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("MyThread running: " + Thread.currentThread().getName());
        try {
            Thread.sleep(1000); // Simulate some work
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("MyThread finished: " + Thread.currentThread().getName());
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("MyRunnable running: " + Thread.currentThread().getName());
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("MyRunnable finished: " + Thread.currentThread().getName());
    }
}
