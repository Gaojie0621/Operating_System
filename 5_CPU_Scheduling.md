# Lecture 4 Summary — CPU Scheduling

## 1. Why CPU Scheduling?
**CPU scheduling is the method by which the operating system decides which ready process (or thread) gets to use the CPU next.**
- CPU and I/O bursts alternate; processes frequently wait for I/O.
    - When a process is waiting for I/O: 
    it cannot use the CPU. The CPU would be idle (wasted time)
- Keeping several process in memory, when a process has to wait, the OS gives the CPU to another process in memory Ready to run.
    - The selection process is carried out by the **CPU scheduler**

**Objectives:**
  - Maximize CPU utilization
  - Increase throughput
  - Minimize waiting and turnaround time
  - Improve responsiveness (interactive systems)



## 2. When Scheduling Occurs
#### 1. Running → Waiting 
Example: I/O request, waiting for child process.
#### 2. Running → Ready 
A timer interrupt occurs; CPU is taken away(preemption)
#### 3. Waiting → Ready 
I/O completes, process becomes ready.
#### 4. Terminated
Process finishes; OS must choose a new one.

![](./images/5_1.png)

### Preemptive vs Non-Preemptive
#### Preemptive: 
OS can interrupt a running process.(use timer interrupts).

Modern OSes use preemption.

⟶ Requires locks & synchronization to avoid data corruption(e.g. race conditions).

#### Non-preemptive: 
The OS cannot force a running process to give up the CPU.

- Only schedules at events (1) and (4).
- Safe, simple, no race conditions — but poor responsiveness.

---

## 3. Dispatcher
- Performs context switching.
- Responsibilities: save/load context, switch to user mode, jump to correct instruction.

**Dispatcher latency** ：
Time it takes to perform a context switch.
- Must be small — too high = CPU is wasted doing overhead work.

![](./images/5_2.png)



## 4. Scheduling Criteria
- **CPU Utilization** : Keep CPU as busy as possible (0–100%).
- **Throughput** : Completed processes per time unit.
- **Turnaround Time** : Time from process arrival → completion.
    - Turnaround = Waiting Time + CPU Time + I/O Time
- **Waiting Time** : Time spent in Ready queue.
    - This is the ONLY metric controlled directly by scheduling algorithms.
- **Response Time** : Time from request → first response (important for interactive systems).



## 5. Scheduling Algorithms
**Deciding which process in the Ready queue 
receives CPU time**
### 1. First-Come, First-Served (FCFS)
- Non-preemptive.
- Simple FIFO.
- **Convoy effect**: long jobs delay short ones. 
    - All short jobs stuck behind long jobs.
    - Average waiting time can be very high.
    - Bad for interactive systems.

![](./images/5_3.png)

### 2. Shortest Job First (SJF)
- Uses predicted CPU burst.
    - One of the disadvantage of SJF is that there is no way to know the length of the next CPU burst.
    - Must predict with an exponential average formula.
- Optimal for minimizing average waiting time.
- Versions:
  - **Non-preemptive**: Assigned when the CPU is available
  ![](./images/5_4.png)

  - **Preemptive SRTF** (Shortest Remaining Time First): Assigned when a new process arrives to Ready 
  ![](./images/5_5.png)



### 3. Round Robin (RR)
Preemptive with **time quantum** q.
    - q is generally between 10 and 100 milliseconds.

**Behavior:**
- If process finishes before q → CPU released
- If not → CPU is taken away, process is re-enqueued.
- The CPU scheduler uses the **Timer interrupt** to gain control

![](./images/5_6.png)

**Advantages:**
- Fair; good for interactive use.
- Prevents starvation.
- Favours short processes (without needing to know burst time)

**Disadvantages:**
- Too small q → overhead; too large q → FCFS.

**Rule of Thumb:** Quantum should be greater than 80% of CPU bursts.

### 4. Priority Scheduling
A priority is associated with each process. The CPU goes to the highest priority
- The shortest-job-first is a special case of this algorithm

Priorities can be defined:
1. **Internally:** based on some measurable quantities (number of open files, memory requirements, ratio CPU and I/O bursts.)
2. **Externally:** importance of the process (more payment, important user…)

- Higher priority = runs first.
- Can be preemptive or non-preemptive.
- **Starvation** problem :Low priority processes may never run.
    - fixed by **aging**: Gradually increase waiting process priority.

![](./images/5_7.png)

### 5. Multilevel Queue
**Use multiple Ready queues, each with its own priority.**
- Each queue has its own scheduling method (e.g., RR, FCFS)

- Scheduling between queues uses fixed priority

- Processes stay in the same queue forever

![](./images/5_8.png)

### 6. Multilevel Feedback Queue
Similar to the Multilevel Queue Scheduling, but it **allows a process to move between queues.**

**General Idea:**
- CPU-heavy tasks should move down

- I/O-heavy (waiting long) tasks should move up

- Prevent starvation

- Improve responsiveness

**Most flexible and complex**
- Supports aging, avoids starvation.
- Must define:

    - Number of queues

    - Scheduling method for each queue

    - How to upgrade or downgrade processes

    - Default queue for new processes


## 6. Multiprocessor Scheduling

### Asymmetric Multiprocessing (AMP)
One CPU is the **master scheduler**; others execute tasks.
- Only one core accesses system data: Simple, no shared data issues
- Master CPU becomes bottleneck → not scalable

### Symmetric Multiprocessing (SMP)
Each CPU is self-scheduling.
**Approaches:**
1. **Single ready queue** 
    Simple, but:
    - Shared queue → race conditions

    - Locking overhead → bottleneck

2. **Per-CPU queues** 
    Pros:

    - Cache efficiency (better locality)

    - No bottleneck

    Cons:

    - Load imbalance possible (one CPU idle while others busy)



## 7. Multicore Considerations
### Multicore Processors
#### 1. Multicore
 multiple computer cores are placed on the same chip
-  They are faster and consume less power than each core on a chip.

- **Memory Stall**: When a processor access memory, it has to wait significant time until the data is available. 
    -  It happens because CPU speed is much faster than memory.
    - CPU waits for memory; multithreading helps.

![](./images/5_12.png)

#### 2. Multithreading (logical CPUs):
Multithread: two or more hardware threads are assigned to each core.
![](./images/5_9.png)

- Each core supports multiple hardware threads. Each HW thread has its state (register, instruction pointer), thus, OS sees each as a **“logical CPU”**.
![](./images/5_10.png)

Still, a core can only execute one HW thread at a time.
**Two levels of decisions:**
1. Level 1: OS decides the thread to run on logical CPU (SW thread)
2. Level 2: a core decides which logical CPU to run (HW thread)
![](./images/5_11.png)


## 8. Load Balancing
Prevents CPUs from being idle while others are busy.
- **Push migration**: A busy CPU pushes threads to idle CPU.
- **Pull migration**: An idle CPU pulls threads from busy one.

Often combined.


## 9. Processor Affinity
Threads prefer to stay on the same CPU they executed on.

**Definition:**
Trying to keep a thread running on the same processor and take advantage of “warm cache”.
- Warm cache: Data recently accessed by a thread populate the cache. Successive memory access have higher chances of finding the data in the cache.

**Benefit:** Improves cache performance.

If a thread migrates processor due to load balancing:
- Invalidate cache of its previous processor
- Populate cache of its new processor

**Types:**
- **Soft affinity**: attempt to keep thread on same CPU
- **Hard affinity**: OS restricts thread to specific CPUs only

**NUMA effects (non-uniform memory access)** :
Affinity is more important because accessing remote memory is slower.

---

## 10. NUMA Awareness
- Each CPU/node has its own local memory.
- Local memory access is faster than remote.
- OS tries to keep process + memory on same node.


