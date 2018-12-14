package com.example.game.core;

import com.example.game.core.threadpool.ThreadPoolProvider;
import com.example.network.MessageService;
import com.example.network.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wdf on 2018/10/10.
 */


@Component
public class GameMain implements ApplicationRunner {

    //记录一下主线程id
    private static long mainThreadid=-1;

    private static MBeanServer mbs= ManagementFactory.getPlatformMBeanServer();


    //关闭标识
    public static AtomicBoolean isShutDown = new AtomicBoolean(false);

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    @Autowired
    private NettyServer nettyServer;

    public void run(ApplicationArguments args) {
        logger.info("SERVER STARTING...");
        init();
        start();
    }

    private void init() {
        MessageService.INSTANCE.init();
        ThreadPoolProvider.INSTANCE.init();
    }

    private void start() {
        try {
            nettyServer.start();
            logger.info("SERVER OPENED");

            final Stopper stopper=new Stopper();
            registerMbean(stopper,"stopper");

            registerShutdownHook();

            stopper.doWait();
            logger.info("SERVER SHUTDOWN...");

            //不再接受任何的协议
            isShutDown.set(true);

            onShutdown();

            logger.info("BYE");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注意：在main函数执行之前或返回之后不得调用！
     * @return
     */
    public static boolean isInMainThread(){
        return Thread.currentThread().getId()==mainThreadid;
    }

    private static void registerMbean(Object obj,String name) throws javax.management.NotCompliantMBeanException,javax.management.MBeanRegistrationException,javax.management.InstanceAlreadyExistsException,javax.management.MalformedObjectNameException{
        mbs.registerMBean(obj, new javax.management.ObjectName("bean:name="+name));
    }

    private static void onShutdown() throws Exception
    {
        // 记录关服时间


        // 关闭所有的定时器调度


        //1.踢所有人下线


        //保存下所有角色的信息


        try {
            Thread.sleep(5000);  //还是挺5秒吧
        } catch (Exception e) {}

        //执行不用按顺序处理的模块退出

        stopJMXServer();
    }

    private static void stopJMXServer()
    {
        try {
            Stopper.shutdownCompletedLock.lockInterruptibly();
        }catch(final InterruptedException ex){
            return;
        }
        Stopper.shutdownCompleted.signalAll();
        Stopper.shutdownCompletedLock.unlock();
        // jmxserver.stop();
        logger.info("JMX SERVER STOPED");
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new ServerShutdownHook());
    }
}
