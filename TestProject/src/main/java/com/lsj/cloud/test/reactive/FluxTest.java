package com.lsj.cloud.test.reactive;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(JUnit4.class)
public class FluxTest {

    @Test
    public void FluxTest() {
        Flux<Integer> flux = Flux.range(1, 100);
        AtomicInteger succ = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        flux.subscribe(v -> {
            System.out.println("Current Thread:" + Thread.currentThread() + "," + v);
            succ.getAndIncrement();
        }, v -> {
            System.out.println("Handle Error:" + v.getMessage());
            fail.getAndIncrement();
        }, () -> {
            System.out.println("Handle success:" + succ.get() + ",fail:" + fail.get());
        });
    }

    @Test
    public void FluxAsynTest() {
        // 异步单线程
        Flux<Integer> flux = Flux.range(1, 100);
        AtomicInteger succ = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        Scheduler subscribeScheduler = Schedulers.newParallel("Subscriber Schedule", 5);
        flux.subscribeOn(subscribeScheduler).subscribe(v -> {
            System.out.println("Current Thread:" + Thread.currentThread() + "," + v);
            succ.getAndIncrement();
        }, v -> {
            System.out.println("Handle Error:" + v.getMessage());
            fail.getAndIncrement();
        }, () -> {
            System.out.println("Handle success:" + succ.get() + ",fail:" + fail.get());
        });

        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void FluxConcurrentTest() {
        // 并发多线程
        Flux<Integer> flux = Flux.range(1, 100);
        AtomicInteger succ = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        Scheduler subscribeScheduler = Schedulers.newParallel("Subscriber Schedule", 5);
        flux.buffer(10)
                .parallel(4)
                .runOn(subscribeScheduler)
                .flatMap(Flux::fromIterable)
                .subscribe(v -> {
                    System.out.println("Current Thread:" + Thread.currentThread() + "," + v);
                    succ.getAndIncrement();
                }, v -> {
                    System.out.println("Handle Error:" + v.getMessage());
                    fail.getAndIncrement();
                }, () -> {
                    System.out.println("Handle success:" + succ.get() + ",fail:" + fail.get());
                });

        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void bufferTest() {
        Flux.range(1, 10).buffer(3).log().subscribe();
    }

    @Test
    public void limitRateTest() {
        Flux.range(1, 10).limitRate(4).log().subscribe();
    }

    @Test
    public void handleTest() {
        Flux.range(1, 10).handle((x, sink) -> {
            if (x % 2 == 0) {
                sink.next(x);
            } else {
                sink.next("String:" + x);
            }
        }).log().subscribe();
    }


    @Test
    public void concatTest() {
        // concatWith只能合并相同类型
        Flux.just(1, 2).concatWith(Flux.just(3, 4)).log().subscribe();

        // concat能合并不同类型
        Flux.concat(Flux.just(1, 2), Flux.just("a", 4)).log().subscribe();

        // concatMap由1个变成多个
        Flux.just(1, 2).concatMap(i -> Flux.just("key" + i, "value" + i, "desc" + i)).log().subscribe();
    }

    @Test
    public void mergeTest() {
        Flux.merge(Flux.range(0, 5)
                        .delaySubscription(Duration.ofMillis(2000)),
                Flux.range(5, 5)
                        .delaySubscription(Duration.ofMillis(1000))).log().subscribe();

        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void zipTest() {
        // 前后合并，[0,5] [1,6] [2,7] [3,8],个数取决于少的组
        Flux.range(0, 5).zipWith(Flux.range(5,4)).log().subscribe();
    }

    @Test
    public void cacheTest() {
        // cache(3)代表缓存个数，后面的会覆盖前面的，最后是2，3，4
        Flux<Integer> cache = Flux.range(0, 5).cache(3);
        cache.log().subscribe();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cache.log().subscribe();
            }
        }).start();

        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
