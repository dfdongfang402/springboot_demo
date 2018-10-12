package com.example.game;

import com.example.network.MessageService;
import com.example.network.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wdf on 2018/10/10.
 */


@Component
public class GameMain implements ApplicationRunner {

    //记录一下主线程id
    private static long mainThreadid=-1;

    //关闭标识
    public static AtomicBoolean isShutDown = new AtomicBoolean(false);

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public void run(ApplicationArguments args) {
        logger.info("SERVER STARTING...");
        init();
        start();
    }

    private void init() {
        MessageService.INSTANCE.init();
    }

    private void start() {
        try {
            new NettyServer().start();
            logger.info("SERVER OPENED");

            final Stopper stopper=new Stopper();
//            registerMbean(stopper,"stopper");
            stopper.doWait();
            logger.info("SERVER SHUTDOWN...");

            //不再接受任何的协议
            isShutDown.set(true);

//            onShutdown();

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
}
