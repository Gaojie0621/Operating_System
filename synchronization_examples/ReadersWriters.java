package synchronization_examples;

import java.util.concurrent.Semaphore;

/**
 * ReadersWriters.java
 * 
 * Implements the Classical Readers-Writers problem.
 * 
 * Rules:
 * 1. Multiple readers can read simultaneously.
 * 2. Only one writer can write at a time (exclusive access).
 * 3. If a writer is writing, no readers may read.
 * 
 * Solution:
 * Uses a 'read_count' to track active readers.
 * The FIRST reader locks the 'rw_mutex' to block writers.
 * The LAST reader unlocks the 'rw_mutex' to allow writers.
 * Writers lock 'rw_mutex' directly.
 * 'mutex' protects the 'read_count' variable.
 */
public class ReadersWriters {

    // Semaphores
    static Semaphore mutex = new Semaphore(1); // protects read_count
    static Semaphore rw_mutex = new Semaphore(1); // protects the shared resource (exclusive for writers)

    static int read_count = 0;
    static int sharedData = 0;

    static class Reader extends Thread {
        int id;

        public Reader(int id) {
            this.id = id;
        }

        public void run() {
            try {
                while (true) {
                    // Entry Section
                    mutex.acquire();
                    read_count++;
                    if (read_count == 1) {
                        rw_mutex.acquire(); // First reader locks out writers
                    }
                    mutex.release();

                    // Critical Section (Reading)
                    System.out.println("Reader " + id + " is reading data: " + sharedData + " (Active Readers: "
                            + read_count + ")");
                    Thread.sleep(500);

                    // Exit Section
                    mutex.acquire();
                    read_count--;
                    if (read_count == 0) {
                        rw_mutex.release(); // Last reader releases writers
                    }
                    mutex.release();

                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class Writer extends Thread {
        int id;

        public Writer(int id) {
            this.id = id;
        }

        public void run() {
            try {
                while (true) {
                    // Entry Section
                    rw_mutex.acquire(); // Request exclusive access

                    // Critical Section (Writing)
                    sharedData++;
                    System.out.println("Writer " + id + " WROTE data: " + sharedData);
                    Thread.sleep(1000);

                    // Exit Section
                    rw_mutex.release();

                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        // Create Readers and Writers
        for (int i = 1; i <= 3; i++)
            new Reader(i).start();
        for (int i = 1; i <= 2; i++)
            new Writer(i).start();
    }
}
