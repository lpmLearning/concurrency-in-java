package com.timbuchalka.concurrency.threadinterference.arrayblockingqueue;

import com.timbuchalka.concurrency.ThreadColor;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static final String EOF = "EOF";

    public static void main(String[] args) {
        ArrayBlockingQueue<String> buffer = new ArrayBlockingQueue<String>(6);

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        MyProducer producer = new MyProducer(buffer, ThreadColor.ANSI_YELLOW);
        MyConsumer consumer1 = new MyConsumer(buffer, ThreadColor.ANSI_PURPLE);
        MyConsumer consumer2 = new MyConsumer(buffer, ThreadColor.ANSI_CYAN);

        executorService.execute(producer);
        executorService.execute(consumer1);
        executorService.execute(consumer2);
        executorService.shutdown();
    }
}

class MyProducer implements Runnable, Logging {

    private ArrayBlockingQueue<String> buffer;
    private String color;

    public MyProducer(ArrayBlockingQueue<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }

    public void run() {
        Random random = new Random();
        String[] nums = {"1", "2", "3", "4", "5"};

        for (String num : nums) {
            try {
                logInfo(color, "Adding..." + num);
                buffer.put(num);

                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                logInfo(color, "Producer was interrupted");
            }
        }

        logInfo(color, "Adding EOF and exiting...");
        try {
            buffer.put("EOF");
        } catch (InterruptedException e) {
        }
    }
}

class MyConsumer implements Runnable, Logging {

    private ArrayBlockingQueue<String> buffer;
    private String color;

    public MyConsumer(ArrayBlockingQueue<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }

    public void run() {

        while (true) {
            // It was required to add the `synchronized` to avoid thread interference/conflict when for instance
            // running 2 consumers that invokes peek (shows the next element), one will be successful while the other
            // will get a null object result in NullPointerException in line 92.
            // To observe that just comment the synchronized statement in line 81.
            synchronized (buffer) {
                String nextElement = "";
                try {
                    if (buffer.isEmpty()) {
                        continue;
                    }

                    nextElement = buffer.peek();
                    logInfo(color, "My next element is: " + nextElement);

                    if (buffer.peek().equals(Main.EOF)) {
                        logInfo(color, "Exiting");
                        break;
                    } else {
                        logInfo(color, "Removed " + buffer.take());
                    }
                } catch (InterruptedException e) {

                } catch (NullPointerException ex) {
                    logInfo(color, "Failed when next element was: " + nextElement);
                }
            }
        }
    }
}

interface Logging {

    default void logInfo(String color, String message) {
        System.out.printf(
            color + "%s - %s#%s - %s%n",
            Instant.now().toString(),
            this.getClass().getSimpleName(),
            Thread.currentThread().getName(),
            message
        );
    }
}


























