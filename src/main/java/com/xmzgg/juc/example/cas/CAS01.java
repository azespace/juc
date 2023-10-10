package com.xmzgg.juc.example.cas;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * There is plenty of fish in the sea
 *
 * @Author XM  2023/10/10
 * Description: 使用CAS优化多线程计数器
 **/
public class CAS01 {
    // 总人数
    private static final int peopleCount = 100;
    // 每人请求次数
    private static final int requestTime = 10;
    // 总请求次数
    private static volatile int totalCount = 0;

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
    // 使用原子性可见性解决多线程安全问题(只加volatile修饰totalCount是不行的，因为++操作不是原子性的，可能2个线程同时读取到totalCount的值为10，然后同时+1，最后结果为11  而不是12)
    // 1 在方法上加synchronized 效率最慢 不做演示
    // 2 降低锁的粒度 使用代码块锁 效率还行
    // 3 使用 CAS 效率较高 因为是无锁机制，基于CPU指令实现原子性的

    private static void request(){
        try{
            TimeUnit.MILLISECONDS.sleep(5);
        }catch (Exception e){
            e.printStackTrace();
        }
/*        // 你会发现在这里加synchronized锁 比在方法上加synchronized锁效率要高很多,因为sleep方法是多线程并行，并未锁住，效率高不少
        synchronized (CAS01.class){
            totalCount++;
        }
        */
        // 使用 CAS 效率更高(指的是JAVA底层的CAS操作，这里只是模拟CAS操作 实际这里效率一般)
        for (;;){
            int expectCount = totalCount;
            if (compareAndSwap(expectCount,expectCount+1)){
                break;
            }
        }
    }
    // 这里必须加synchronized 性能其实还没上面的高 只是为了引入CAS的内容，否则在做相等比较然后赋值的那一步不是原子性还是有安全问题
    // JAVA的底层是用的UnSafe类做的比较并交换，这里只是模拟一下 是属于硬件层面的无锁操作
    private static synchronized boolean compareAndSwap(int expectCount, int newCount) {
        // 如果当前值和期望值相等，就更新为新值
        if (expectCount == totalCount){
            totalCount = newCount;
            return true;
        }
        return false;
    }
}
