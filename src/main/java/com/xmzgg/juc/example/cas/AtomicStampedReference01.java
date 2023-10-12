package com.xmzgg.juc.example.cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * There is plenty of fish in the sea
 *
 * @Author XM  2023/10/12
 * Description: 使用AtomicStampedReference解决ABA问题
 **/
public class AtomicStampedReference01 {
    // 初始值为1，初始版本号为1
    private static AtomicStampedReference<Integer> atomicStampedRef =
            new AtomicStampedReference<>(1, 1);
    public static void main(String[] args) {
        new Thread(() -> {
            Integer reference = atomicStampedRef.getReference();
            int stamp = atomicStampedRef.getStamp();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 尝试将值改为2 失败 此时版本号是 1 -> 2  但是值已经被改为3了
            boolean b = atomicStampedRef.compareAndSet(reference, reference + 1, stamp, stamp + 1);
            System.out.println(Thread.currentThread().getName() + " " + b);
        }, "t1").start();
        new Thread(() -> {
            Integer reference = atomicStampedRef.getReference();
            int stamp = atomicStampedRef.getStamp();
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 尝试将值改为2 版本号从 1 -> 2  没问题
            boolean b = atomicStampedRef.compareAndSet(reference, reference + 1,stamp, stamp + 1);
            // 再将值改回1 但此时版本号已经变了 第一个线程就修改不了了 这里可以修改是因为版本号是 2 -> 3 没问题 这里一定要用atomicStampedRef.getStamp()获取最新的版本号 不然也更新不了的
            boolean b1 = atomicStampedRef.compareAndSet(reference+1, reference , atomicStampedRef.getStamp(), atomicStampedRef.getStamp()+1);
            System.out.println(Thread.currentThread().getName() + " " + b + " " + b1);
        }, "t2").start();
    }
}
