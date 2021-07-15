package io.xzw.xzwrpc.stub.net.params;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 客户端异步调用，返回结果类
 */
@Slf4j
public class RpcFutureResp implements Future<RpcResponse> {

    private RpcResponse resp;
    private final Lock lock =new ReentrantLock();
    private final Condition fin =lock.newCondition();
    public void RespBackBellRing(RpcResponse resp){
        // 猜测这一部分逻辑是该方法之前,就已经被调用get方法来异步获取resp
        // 此时resp来了，开始唤醒等待的阻塞线程
        this.resp =resp;

        try{
           lock.lock();
            fin.signalAll();
        }
        finally {
            lock.unlock();
        }
    }
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }
    @Override
    public boolean isCancelled() {
        return false;
    }
    @Override
    public boolean isDone() {
        /**
         * 当返回内容不为null的时候，代表异步返回的结果已经到达
         */
        return this.resp!=null;
    }
    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        get(-1,TimeUnit.MILLISECONDS);
        return resp;
    }
    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        try{
            lock.lock();
            // 当数据还未到达时
            while(!isDone()){
                // 如果发现超时的话，那么就直接阻塞，等待被唤醒
                if (timeout<=0){
                    // 走到这里的话就是直接被阻塞，因为超时时间为负数，只能被动唤醒
                    fin.await();
                }else {
                    // 设置超时等待，知道时间到达，被唤醒以后，检查一下数据是否到达，
                    // 如果数据到达，则直接退出循环
                    boolean await =fin.await(timeout,unit);
                    if(!await || isDone()){
                        break;
                    }
                }
            }
            if(!isDone()){
                // 如果超时的话，那么就会设置resp为超时异常
                log.error("xzw-rpc invoke timeout");
                resp =RpcResponse.builder().exception(new TimeoutException("invoke timeout")).build();
            }
        }
        finally {
            lock.unlock();
        }
        return resp;
    }
}
