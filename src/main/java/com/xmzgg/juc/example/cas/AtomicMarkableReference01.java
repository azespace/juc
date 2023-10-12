package com.xmzgg.juc.example.cas;

import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * There is plenty of fish in the sea
 *
 * @Author XM  2023/10/12
 * Description: 使用AtomicMarkableReference解决ABA问题
 **/
public class AtomicMarkableReference01 {
    // 思想不是版本号了，而是是否被标记过 也就是是否被修改过
    private static AtomicMarkableReference<Integer> atomicMarkableReference= new AtomicMarkableReference(1, Boolean.FALSE);
    public static void main(String[] args) {
        new Thread(() -> {
            Integer reference = atomicMarkableReference.getReference();
            boolean marked = atomicMarkableReference.isMarked();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean b = atomicMarkableReference.compareAndSet(reference, reference + 1, marked, !marked);
            System.out.println(Thread.currentThread().getName() + " " + b);
        }, "t1").start();
        new Thread(() -> {
            Integer reference = atomicMarkableReference.getReference();
            boolean marked = atomicMarkableReference.isMarked();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 将值改为2后 已经修改了 同时标记符号 marked 改成 ！marked 这样线程1就改不了了 除非把marked 改成false才能修改 这也是一种思想
            boolean b = atomicMarkableReference.compareAndSet(reference, reference + 1,marked, !marked);
            System.out.println(Thread.currentThread().getName() + " " + b);
        }, "t2").start();
    }
}
