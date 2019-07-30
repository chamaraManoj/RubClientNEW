package com.example.clientside;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ModuleManager {

    /*Executor interface is used to decouple the task submission from the task
     * execution. ThreadPoolExecutor class has implemented this ExectorService,
     * which has implemented the executor interface.*/

    private final ThreadPoolExecutor socketDataStreamPool;

    /*There are set of queues available to include tasks as a queue*/
    private final BlockingQueue<Runnable> socketDataStreamQueue;


    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private static ModuleManager moduleManager = null;
    private static MainThreadExecutor handler;

    static {
        moduleManager = new ModuleManager();
        handler = new MainThreadExecutor();
    }

    /*With in the constructor both task queue and Thread pool is created*/
    private ModuleManager() {
        socketDataStreamQueue = new LinkedBlockingDeque<Runnable>();
        socketDataStreamPool = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                socketDataStreamQueue);
    }

    /*method to be invoked for getting the module manager object*/
    public static ModuleManager getDownloadManager(){
        return moduleManager;
    }

    /*Add the runnable thread to the pool to be executed*/
    public void runDownloadFile(Runnable task){
        socketDataStreamPool.execute(task);
    }

    public MainThreadExecutor getMainThreadExecutor(){
        return handler;
    }

}
