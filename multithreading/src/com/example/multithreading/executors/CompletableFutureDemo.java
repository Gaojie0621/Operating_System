package com.example.multithreading.executors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Demonstrates asynchronous programming using CompletableFuture.
 * Allows chaining of asynchronous operations.
 */
public class CompletableFutureDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("Main thread: " + Thread.currentThread().getName());

        // Run a task asynchronously
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("SupplyAsync running on: " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello";
        });

        // Chain another task to process the result
        CompletableFuture<String> resultFuture = future.thenApply(s -> {
            System.out.println("ThenApply running on: " + Thread.currentThread().getName());
            return s + " World!";
        });

        // Block and get the result (just for demonstration, usually we avoid blocking)
        String result = resultFuture.get();
        System.out.println("Result: " + result);

        // Combining two futures
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 10);
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 20);

        CompletableFuture<Integer> combined = future1.thenCombine(future2, (num1, num2) -> num1 + num2);
        System.out.println("Combined Result: " + combined.get());
    }
}
