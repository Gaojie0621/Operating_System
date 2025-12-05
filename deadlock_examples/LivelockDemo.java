package deadlock_examples;

/**
 * Demonstrates Livelock.
 * 
 * Scenario:
 * A husband and wife are trying to eat soup, but there is only one spoon.
 * They are too polite: if the other person is hungry, they pass the spoon.
 * Result: They keep passing the spoon back and forth forever, and no one eats.
 * 
 * Unlike Deadlock, threads are NOT blocked. They are actively changing state.
 */
public class LivelockDemo {
    static class Spoon {
        private Diner owner;

        public Spoon(Diner owner) {
            this.owner = owner;
        }

        public synchronized void setOwner(Diner d) {
            owner = d;
        }

        public synchronized void use() {
            System.out.printf("%s has eaten!%n", owner.name);
        }
    }

    static class Diner {
        private String name;
        private boolean isHungry;

        public Diner(String n) {
            this.name = n;
            this.isHungry = true;
        }

        public void eatWith(Spoon spoon, Diner spouse) {
            while (isHungry) {
                // Don't have the spoon, so wait patiently for spouse.
                if (spoon.owner != this) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        continue;
                    }
                    continue;
                }

                // If spouse is hungry, insist on passing the spoon.
                if (spouse.isHungry) {
                    System.out.printf("%s: You eat first my darling %s!%n", name, spouse.name);
                    spoon.setOwner(spouse);
                    continue;
                }

                // Spouse wasn't hungry, so finally eat.
                spoon.use();
                isHungry = false;
                System.out.printf("%s: I am full now!%n", name);
                spoon.setOwner(spouse);
            }
        }
    }

    public static void main(String[] args) {
        Diner husband = new Diner("Husband");
        Diner wife = new Diner("Wife");

        Spoon s = new Spoon(husband);

        new Thread(() -> husband.eatWith(s, wife)).start();
        new Thread(() -> wife.eatWith(s, husband)).start();
    }
}
