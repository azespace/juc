package com.xmzgg.juc.item001;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName juc003
 * @Description synchronized对象锁与类锁
 * @Author XM
 * @Date 2022/09/28 17:15
 **/
public class Juc003 {
    // 对象锁 使用 synchronized 关键字修饰一个实例方法或者代码块 这个方法或者代码块被称为对象锁。对象锁是针对对象实例的，每个实例都有自己的对象锁。不同实例的对象锁是独立的，因此它们不会相互影响
    // 类锁 当你使用 synchronized 关键字修饰一个静态方法时，这个方法被称为类锁。类锁是针对整个类的，而不是特定的实例。只要一个线程获得了类锁，其他线程就不能同时调用该类的其他synchronized静态方法

    /**
     * "synchronized "关键字只能用于方法和代码块。这些方法或代码块可以是静态或非静态的。
     * 在方法上加synchronized锁,属于对象锁,对new出来的这个对象生效,会获得一个该类的唯一的同步锁，当这个锁被占用时，其他的加了 synchronized 的方法就必须等待
     * 当一个线程进入synchronized方法或代码块时，它就会获得一个锁，当它离开同步方法或代码块时，它就会释放这个锁。如果线程执行过程出现任何错误或异常，锁也会被释放。
     * 使用"synchronized "关键字持有的锁在本质上是可重入的，这意味着如果一个同步方法调用另一个使用相同锁的同步方法，那么持有锁的当前线程可以进入该方法而无需再次获得锁。
     * 如果同步块中使用的对象为空，Java synchronized 将抛出NullPointerException
     * 使用synchronized同步方法会给你的应用程序带来性能成本。因此，尽量在绝对需要的情况下才使用同步。另外优先考虑使用同步代码块，并且只同步代码的关键部分。
     * 静态同步方法和非静态同步方法有可能同时或并发运行，因为它们使用的是不同的锁。
     * 根据Java语言规范，你不能在构造函数中使用synchronized关键字。这是不合法的，会导致编译错误。
     * 不要使用非final的成员变量作为同步锁对象(针对对象锁)，因为非final成员变量可以被重新赋值，导致不同的线程使用不同的对象作为锁，达不到同步锁定的效果。
     * 不要使用字符串字面量作为锁对象,如：String a = "hello";因为它们可能会被应用程序中的其他地方引用比如java的api类库，并可能导致死锁，因为使用的同一把锁。用new关键字创建的字符串对象可以安全使用（见测试）
     **/

    //   第一种对象锁
    //   相比synchronized (this) 粒度更细，this锁定的是当前对象实例，对当前对象的所有同步代码块或同步方法进行锁定
    //   obj锁定的是指定的 Object 类型的对象，可以选择性地控制一段代码块的同步，而不影响代码的其他部分
    private final Object obj = new Object();

    public void printA() {
        synchronized (obj) {
        }
    }

    //    第二种对象锁
    //    this也是属于对象锁,缺点是一旦别人用了这个对象那么会造成死锁，就是说new了juc003对象,然后执行了juc003.printA(),但是其他线程使用synchronized(juc003){},就会导致死锁,下面详细介绍
    //    因为他们锁的是这个当前对象，所以如果其他线程也用了这个对象，那么就会造成死锁，因为他们都在等待锁，但是锁都被占用了，所以就会造成死锁
    //    我们应该使用锁粒度更小的代码块锁并使用其他对象作为锁，而不是使用this对象作为锁以及在方法上加上synchronized关键字 从而减少竞态条件和性能问题
    //    竞态条件是指多个线程并发执行时，由于不恰当的执行顺序或时序问题，导致程序的行为产生不确定的结果 这里指没有锁住关键资源  性能问题是指其他线程频繁抢锁
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
