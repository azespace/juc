package com.xmzgg.juc.Knowledge.item001;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadLifeCycle
 * @Description 线程的生命周期
 * @Author XM
 * @Date 2022/09/22 17:48
 **/
public class ThreadLifeCycle {
    public static void main(String[] args) throws InterruptedException {
        //NEW -> RUNNABLE (Ready,Running) ->TERMINATED
        //线程的生命周期通过getState()方法查看   要注意这里输出为  NEW
        Thread thread01 = new Thread(()-> System.out.println("isRunning~~"));
        System.out.println(thread01.getState());//未执行start方法所以是NEW
        //调用start方法后进入Runnable状态,该状态细分2个状态,此时cpu可以分配时间片,未获取到时间片则为Ready状态,获取到则为Running状态
        //要知道的是还有个yield方法简称礼让线程,可以让当前线程Running状态重新进入Runnable的Ready状态
        thread01.start();
        //要注意这里输出为 RUNNABLE
        //新开线程运行需要时间,所以这里输出后才会执行线程语句输出isRunning~~,这里先执行
        System.out.println(thread01.getState());
        //这里还有个线程的状态叫TIMED_WAITING,表示在沉睡指定时间后继续执行进入RUNNABLE状态
        TimeUnit.SECONDS.sleep(1);
        //主线程睡了1秒等待新的线程执行完毕了,此时线程状态是TERMINATED啦
        System.out.println(thread01.getState());
        //注意：sleep和wait的区别在于sleep是不需要锁如果有锁了也不会释放,而wait必须需要在synchronized中执行并且释放锁，一个是obj方法，一个是Thread方法
        //必须在synchronized中执行保证了只有一个线程可以等待和访问对象锁，synchronized保证拥有锁，wait保障可以释放锁，允许其他线程获得锁并执行相关任务 并通过notify释放锁让synchronized重新获得锁  防止死锁和并发问题


        //wait方法让正在运行中的线程处于waiting等待状态,所以在run方法中执行,其他的则线程调用notify或者notifyAll唤醒,需要注意的是这2个方法需要配合synchronized锁，否则无法使用(直接报错)
        Object obj = new Object();
        Thread thread02 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (obj){
                    try {
                        //此线程直接释放obj锁，同时进入waiting等待阻塞状态,注意：等待状态的特征是等待其他线程做某事(notify等),Obj的wait(),Thread的join(),LockSupport的park()方法会进入waiting等待阻塞状态
                        obj.wait();
                        System.out.println("thread02:isRunning~~");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread02.start();
        TimeUnit.SECONDS.sleep(1);
        //睡了1s等待释放锁  WAITING
        System.out.println("thread02:"+thread02.getState());
        synchronized (obj){
            obj.notify();
        }
        //注意：阻塞状态的特征是等待获取对象的monitor锁 这里输出的是BLOCKED同步阻塞状态,执行了notify或者notifyAll方法后发生,这里主要是waiting等待队列进入了blocked的同步队列,这个状态表示的是阻塞线程在等待monitor锁
        //BLOCKED
        System.out.println("thread02:"+thread02.getState());
        //此时的线程在阻塞期间会获取到时间片，进而进去RUNNABLE状态
        //睡1秒等待线程执行完毕  TERMINATED
        TimeUnit.SECONDS.sleep(1);
        System.out.println("thread02:"+thread02.getState());

        //join
        Thread thread03 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.println(i);
                }
            }
        });
        thread03.start();
        System.out.println("one");
        //join方法底层是调用了wait方法的,并且通过线程死亡后调用了notifyAll方法唤醒,该方法的作用是a线程中调用b.join()，a终止，等待b执行完再执行自己
        //所以这里是输出ONE后，main线程就等待了，for循环执行完会线程关闭唤醒main线程执行下面的打印two
        thread03.join();
        System.out.println("two");
    }
}
