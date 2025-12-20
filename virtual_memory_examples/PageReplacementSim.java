package virtual_memory_examples;

import java.util.*;

/**
 * PageReplacementSim.java
 * 
 * Simulates standard Page Replacement Algorithms:
 * 1. FIFO (First-In-First-Out)
 * 2. LRU (Least Recently Used)
 * 3. OPT (Optimal - requires knowing future)
 */
public class PageReplacementSim {

    public static void main(String[] args) {
        // Standard reference string from many OS textbooks
        int[] referenceString = { 7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1 };
        int frameCount = 3;

        System.out.println("Reference String: " + Arrays.toString(referenceString));
        System.out.println("Frame Count: " + frameCount);
        System.out.println("--------------------------------------------------");

        int faultsFIFO = runFIFO(referenceString, frameCount);
        System.out.println("FIFO Faults:    " + faultsFIFO);

        int faultsLRU = runLRU(referenceString, frameCount);
        System.out.println("LRU Faults:     " + faultsLRU);

        int faultsOPT = runOptimal(referenceString, frameCount);
        System.out.println("Optimal Faults: " + faultsOPT);
        System.out.println("--------------------------------------------------");
    }

    // --- FIFO Algorithm ---
    private static int runFIFO(int[] references, int frameCount) {
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> frames = new HashSet<>();
        int faults = 0;

        for (int page : references) {
            if (!frames.contains(page)) {
                faults++;
                if (frames.size() == frameCount) {
                    int victim = queue.poll();
                    frames.remove(victim);
                }
                frames.add(page);
                queue.offer(page);
            }
        }
        return faults;
    }

    // --- LRU Algorithm ---
    private static int runLRU(int[] references, int frameCount) {
        // LinkedHashMap with accessOrder=true acts as LRU cache
        // However, here we just need to track the order manually or use a list
        LinkedList<Integer> lruList = new LinkedList<>();
        Set<Integer> frames = new HashSet<>();
        int faults = 0;

        for (int page : references) {
            if (!frames.contains(page)) {
                faults++;
                if (frames.size() == frameCount) {
                    // Remove Least Recently Used (head of list)
                    int victim = lruList.removeFirst();
                    frames.remove(victim);
                }
                frames.add(page);
                lruList.addLast(page); // Recently used -> end of list
            } else {
                // Page exists, update its position (make it most recently used)
                lruList.remove((Integer) page);
                lruList.addLast(page);
            }
        }
        return faults;
    }

    // --- Optimal Algorithm ---
    private static int runOptimal(int[] references, int frameCount) {
        Set<Integer> frames = new HashSet<>();
        int faults = 0;

        for (int i = 0; i < references.length; i++) {
            int page = references[i];
            if (!frames.contains(page)) {
                faults++;
                if (frames.size() == frameCount) {
                    int victim = -1;
                    int furthestUse = -1;

                    for (int frame : frames) {
                        int nextUse = Integer.MAX_VALUE;
                        // finding next use
                        for (int j = i + 1; j < references.length; j++) {
                            if (references[j] == frame) {
                                nextUse = j;
                                break;
                            }
                        }

                        if (nextUse > furthestUse) {
                            furthestUse = nextUse;
                            victim = frame;
                        }
                    }
                    frames.remove(victim);
                }
                frames.add(page);
            }
        }
        return faults;
    }
}
