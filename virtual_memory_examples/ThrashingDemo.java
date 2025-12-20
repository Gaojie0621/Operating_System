package virtual_memory_examples;

import java.util.ArrayList;
import java.util.List;

/**
 * ThrashingDemo.java
 * 
 * Simulates the concept of "Thrashing".
 * 
 * Scenario:
 * - We have a fixed amount of Physical Memory (totalFrames).
 * - We add processes, each requiring a certain "Working Set" of frames to run
 * efficiently.
 * - If (Total Working Set > Total Frames), the system starts "Thrashing".
 * - In this simulation, this is modeled by adding a massive delay to each
 * "instruction".
 * 
 * Output:
 * - Shows System Throughput (Instructions per Second) as we add more processes.
 * - You will see throughput rise (good multiprogramming) and then crash
 * (thrashing).
 */
public class ThrashingDemo {

    static final int TOTAL_FRAMES = 100; // Total physical memory
    static final int FRAMES_PER_PROCESS = 25; // Each process needs this many frames to be happy
    static final int INSTRUCTION_COUNT = 1000;

    static class ProcessSim {
        int id;
        boolean isThrashing = false;

        public ProcessSim(int id) {
            this.id = id;
        }

        public void run() {
            try {
                // Simulate execution
                for (int i = 0; i < INSTRUCTION_COUNT; i++) {
                    if (isThrashing) {
                        // High penalty for paging!
                        Thread.sleep(1);
                    } else {
                        // Fast execution (CPU bound)
                        // Thread.sleep(0); // practically instant
                        Math.sin(i); // do some work
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Total System Frames: " + TOTAL_FRAMES);
        System.out.println("Frames needed per Process: " + FRAMES_PER_PROCESS);
        System.out.println("----------------------------------------------------------------");

        // We will run tests with 1, 2, 3, 4, 5, 6 processes
        for (int numProcesses = 1; numProcesses <= 6; numProcesses++) {
            runSimulation(numProcesses);
        }
    }

    private static void runSimulation(int numProcesses) {
        int totalNeeded = numProcesses * FRAMES_PER_PROCESS;
        boolean thrashing = totalNeeded > TOTAL_FRAMES;

        List<ProcessSim> processes = new ArrayList<>();
        for (int i = 0; i < numProcesses; i++) {
            ProcessSim p = new ProcessSim(i);
            p.isThrashing = thrashing; // In a real OS, global replacement would cause EVERYONE to suffer
            processes.add(p);
        }

        long start = System.currentTimeMillis();

        // Run all sequentially for simple throughput measurement (or parallel)
        // Parallel is more realistic for "System Throughput" equivalent
        processes.parallelStream().forEach(ProcessSim::run);

        long end = System.currentTimeMillis();
        long duration = Math.max(1, end - start);
        double throughput = (double) (numProcesses * INSTRUCTION_COUNT) / duration;

        System.out
                .println(String.format("Processes: %d | Total Needed: %3d | Status: %-10s | Throughput: %8.2f instr/ms",
                        numProcesses, totalNeeded, (thrashing ? "THRASHING" : "OK"), throughput));

        if (thrashing) {
            System.out.println("  -> System over-committed! Spending time paging instead of working.");
        }
    }
}
