package com.xmzgg.juc.item001;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName juc003
 * @Description synchronized对象锁与类锁
 * @Author XM
 * @Date 2022/09/28 17:15
 **/
public class Juc003 {
    /**
     * "synchronized "关键字只能用于方法和代码块。这些方法或代码块可以是静态或非静态的。
     * 在方法上加synchronized锁,属于对象锁,对new出来的这个对象生效,会获得一个该类的唯一的同步锁，当这个锁被占用时，其他的加了 synchronized 的方法就必须等待
     * 当一个线程进入synchronized方法或代码块时，它就会获得一个锁，当它离开同步方法或代码块时，它就会释放这个锁。如果线程执行过程出现任何错误或异常，锁也会被释放。
     * 使用"synchronized "关键字持有的锁在本质上是可重入的，这意味着如果一个同步方法调用另一个使用相同锁的同步方法，那么持有锁的当前线程可以进入该方法而无需再次获得锁。
     * 如果同步块中使用的对象为空，Java synchronized 将抛出NullPointerException
     * 使用synchronized同步方法会给你的应用程序带来性能成本。因此，尽量在绝对需要的情况下才使用同步。另外优先考虑使用同步代码块，并且只同步代码的关键部分。
     * 静态同步方法和非静态同步方法有可能同时或并发运行，因为它们使用的是不同的锁。
     * 根据Java语言规范，你不能在构造函数中使用synchronized关键字。这是不合法的，会导致编译错误。
     * 不要使用非final的成员变量作为同步锁对象，因为非final成员变量可以被重新赋值，导致不同的线程使用不同的对象作为锁，达不到同步锁定的效果。
     * 不要使用字符串字面量作为锁对象,如：String a = "hello";因为它们可能会被应用程序中的其他地方引用比如java的api类库，并可能导致死锁，因为使用的同一把锁。用new关键字创建的字符串对象可以安全使用（见测试）
     * 第一种对象锁
     **/
    private final Object obj = new Object();

    public void printA() {
        synchronized (obj) {
        }
    }

    //    第二种对象锁
    //    this也是属于对象锁,缺点是一旦别人用了这个对象那么会造成死锁，就是说new了juc003对象,然后执行了juc003.printA(),但是其他线程使用synchronized(juc003){},就会导致死锁,下面详细介绍
    public void printB() {
        synchronized (this) {
            for (int i = 0; i < 100; i++) {
                System.out.println(i);
            }
        }
    }

    //    第三种对象锁
    public synchronized void printC() {
        //            ...
    }

    //比如在不带关键字static的方法上加锁，或者代码块synchronized(obj){} 里面的对象也不能是static修饰,不然就是类锁，注意，代码块中的对象必须加final，保证该对象不可变
    //不然在执行其他方法的时候对象引用变了就锁不住了。
    //还有一种是属于类锁就是方法带static或者代码块里的private final Object obj = new Object();
    public static void main(String[] args) {
        Juc003 juc003 = new Juc003();
        Juc003 juc003A = new Juc003();//用这个就不会锁住了
        //这里测试了printB的this锁,只要其他线程用了这个对象，那么只要用了this的代码块的线程就会陷入锁竞争，因为调用该方法的对象和代码块锁的对象是同一个哈
        new Thread(() -> {
            synchronized (juc003A) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println("1");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        new Thread(() -> {
            juc003A.printB();
        }).start();
    }
}
