package com.timbuchalka.concurrency.threadinterference;

import com.timbuchalka.concurrency.ThreadColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPoolMain {
    public static final String EOF = "EOF";

    public static void main(String[] args) {
        List<String> buffer = new ArrayList<String>();
        // newFixedThreadPool - Sets the maximum number of allowed threads to acquire lock and execute,
        // others will be suspended until one of the threads release it's lock
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        MyProducer producer = new MyProducer(buffer, ThreadColor.ANSI_YELLOW);
        MyConsumer consumer1 = new MyConsumer(buffer, ThreadColor.ANSI_PURPLE);
        MyConsumer consumer2 = new MyConsumer(buffer, ThreadColor.ANSI_CYAN);

        executorService.execute(producer);
        executorService.execute(consumer1);
        executorService.execute(consumer2);

        // It's also possible to use Callable (via lambda) that returns Future object.
        Future<String> future = executorService.submit(() -> {
            System.out.println(ThreadColor.ANSI_WHITE + "I'm being printed for the Callable class");
            return "This is the callable result";
        });

        try {
            System.out.println(future.get());
        } catch(ExecutionException e) {
            System.out.println("Something went wrong");
        } catch(InterruptedException e) {
            System.out.println("Thread running the task was interrupted");
        }

        executorService.shutdown();
    }
}


























