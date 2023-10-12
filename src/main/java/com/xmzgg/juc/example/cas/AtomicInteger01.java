package com.xmzgg.juc.example.cas;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * There is plenty of fish in the sea
 *
 * @Author XM  2023/10/12
 * Description: 使用AtomicInteger完成CAS操作
 **/
public class AtomicInteger01 {
    // 总人数
    private static final int peopleCount = 100;
    // 每人请求次数
    private static final int requestTime = 10;
    // 总请求次数
    private static AtomicInteger totalCount = new AtomicInteger(0);

    public static void main(String[] args) throws Exception{
        // 计数器 这里表示初始化100个线程
        CountDownLatch downLatch = new CountDownLatch(peopleCount);
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < requestTime; j++) {
                    request();
                }
                // 每次执行完一个线程，计数器减 1
                downLatch.countDown();
            }).start();
        }
        // 在计数器不为0的时候，会一直阻塞在这里  直到计数器为0
        downLatch.await();
        System.out.println(totalCount);
    }
    private static void request(){
        try{
            TimeUnit.MILLISECONDS.sleep(5);
        }catch (Exception e){
            e.printStackTrace();
        }
        // 原子操作 不论多少个线程并发 最终一定是1000
        totalCount.incrementAndGet();
    }
}
