package com.timbuchalka.concurrency.threadinterference;

import com.timbuchalka.concurrency.ThreadColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static final String EOF = "EOF";

    public static void main(String[] args) {
        /* ArrayList is not thread-safe hence it's not synchronized.
         * This means that it will be affected by concurrent threads when trying to modify the same list.
         * See more at: https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
         */
        List<String> buffer = new ArrayList<String>();
        MyProducer producer = new MyProducer(buffer, ThreadColor.ANSI_YELLOW);
        MyConsumer consumer1 = new MyConsumer(buffer, ThreadColor.ANSI_PURPLE);
        MyConsumer consumer2 = new MyConsumer(buffer, ThreadColor.ANSI_CYAN);

        new Thread(producer).start();
        new Thread(consumer1).start();
        new Thread(consumer2).start();
    }
}

class MyProducer implements  Runnable {
    private List<String> buffer;
    private String color;

    public MyProducer(List<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }

    public void run() {
        Random random = new Random();
        String[] nums = { "1", "2", "3", "4", "5"};

        for(String num: nums) {
            try {
                System.out.println(color + "Adding..." + num);
                /*
                 * In order to prevent concurrent modifications from multiple threads on the same list, the 'synchronized' block is added
                 * Therefore obtaining a lock for this Thread when changing this list.
                 * The drawbacks:
                 * - Threads that are blocked due to waiting to execute synchronized code can't be interrupted until they acquire the lock ownership.
                 * - The 'synchronized' block must be within the same method and can't end on another.
                 * - Can't find information about the locks or perform a timeout when lock is not acquired within a period of time.
                 * - If multiple threads are waiting to get a lock, it's not a first come first service.
                 * JVM doesn't follow an order to choose which thread should get the lock, which means that
                 * The first thread to ask for a lock could be the last to obtain it.
                 */
                synchronized(buffer) {
                    buffer.add(num);
                }

                Thread.sleep(random.nextInt(1000));
            } catch(InterruptedException e) {
                System.out.println("Producer was interrupted");
            }
        }

        System.out.println(color + "Adding EOF and exiting....");
        /*
         * Same as mentioned on the previous `synchronized' block.
         * All changes to the list must have it to prevent Thread interference.
         */
        synchronized(buffer) {
            buffer.add("EOF");
        }
    }
}

class MyConsumer implements Runnable {
    private List<String> buffer;
    private String color;

    public MyConsumer(List<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }

    public void run() {
        while(true) {
            /*
             * It's also possible to use {@link java.util.concurrent.locks.ReentrantLock} instead of 'synchronized' blocks.
             * But on this case it's the developer's responsibility to lock and unlock to release the Thread.
             * If unlock is missing or not executed it could lead to an exception on maximum locks acquired.
             */
            synchronized(buffer) {
                if(buffer.isEmpty()) {
                    continue;
                }
                if(buffer.get(0).equals(Main.EOF)) {
                    System.out.println(color + "Exiting");
                    break;
                } else {
                    System.out.println(color + "Removed " + buffer.remove(0));
                }
            }
        }
    }
}
