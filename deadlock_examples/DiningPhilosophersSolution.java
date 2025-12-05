package deadlock_examples;

/**
 * Demonstrates the Solution to Dining Philosophers using Resource Hierarchy.
 * 
 * Strategy:
 * Assign a global order to resources (Chopsticks 0..4).
 * Rule: Always pick up the LOWER numbered chopstick first.
 * 
 * Why it works:
 * - Philosophers 0..3 pick up Left (i) then Right (i+1).
 * - Philosopher 4 would normally pick up Left (4) then Right (0).
 * - BUT, following the rule, Philosopher 4 must pick up 0 (Right) then 4
 * (Left).
 * 
 * This breaks the circular dependency because P4 is competing for Chopstick 0
 * with P0.
 * One of them will get it, preventing the cycle where everyone holds Left and
 * waits for Right.
 */
public class DiningPhilosophersSolution {
    static class Chopstick {
        private final int id;

        public Chopstick(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Chopstick-" + id;
        }
    }

    static class Philosopher extends Thread {
        private final int id;
        private final Chopstick firstChopstick;
        private final Chopstick secondChopstick;

        public Philosopher(int id, Chopstick left, Chopstick right) {
            this.id = id;
            // Resource Hierarchy Logic:
            if (left.id < right.id) {
                this.firstChopstick = left;
                this.secondChopstick = right;
            } else {
                this.firstChopstick = right;
                this.secondChopstick = left;
            }
        }

        private void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " is thinking...");
            Thread.sleep((long) (Math.random() * 100));
        }

        private void eat() throws InterruptedException {
            System.out.println("Philosopher " + id + " is hungry.");

            synchronized (firstChopstick) {
                System.out.println("Philosopher " + id + " picked up " + firstChopstick + " (First)");

                Thread.sleep(10); // Small delay

                synchronized (secondChopstick) {
                    System.out.println(
                            "Philosopher " + id + " picked up " + secondChopstick + " (Second) and is EATING.");
                    Thread.sleep((long) (Math.random() * 100));
                }
            }
            System.out.println("Philosopher " + id + " put down chopsticks and finished eating.");
        }

        @Override
        public void run() {
            try {
                while (true) {
                    think();
                    eat();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        int numPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numPhilosophers];
        Chopstick[] chopsticks = new Chopstick[numPhilosophers];

        for (int i = 0; i < numPhilosophers; i++) {
            chopsticks[i] = new Chopstick(i);
        }

        for (int i = 0; i < numPhilosophers; i++) {
            Chopstick left = chopsticks[i];
            Chopstick right = chopsticks[(i + 1) % numPhilosophers];

            // The Philosopher constructor handles the ordering logic
            philosophers[i] = new Philosopher(i, left, right);
            philosophers[i].start();
        }
    }
}
