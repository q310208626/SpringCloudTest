package com.lsj.cloud.test.thread;

public class YieldTest {
    public static void main(String[] args) {
        try {
            new Thread(new Runnable() {
                public void run() {
                    long start = System.currentTimeMillis();
                    for (int i = 0; i < 1000000; i++) {
                        Thread.yield();
                    }
                    long end = System.currentTimeMillis();
                    System.out.println("a cost: " + (end - start) + " ms");
                }
            }).start();

            new Thread(new Runnable() {
                public void run() {
                    long start = System.currentTimeMillis();
                    for (int i = 0; i < 1000000; i++) {
                        try {
                            Thread.sleep(0);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    long end = System.currentTimeMillis();
                    System.out.println("b cost: " + (end - start) + " ms");
                }
            }).start();

            new Thread(new Runnable() {
                public void run() {
                    long start = System.currentTimeMillis();
                    for (int i = 0; i < 1000000; i++) {
                        Thread.onSpinWait();
                    }
                    long end = System.currentTimeMillis();
                    System.out.println("c cost: " + (end - start) + " ms");
                }
            }).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
