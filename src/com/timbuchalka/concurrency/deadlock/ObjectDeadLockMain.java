package com.timbuchalka.concurrency.deadlock;

/**
 * To avoid Deadlock the methods must be refactored to follow the same order when acquiring locks
 * Therefore avoiding locks from other threads that it depends.
 */
public class ObjectDeadLockMain {

    public static Object lock1 = new Object();
    public static Object lock2 = new Object();

    public static void main(String[] args) {
        new Thread1().start();
        new Thread2().start();
    }

    private static class Thread1 extends Thread {

        public void run() {
            synchronized (lock1) {
                System.out.println("Thread 1: Has lock1");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
                System.out.println("Thread 1: Waiting for lock 2");
                synchronized (lock2) {
                    System.out.println("Thread 1: Has lock1 and lock2");
                }
                System.out.println("Thread 1: Released lock2");
            }
            System.out.println("Thread 1: Released lock1. Exiting...");
        }
    }

    /**
     * To avoid DeadLock, the follow class methods must follow the same order
     */
    private static class Thread2 extends Thread {

        public void run() {
//            runDeadlock();
            runNonDeadlock();
        }

        /**
         * Thread 2 acquires lock 2 while Thread 1 acquires lock 1.
         * Then Thread 2 wants to acquire lock 1 while Thread 1 wants to acquire lock 2.
         * Both are suspended and blocked. We have a deadlock!
         */
        public void runDeadlock() {
            synchronized (lock2) {
                System.out.println("Thread 2: Has lock2");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
                System.out.println("Thread 2: Waiting for lock1");
                synchronized (lock1) {
                    System.out.println("Thread 2: Has lock1 and lock2");
                }
                System.out.println("Thread 2: released lock1");
            }
            System.out.println("Thread 2: Released lock2. Exiting...");
        }

        /**
         * In order to prevent a Deadlock, the lock should be acquire in the same order.
         * By using synchronized, only 1 of the Threads will acquire lock and execute.
         *
         * Thread 1 and Thread 2 wants to acquire lock 1.
         * If Thread 1 acquires lock 1, then Thread 2 will be suspended until Thread 1 releases it.
         *
         * Then Thread 1 acquires lock 2 and releases all locks.
         * Thread 2 resume execution and acquires lock 1 and then lock 2 and finally releases all locks.
         *
         */
        public void runNonDeadlock() {
            synchronized (lock1) {
                System.out.println("Thread 2: Has lock1");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
                System.out.println("Thread 2: Waiting for lock2");
                synchronized (lock2) {
                    System.out.println("Thread 2: Has lock1 and lock2");
                }
                System.out.println("Thread 2: released lock2");
            }
            System.out.println("Thread 2: Released lock1. Exiting...");
        }
    }
}







