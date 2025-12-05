package deadlock_examples;

/**
 * Demonstrates the Dining Philosophers Problem (Deadlock Scenario).
 * 
 * Scenario:
 * 5 Philosophers sit around a table.
 * There is 1 chopstick between each pair.
 * To eat, a philosopher needs BOTH the left and right chopsticks.
 * 
 * Deadlock Condition:
 * If every philosopher picks up their LEFT chopstick at the same time,
 * they will all wait forever for their RIGHT chopstick.
 */
public class DiningPhilosophersDeadlock {
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
        private final Chopstick leftChopstick;
        private final Chopstick rightChopstick;

        public Philosopher(int id, Chopstick left, Chopstick right) {
            this.id = id;
            this.leftChopstick = left;
            this.rightChopstick = right;
        }

        private void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " is thinking...");
            Thread.sleep((long) (Math.random() * 100));
        }

        private void eat() throws InterruptedException {
            System.out.println("Philosopher " + id + " is hungry.");

            // Naive Strategy: Pick up Left, then Right
            synchronized (leftChopstick) {
                System.out.println("Philosopher " + id + " picked up " + leftChopstick + " (Left)");

                // Artificial delay to ensure deadlock happens easily
                Thread.sleep(50);

                synchronized (rightChopstick) {
                    System.out
                            .println("Philosopher " + id + " picked up " + rightChopstick + " (Right) and is EATING.");
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

            // Everyone picks up Left then Right
            philosophers[i] = new Philosopher(i, left, right);
            philosophers[i].start();
        }
    }
}
