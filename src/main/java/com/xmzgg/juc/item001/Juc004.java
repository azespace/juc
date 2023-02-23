package com.xmzgg.juc.item001;

/**
 * @ClassName juc003
 * @Description 线程交替打印
 * @Author XM
 * @Date 2022/09/23 16:42
 **/
public class Juc004 {
    //这里第一个线程启动执行printA的时候获取了同步锁,第二个线程执行了printB就会等待,此时printA调用wait释放锁并陷入了等待唤醒,然后printB获取到同步锁进入修改flag并打印输出后唤醒A后获取锁
    //后wait释放锁并等待notify
    boolean flag = false;
    public synchronized void printA() {
        if (!flag) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        flag = false;
        System.out.println(Thread.currentThread().getName() + "PrintA");
        notify();
    }
    public synchronized void printB() {
        if (flag) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        flag = true;
        System.out.println(Thread.currentThread().getName() + "PrintB");
        notify();
    }
    public static void main(String[] args) {
        Juc004 juc004 = new Juc004();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                juc004.printA();
            }
        }, "thread01").start();//thread01表示给线程命名方便控制台输出查看
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                juc004.printB();
            }
        },"thread02").start();
    }
}
