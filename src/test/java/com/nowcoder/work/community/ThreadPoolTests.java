package com.nowcoder.work.community;

import com.nowcoder.work.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {

    private static final Logger logger  = LoggerFactory.getLogger(ThreadPoolTests.class);

    // JDK普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    // JDK定时任务线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    // spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    // spring 可以执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private AlphaService alphaService;

    public void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 1.JDK普通线程池
    @Test
    public void testExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ExecutorService");
            }
        };
        for(int i = 0; i < 10; i++){
            executorService.submit(task);
        }
        sleep(10000);
    }

    // 2.JDK定时任务线程池
    @Test
    public void testScheduledExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ScheduledExecutorService");
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(task, 1000, 1000, TimeUnit.MILLISECONDS);
        sleep(12000);
    }

    // 3.spring普通线程池
    @Test
    public void testThreadTaskExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ThreadPoolTaskExecutor");
            }
        };
        for(int i = 0; i < 10; i++){
            taskExecutor.submit(task);
        }
        sleep(10000);
    }

    // 4.spring执行定时任务的线城池
    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("Hello ThreadPoolTaskScheduler");
            }
        };
        Date startTime = new Date(System.currentTimeMillis() + 1000);
        taskScheduler.scheduleAtFixedRate(task, startTime, 1000);
        sleep(12000);
    }

    // 5.简化版的spring普通线程池
    @Test
    public void testThreadPoolTaskExecutorSimple(){
        for(int i = 0; i < 10; i++){
            alphaService.execute1();
        }
        sleep(10000);
    }

    // 5.简化版的spring定时任务线程池
    @Test
    public void testThreadPoolTaskSchedulerSimple(){
        sleep(10000);
    }
}
