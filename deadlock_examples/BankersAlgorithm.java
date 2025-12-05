package deadlock_examples;

import java.util.Arrays;

/**
 * Demonstrates Deadlock Avoidance using the Banker's Algorithm.
 * 
 * Strategy:
 * Before granting a request, the OS simulates the allocation.
 * It checks if the system will remain in a "Safe State".
 * A state is safe if there exists a sequence of processes such that
 * each process can finish with the currently available resources + resources
 * held by previous processes in the sequence.
 */
public class BankersAlgorithm {
    private int numProcesses;
    private int numResources;

    private int[] available; // Available instances of each resource
    private int[][] maximum; // Max demand of each process
    private int[][] allocation; // Currently allocated resources
    private int[][] need; // Remaining need (Max - Allocation)

    public BankersAlgorithm(int numProcesses, int numResources) {
        this.numProcesses = numProcesses;
        this.numResources = numResources;

        available = new int[numResources];
        maximum = new int[numProcesses][numResources];
        allocation = new int[numProcesses][numResources];
        need = new int[numProcesses][numResources];
    }

    public void setAvailable(int[] available) {
        this.available = available;
    }

    public void setProcessInfo(int processId, int[] max, int[] alloc) {
        maximum[processId] = max;
        allocation[processId] = alloc;
        for (int i = 0; i < numResources; i++) {
            need[processId][i] = max[i] - alloc[i];
        }
    }

    // Check if the system is in a safe state
    public boolean isSafeState() {
        int[] work = Arrays.copyOf(available, numResources);
        boolean[] finish = new boolean[numProcesses];
        int count = 0;

        while (count < numProcesses) {
            boolean found = false;
            for (int p = 0; p < numProcesses; p++) {
                if (!finish[p]) {
                    // Check if Need[p] <= Work
                    int j;
                    for (j = 0; j < numResources; j++) {
                        if (need[p][j] > work[j]) {
                            break;
                        }
                    }

                    // If all needs can be satisfied
                    if (j == numResources) {
                        // "Grant" resources, let process finish, and return resources
                        for (int k = 0; k < numResources; k++) {
                            work[k] += allocation[p][k];
                        }
                        finish[p] = true;
                        found = true;
                        count++;
                        // System.out.println("Process P" + p + " can finish.");
                    }
                }
            }

            if (!found) {
                // No process can finish
                return false;
            }
        }
        return true;
    }

    public boolean requestResources(int processId, int[] request) {
        // 1. Check if Request <= Need
        for (int i = 0; i < numResources; i++) {
            if (request[i] > need[processId][i]) {
                System.out.println("Error: Process has exceeded its maximum claim.");
                return false;
            }
        }

        // 2. Check if Request <= Available
        for (int i = 0; i < numResources; i++) {
            if (request[i] > available[i]) {
                System.out.println("Process P" + processId + " must wait (not enough resources).");
                return false;
            }
        }

        // 3. Pretend to allocate
        for (int i = 0; i < numResources; i++) {
            available[i] -= request[i];
            allocation[processId][i] += request[i];
            need[processId][i] -= request[i];
        }

        // 4. Check safety
        if (isSafeState()) {
            System.out.println("Request granted for P" + processId + ". System is in SAFE state.");
            return true;
        } else {
            System.out.println("Request DENIED for P" + processId + ". System would be UNSAFE.");
            // Rollback
            for (int i = 0; i < numResources; i++) {
                available[i] += request[i];
                allocation[processId][i] -= request[i];
                need[processId][i] += request[i];
            }
            return false;
        }
    }

    public static void main(String[] args) {
        // Example: 5 Processes, 3 Resource Types (A, B, C)
        BankersAlgorithm banker = new BankersAlgorithm(5, 3);

        // Total Resources in System: A=10, B=5, C=7
        // Currently Available: A=3, B=3, C=2
        banker.setAvailable(new int[] { 3, 3, 2 });

        // Process 0: Max=[7,5,3], Alloc=[0,1,0]
        banker.setProcessInfo(0, new int[] { 7, 5, 3 }, new int[] { 0, 1, 0 });

        // Process 1: Max=[3,2,2], Alloc=[2,0,0]
        banker.setProcessInfo(1, new int[] { 3, 2, 2 }, new int[] { 2, 0, 0 });

        // Process 2: Max=[9,0,2], Alloc=[3,0,2]
        banker.setProcessInfo(2, new int[] { 9, 0, 2 }, new int[] { 3, 0, 2 });

        // Process 3: Max=[2,2,2], Alloc=[2,1,1]
        banker.setProcessInfo(3, new int[] { 2, 2, 2 }, new int[] { 2, 1, 1 });

        // Process 4: Max=[4,3,3], Alloc=[0,0,2]
        banker.setProcessInfo(4, new int[] { 4, 3, 3 }, new int[] { 0, 0, 2 });

        System.out.println("Initial State Safety Check: " + (banker.isSafeState() ? "SAFE" : "UNSAFE"));

        // Test Request: P1 requests [1, 0, 2]
        System.out.println("\nP1 requests [1, 0, 2]...");
        banker.requestResources(1, new int[] { 1, 0, 2 });

        // Test Request: P4 requests [3, 3, 0] -> Should be denied (unsafe)
        System.out.println("\nP4 requests [3, 3, 0]...");
        banker.requestResources(4, new int[] { 3, 3, 0 });
    }
}
