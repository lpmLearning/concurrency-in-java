package com.timbuchalka.concurrency.notifyall;

import java.time.Instant;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Message message = new Message();
        (new Thread(new Writer(message))).start();
        (new Thread(new Reader(message))).start();
    }
}

class Message {
    private String message;
    private boolean empty = true;

    public synchronized String read() {
        while(empty) {
            try {
                /*
                 * Suspends execution and releases any locks it's holding until is notified (via notifyAll or notify method)
                 *
                 * Why does the 'wait()' needs to be inside a loop?
                 * Because there's no guarantee that the Thread will be waken up either by a condition is waiting on has changed
                 * or OS has waken it up for another reason or because it threw an {@link InterruptedException}
                 * Therefore it's better to always call wait within a loop, this way it will make sure it's condition is satisfied
                 * And 'wait()' will be called again if the condition hasn't changed.
                 */
                System.out.println(Instant.now() + ": Message - read will wait");
                wait();
            } catch(InterruptedException e) {

            }

        }
        empty = true;
        /*
         * Notifies a suspended thread to resume execution
         *
         * Difference between notify and notifyAll?
         * Notify is better used when there's multiple threads executing a similar task waiting for a lock,
         * in order to prevent waking up all threads the 'notify' method is used instead also to avoid performance issues.
         * NotifyAll is used conventionally when there's no multiple threads executing a similar task.
         */
        notifyAll();
        System.out.println(Instant.now() + ": Message - reading");
        return message;
    }

    public synchronized void write(String message) {
        while(!empty) {
            try {
                //Suspends execution and releases any locks it's holding until is notified (via notifyAll or notify method)
                System.out.println(Instant.now() + ": Message - write will wait");
                wait();
            } catch(InterruptedException e) {

            }

        }
        empty = false;
        System.out.println(Instant.now() + ": Message - writing");
        this.message = message;
        //Notifies a suspended thread to resume execution
        notifyAll();
    }
}

class Writer implements Runnable {
    private Message message;

    public Writer(Message message) {
        this.message = message;
    }

    public void run() {
        String messages[] = {
                "Humpty Dumpty sat on a wall",
                "Humpty Dumpty had a great fall",
                "All the king's horses and all the king's men",
                "Couldn't put Humpty together again"
        };

        Random random = new Random();

        for(int i=0; i<messages.length; i++) {
            message.write(messages[i]);
            try {
                int randomInt = random.nextInt(2000);
                System.out.println(Instant.now() + ": Writer - waiting " + randomInt + " milliseconds");
                Thread.sleep(randomInt);
                System.out.println(Instant.now() + ": Writer - done waiting");
            } catch(InterruptedException e) {

            }
        }
        message.write("Finished");
        System.out.println(Instant.now() + ": Writer - finished");
    }
}

class Reader implements Runnable {
    private Message message;

    public Reader(Message message) {
        this.message = message;
    }

    public void run() {
        Random random = new Random();
        for(String latestMessage = message.read(); !latestMessage.equals("Finished");
            latestMessage = message.read()) {
            System.out.println(Instant.now() + ": Reader - message is: " + latestMessage);
            try {
                int randomInt = random.nextInt(2000);
                System.out.println(Instant.now() + ": Reader - waiting " + randomInt + " milliseconds");
                Thread.sleep(randomInt);
                System.out.println(Instant.now() + ": Reader - done waiting");
            } catch(InterruptedException e) {

            }
        }
        System.out.println(Instant.now() + ": Reader - finished");
    }
}















