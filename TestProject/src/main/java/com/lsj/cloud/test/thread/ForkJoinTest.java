package com.lsj.cloud.test.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ForkJoinTest {
    public static void main(String[] args) {
        try {
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            NumZeroToEndSum numZeroToEndSum = new NumZeroToEndSum(0, 10000000, 1000);
            long start = System.currentTimeMillis();
            ForkJoinTask<Integer> submit = forkJoinPool.submit(numZeroToEndSum);
            int result = submit.get();
            long end = System.currentTimeMillis();
            System.out.println("result:" + result + ",cost: " + (end - start));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    static class NumZeroToEndSum extends RecursiveTask<Integer> {
        private int min;
        private int max;
        private int threshold;

        public NumZeroToEndSum(int min, int max, int threshold) {
            this.min = min;
            this.max = max;
            this.threshold = threshold;
        }

        @Override
        protected Integer compute() {
            if (max - min <= threshold) {
                int total = 0;
                for (int i = min; i <= max; i++) {
                    total += i;
                }
                return total;
            }

            int mid = min + (max - min) / 2;
            NumZeroToEndSum leftTask = new NumZeroToEndSum(min, mid, threshold);
            NumZeroToEndSum rightTask = new NumZeroToEndSum(mid + 1, max, threshold);

            leftTask.fork();
            rightTask.fork();

            int leftResult = leftTask.join();
            int rightResult = rightTask.join();
            return leftResult + rightResult;
        }
    }
}
