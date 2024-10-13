package com.lsj.cloud.test.reactive;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

@RunWith(JUnit4.class)
public class ReactiveTest {

    @Test
    public void ReactiveStreamTest() {
        Flow.Subscriber<String> subscriber = new Flow.Subscriber<String>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("Start onSubscribe");
                this.subscription = subscription;
                subscription.request(1);
                System.out.println("End onSubscribe");
            }

            @Override
            public void onNext(String item) {
                System.out.println("Start onNext");
                System.out.println("Subscriber Thread:" + Thread.currentThread() + ",onNext: " + item);
                subscription.request(1);
                System.out.println("End onNext");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Start onError");
                System.out.println("onError: " + throwable.getMessage());
                subscription.cancel();
                System.out.println("End onError");
            }

            @Override
            public void onComplete() {
                System.out.println("Tasks onComplete");
            }
        };

        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
        publisher.subscribe(subscriber);

        for (int i = 0; i < 100; i++) {
            publisher.submit("Task:" + i);
        }

        publisher.close();
    }
}
