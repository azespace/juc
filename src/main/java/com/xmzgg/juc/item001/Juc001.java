package com.xmzgg.juc.item001;

import java.util.concurrent.*;

/**
 * @ClassName juc001
 * @Description 多线程的5种实现方式
 * @Author XM
 * @Date 2022/09/22 11:17
 **/
public class Juc001 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //创建线程第一种 继承Thread类并调用该类的start方法
        new MyThread().start();
        //创建线程第二种 实现Runnable接口,并将该类通过Thread类的构造方法的参数传入后调用Thread类的start方法启动线程
        new Thread(new MyRunnable()).start();
        //创建线程第三种  匿名内部类Runnable接口实现，又由于是函数式接口,所以用lambda表达式实现
        new Thread(()-> System.out.println("three")).start();
        //创建线程第四种  Callable接口lambda表达式利用FutureTask实现,这里注意Callable必须要返回数据的
        FutureTask<String> futureTask = new FutureTask<>(() -> "four");//Callable接口也是函数式接口,这里无参数,并且省略了return
        new Thread(futureTask).start();
        System.out.println(futureTask.get());//注意,要想获取返回的数据，需要调用FutureTask的get()方法获取
        //创建线程第五种线程池创建
        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.execute(()-> System.out.println("five"));//Runnable接口
        pool.shutdown();
    }
    public static class MyThread extends Thread{
        @Override
        public void run() {
            System.out.println("one");
        }
    }
    public static class MyRunnable implements  Runnable{
        @Override
        public void run() {
            System.out.println("two");
        }
    }
}
