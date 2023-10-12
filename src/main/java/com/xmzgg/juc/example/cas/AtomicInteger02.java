package com.xmzgg.juc.example.cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * There is plenty of fish in the sea
 *
 * @Author XM  2023/10/12
 * Description: 使用AtomicInteger演示ABA问题
 **/
public class AtomicInteger02 {
    private static AtomicInteger atomicInteger = new AtomicInteger(100);
    public static void main(String[] args) {
        new Thread(() -> {
            int a = atomicInteger.get();
            int b = a+1;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            atomicInteger.compareAndSet(a, b);
            System.out.println(Thread.currentThread().getName() + " " + atomicInteger.get());
        }, "t1").start();
        // 其实atomicInteger的值已经被修改过了，只是线程1不知道，应该给每次修改都加一次版本号 AtomicStampedReference类可以实现，下一个案例演示
        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int a = atomicInteger.incrementAndGet();
            int b = atomicInteger.decrementAndGet();
            System.out.println(Thread.currentThread().getName() + " " + a+"----" + b);
        }, "t2").start();
    }
}
