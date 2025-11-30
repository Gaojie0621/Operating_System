# 1. Motivation: Why Synchronization is Needed

Modern systems run many threads at the same time.
These threads often share data, such as:

- shared variables

- shared buffers

- shared memory regions

- shared files

- OS internal data structures

## Problem: Race Conditions

A race condition occurs when:

Two or more threads/processes read and write **shared data** at the same time,
and the final result depends on the timing of their execution.

This leads to **data inconsistency or corruption**.

**It happens EVEN on single-core CPUs**:
even on single-core systems, the OS may:

- Interrupt a running process

- Context switch to another one → causing overlapping access to shared data.

Synchronization prevents these issues.

### Race Condition Example
```java
counter++:
   r1 = counter
   r1 = r1 + 1
   counter = r1

counter--:
   r2 = counter
   r2 = r2 - 1
   counter = r2
```

If interleaving happens like this:
```
r1 = 5

r1 = 6

... interrupted by counter--
r2 = 5 (outdated value!)

r2 = 4

... counter++ resumes
counter = 6

... counter-- resumes
counter = 4 ← overwritten
```
Final result = 4 (incorrect)

Thus: A correct solution MUST prevent both processes from executing this code simultaneously.

# 2. The Critical-Section Problem
A critical section is **a part of code that accesses shared data**.

The critical-section problem is:
**How can we ensure that only ONE process is in the critical section at a time?**

A correct solution must satisfy three essential properties:

## 1. Mutual Exclusion

If one process is in its critical section, no other process may enter its critical section.

## 2. Progress

If no one is in the critical section and someone wants to enter, the system must allow progress.
Indefinite postponement is not allowed.

## 3. Bounded Waiting

After a process requests entry, there must be a limit on how many others can enter before it.

After a process Pi signals intent to enter, there exists a bound K such that **at most K other entry attempts by other processes** can be granted before Pi is allowed to enter. (This prevents **indefinite postponement**.)

These three rules are the foundation of synchronization theory.

# 3. Mutex Locks
A mutex lock is the simplest software tool for ensuring mutual exclusion.

How it works:
```c
acquire();     // request entry
critical section
release();     // exit
```

Internally, a mutex is a boolean flag:

- `available = true` → lock free

- `available = false` → another process owns it

If a process calls `acquire()` when unavailable, it must wait.

**Note**: The code for acquisition or release of a lock must be atomic.
- They can be implemented using the compare_and_swap() for example.

## Spinlocks

mutexes implemented with `compare_and_swap()` or `test_and_set()` cause **busy waiting**, meaning:

**Disadvantages:**
- The thread loops continuously, checking the lock

- CPU stays at 100% usage

- Wasteful on single-core

**Something Positive:**
- But **fast on multicore** for short waits (no context switching)

# 4. Semaphores
A semaphore is a **synchronization tool** composed of an **integer variable** that is accessed only through 
two atomic operations: `wait()` and `signal()`

Semaphores generalize mutexes:

## Types:

### 1. Binary semaphore

Like a mutex (0/1)

### 2. Counting semaphore

Value initialized to number of resources

## Atomic operations:

- `wait()` — decrement; if < 0, the process waits

- `signal()` — increment; wakes a waiting process

## Better than spinlocks:

Semaphores can avoid busy waiting by:

- Putting the process to `sleep()` when waiting (Solves busy waiting)

- Using a **queue** so waiting is **bounded**

- `signal()` wakes up **FIFO order** → fairness

Semaphores are extremely powerful for solving synchronization problems.

# 5. Liveness Issues
## Liveness
a set of properties that a system must satisfy to ensure that processes make progress. 
- A process waiting indefinitely is an example of “liveness failure”

## Issues
### 1. Deadlock

Two or more processes are waiting for events that will never occur because they depend on each other.

### 2. Priority Inversion

A high-priority process needs a lock held by a low-priority process,
but the low-priority process gets preempted by a medium one.

Thus the highest priority process is blocked by lower ones — a serious liveness failure.

# 6. Classical Synchronization Problems
## 6.1 Bounded-Buffer Problem (Producer–Consumer)
### Goal

Producers place items into a fixed-size circular buffer (capacity N). Consumers remove items. We must:

- Prevent producers overwriting full buffer

- Prevent consumers from removing from empty buffer

- Avoid race conditions on buffer indices and shared counters

### Tools (classic solution)

- `semaphore empty = N` — counts free slots

- `semaphore full = 0` — counts filled slots

- mutex (binary semaphore or pthread mutex) — protects buffer access (head/tail and actual store/load)

All operations on semaphores are **atomic (wait / signal)**.

### Pseudocode (producer / consumer)

**Producer:**
```c
wait(empty);        // wait for an empty slot
wait(mutex);        // enter critical section to modify buffer
    buffer[tail] = item;
    tail = (tail + 1) % N;
signal(mutex);      // leave critical section
signal(full);       // one more filled slot
```

**Consumer:**
```c
wait(full);         // wait for at least one item
wait(mutex);        // enter critical section to modify buffer
    item = buffer[head];
    head = (head + 1) % N;
signal(mutex);      // leave critical section
signal(empty);      // one more empty slot
```
### Why it works (correctness sketch)

1. **Mutual exclusion on buffer indices & access:** mutex ensures only one thread updates head/tail or writes/reads a buffer slot at a time — no concurrent corruption.

2. **Safety (no overrun / underrun):** empty prevents producer when buffer full (empty == 0), full prevents consumer when empty.

3. **Liveness:** If there is both producer and consumer activity possible, one will progress — semaphores move counts monotonically. With fair semaphores (FIFO queue), no starvation; with unfair ones, starvation is possible in pathological scheduling but uncommon in practice.

4. **No if needed:** semaphores implicitly block until the condition (slot available / filled) is true.

## 6.2 Readers–Writers Problem
### Rules:

- Many readers may read simultaneously

- Only ONE writer may write

- Readers and writers cannot access data together

### Tools used:

- mutex — protects `read_count` updates

- rw_mutex — binary semaphore or mutex that grants exclusive access to the dataset (writers hold it, readers collectively hold it)

- `read_count` — number of active readers

We focus on the version where:

Writers should not starve - When a writer wants to write, it should be allowed ASAP

### Correct writer-priority pseudocode
**Reader:**
```c
wait(mutex);
    read_count++;
    if (read_count == 1) wait(rw_mutex);  // first reader locks resource for readers
signal(mutex);

// ----- Reading happens concurrently -----
read_data();

wait(mutex);
    read_count--;
    if (read_count == 0) signal(rw_mutex); // last reader releases resource
signal(mutex);
```

**Writer:**
```c
wait(rw_mutex);    // acquire exclusive access
// ----- Writing happens -----
write_data();
signal(rw_mutex);
```

## 6.3 Dining Philosophers Problem
5 philosophers share 5 chopsticks.
Each philosopher needs two chopsticks (left and right) to eat.

### Naive solution:

Each chopstick = binary semaphore.
→ DEADLOCK, because all may pick up the right chopstick and wait forever for the left.

### Better:

**States:** THINKING, HUNGRY, EATING

Use a **mutex** to protect the **check logic**

A philosopher eats only if **both neighbors are not eating**

**pickup(i) and putdown(i)** operations control access

wait() makes philosophers block (instead of spinning)

- Still NOT starvation-free, But deadlock is avoided.

#### Pseudocode
maintain an array state[i] ∈ {THINKING, HUNGRY, EATING} and a semaphore S[i] for each philosopher to block/unblock them; a global mutex protects state updates and checks.
```c
state[N];           // THINKING, HUNGRY, EATING
semaphore S[N];     // initialized to 0 for blocking
mutex mutex = 1;    // protect state array

void test(int i) {
    if (state[i] == HUNGRY && state[left(i)] != EATING && state[right(i)] != EATING) {
        state[i] = EATING;
        signal(S[i]);   // wake up philosopher i if it was waiting
    }
}

void pickup(int i) {
    wait(mutex);
    state[i] = HUNGRY;
    test(i);            // try to acquire two chopsticks
    signal(mutex);
    wait(S[i]);         // block if not granted (if still HUNGRY)
}

void putdown(int i) {
    wait(mutex);
    state[i] = THINKING;
    test(left(i));      // check if left neighbor can eat now
    test(right(i));     // check if right neighbor can eat now
    signal(mutex);
}
```
#### Why it works

- test(i) only sets state[i] = EATING and signal(S[i]) when both neighbors are not EATING. Thus mutual exclusion of neighbors is preserved.

- mutex serializes the checks and updates of the state array, preventing races.

- pickup() blocks (via S[i]) until test(i) is called when it's safe to eat.

- The test(left) / test(right) calls in putdown() wake up waiting neighbors when they become feasible, ensuring progress.

#### Starvation
This solution avoids deadlock but does not guarantee starvation-freedom in all scheduling models: a philosopher might be overtaken repeatedly by others if test and wake-ups favor others, depending on scheduling order. In practice, starvation is rare but theoretically possible. To guarantee no starvation, you need additional fairness (e.g., queue of waiting philosophers or ticketing).

