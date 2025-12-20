package virtual_memory_examples;

/**
 * DataLocalityDemo.java
 * 
 * Demonstrates the impact of Data Locality (Spatial Locality) on performance.
 * 
 * Concept:
 * - Row-major traversal (accessing data[0][0], data[0][1], ...) is
 * cache-friendly
 * because data is likely pre-fetched and in the same cache line (conceptually).
 * - Column-major traversal (accessing data[0][0], data[1][0], ...) causes
 * frequent
 * cache misses because memory addresses are far apart (stride is large).
 * 
 * Although Java arrays of arrays are not guaranteed to be contiguous like C
 * arrays,
 * simulating row-major vs column-major usually still shows a significant
 * difference
 * due to how the JVM lays out objects and CPU prefetching.
 */
public class DataLocalityDemo {

    private static final int ROWS = 10000;
    private static final int COLS = 10000;
    private static final int[][] matrix = new int[ROWS][COLS];

    public static void main(String[] args) {
        System.out.println("Initializing " + ROWS + "x" + COLS + " matrix...");
        // Initialize simple data
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                matrix[i][j] = i + j;
            }
        }

        System.out.println("Starting Row-Major Traversal (Cache Friendly)...");
        long start = System.nanoTime();
        long sumRow = rowMajorTraversal();
        long end = System.nanoTime();
        double rowDuration = (end - start) / 1_000_000.0;
        System.out.println("Row-Major Time:    " + String.format("%.2f", rowDuration) + " ms");

        System.out.println("Starting Column-Major Traversal (Cache Unfriendly)...");
        start = System.nanoTime();
        long sumCol = colMajorTraversal();
        end = System.nanoTime();
        double colDuration = (end - start) / 1_000_000.0;
        System.out.println("Column-Major Time: " + String.format("%.2f", colDuration) + " ms");

        System.out.println("\n--- Analysis ---");
        System.out.println("Ratio (Col / Row): " + String.format("%.2f", colDuration / rowDuration) + "x slower");
        if (sumRow != sumCol) {
            System.err.println("Error: Sums do not match! (Should not happen)");
        }
    }

    private static long rowMajorTraversal() {
        long sum = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                sum += matrix[i][j];
            }
        }
        return sum;
    }

    private static long colMajorTraversal() {
        long sum = 0;
        for (int j = 0; j < COLS; j++) {
            for (int i = 0; i < ROWS; i++) {
                sum += matrix[i][j];
            }
        }
        return sum;
    }
}
