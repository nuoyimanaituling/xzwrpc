package io.xzw.xzwrpc.util;
import io.xzw.xzwrpc.exception.XzwRpcException;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolUtil {

    private static final int CORE_SIZE =32;
    private static final int MAX_SIZE =40;
    private static final long KEEP_ALIVE_TIME =60;

    private static final Integer QUEUE_CAPACITY =4096;

    private volatile static ExecutorService DEFAULT_CLIENT_EXECUTOR;

    private volatile static ExecutorService DEFAULT_SERVER_EXECUTOR;
    // 使用copyOnWriteArrayList可以作为考点
    private static final List<ExecutorService> EXECUTOR_POOLS =new CopyOnWriteArrayList<>();
    private ThreadPoolUtil(){};
    // 创建执行任务的线程池
    /**
     *
     * @param poolName
     * @return
     * 创建线程池的lamba表达式写法
     */
    public static ExecutorService createPool(String poolName){
        ThreadPoolExecutor poolExecutor =new ThreadPoolExecutor(CORE_SIZE,MAX_SIZE,KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,new ArrayBlockingQueue<>(QUEUE_CAPACITY),r -> {
            Thread thread =new Thread(r,poolName+"-worker");
            thread.setUncaughtExceptionHandler(((t, e) -> e.printStackTrace()));
            return thread;
            /**
             * 定义抛出的异常信息
             */
        },(r, executor) -> {

            throw new XzwRpcException("thread pool["+poolName+"] is exhausted");
        });
        EXECUTOR_POOLS.add(poolExecutor);
        return poolExecutor;
    }
    public static ExecutorService defaultRpcClientExecutor(){
        if(DEFAULT_CLIENT_EXECUTOR==null){
            synchronized (ThreadPoolExecutor.class){
                if (DEFAULT_CLIENT_EXECUTOR==null){
                    DEFAULT_CLIENT_EXECUTOR =createPool("rpc-default-client-pool");
                    return DEFAULT_CLIENT_EXECUTOR;
                }
            }
        }
        return DEFAULT_CLIENT_EXECUTOR;
    }
    public static ExecutorService defaultRpcServerExecutor(){
        if(DEFAULT_SERVER_EXECUTOR==null){
            synchronized (ThreadPoolExecutor.class){
                if (DEFAULT_SERVER_EXECUTOR==null){
                    DEFAULT_SERVER_EXECUTOR =createPool("rpc-default-server-pool");
                    return DEFAULT_SERVER_EXECUTOR;
                }
            }
        }
        return DEFAULT_SERVER_EXECUTOR;
    }
    public static void shutdownExistsPools(){
        EXECUTOR_POOLS.forEach(ExecutorService::shutdown);

    }
}
