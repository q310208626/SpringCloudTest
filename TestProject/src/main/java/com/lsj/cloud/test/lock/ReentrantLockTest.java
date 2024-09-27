package com.lsj.cloud.test.lock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@RunWith(JUnit4.class)
public class ReentrantLockTest {

    @Test
    public void reentrantLock() {
        ReentrantLock lock = new ReentrantLock(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.lock();
                    Thread.sleep(60000);
                    lock.unlock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                lock.unlock();
            }
        }).start();
    }

    @Test
    public void conditionTest(){
        ReentrantLock lock = new ReentrantLock(true);
        Condition condition = lock.newCondition();

//        线程0 竞争锁--->线程0获取锁
//        线程0 condition await--->线程0第一个加入condition队列，释放锁
//        线程1 竞争锁 --->线程1获取锁
//        线程1 condition await --->线程1第二个加入condition队列，释放锁
//        线程2竞争锁--->线程2获取锁
//        线程2已执行--->线程2 doSignal()，把condition队列中第一个线程节点，即线程0先移出，再把它加入到lock中的等待队列，然后释放锁，唤醒等待lock队列中第一个线程，即线程0
//        线程0 condition 继续--->线程0被唤醒，获取到锁后，把condition队列中第一个线程，即线程1先移出，再把它加入到lock中的等待队列，然后释放锁，唤醒等待lock队列中第一个线程，即线程1
//        线程1 condition 继续--->线程1被唤醒
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("线程0 竞争锁");
                    lock.lock();
                    System.out.println("线程0 condition await");
                    condition.await();
                    condition.signal();
                    lock.unlock();
                    System.out.println("线程0 condition 继续");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("线程1 竞争锁");
                    lock.lock();
                    System.out.println("线程1 condition await");
                    condition.await();

                    lock.unlock();
                    System.out.println("线程1 condition 继续");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //                    Thread.sleep(1000);
                System.out.println("线程2竞争锁");
                lock.lock();
                condition.signal();
                System.out.println("线程2已执行");
                lock.unlock();
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }
}
