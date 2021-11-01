package com.timbuchalka.concurrency.joinandinterruption;

import static com.timbuchalka.concurrency.ThreadColor.ANSI_GREEN;
import static com.timbuchalka.concurrency.ThreadColor.ANSI_PURPLE;
import static com.timbuchalka.concurrency.ThreadColor.ANSI_RED;

public class Main {

    public static void main(String[] args) {
        System.out.println(ANSI_PURPLE+"Hello from the main thread.");

        final Thread anotherThread = new AnotherThread();
        anotherThread.setName("== Another Thread ==");
        //Be aware that while 'start()' triggers another Thread the 'run()' will couple the execution to the main Thread
        //and will block it until its done.
        anotherThread.start();

        new Thread() {
            public void run() {
                System.out.println(ANSI_GREEN + "Hello from the anonymous class thread");
            }
        }.start();

        Thread myRunnableThread = new Thread(new MyRunnable() {
            @Override
            public void run() {
                System.out.println(ANSI_RED + "Hello from the anonymous class's implementation of run()");
                try {
                    anotherThread.join();
                    System.out.println(ANSI_RED + "AnotherThread terminated, or timed out, so I'm running again");
                } catch(InterruptedException e) {
                    System.out.println(ANSI_RED + "I couldn't wait after all. I was interrupted");
                }
            }
        });

        myRunnableThread.start();
        //This will cause the thread to be interrupted
//        anotherThread.interrupt();

        System.out.println(ANSI_PURPLE+"Hello again from the main thread.");


    }
}
