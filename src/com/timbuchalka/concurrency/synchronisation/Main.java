package com.timbuchalka.concurrency.synchronisation;

import com.timbuchalka.concurrency.ThreadColor;

public class Main {

    public static void main(String[] args) {
        Countdown countdown = new Countdown();

        CountdownThread t1 = new CountdownThread(countdown);
        t1.setName("Thread 1");
        CountdownThread t2 = new CountdownThread(countdown);
        t2.setName("Thread 2");

        t1.start();
        t2.start();
    }
}

class Countdown {

    //This object is shared between Threads in the heap memory
    private int i;

    //Because the 'i' is shared in the heap memory, it causes Thread interference (or Race Condition)
    //And some numbers are skipped by one of them Threads
    //To prevent this, it's possible to add the 'synchronized' keyword on the method.
    //This makes sure that this method gets executed by one Thread at a time while the other keeps suspended until the other ends.
    public void doCountdown() {
        String color;

        switch(Thread.currentThread().getName()) {
            case "Thread 1":
                color = ThreadColor.ANSI_CYAN;
                break;
            case "Thread 2":
                color = ThreadColor.ANSI_PURPLE;
                break;
            default:
                color = ThreadColor.ANSI_GREEN;
        }

        //The 'synchronized' keyword on this block makes sure that this loop will be executed by one Thread at a time
        synchronized(this) {
            for(i=10; i > 0; i--) {
                System.out.println(color + Thread.currentThread().getName() + ": i =" + i);
            }
        }
    }
}

class CountdownThread extends Thread {
    private Countdown threadCountdown;

    public CountdownThread(Countdown countdown) {
        threadCountdown = countdown;
    }

    public void run() {
        threadCountdown.doCountdown();
    }
}