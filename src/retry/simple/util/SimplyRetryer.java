package retry.simple.util;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * <p>
 * 类描述: 简单的重试类
 * </p>
 * 
 * @since 2018年11月16日
 * @author Jiafeng Feng
 */
public class SimplyRetryer<T> {
    /** 日志 */
    private static final Logger LOG = Logger.getLogger(SimplyRetryer.class.getName());

    /** 默认重试次数 */
    private static final int DEFAULT_TRY_NUM = 1;
    /** 默认重试时间 */
    private static final long DEFAULT_TIME_OUT = -1L;
    /** 重试 */
    private int tryNum = DEFAULT_TRY_NUM;
    /** 超时 */
    private long timeOut = DEFAULT_TIME_OUT;
    /** 线程池 */
    private ExecutorService executor;

    /**
     * 设置重试次数，默认重试1次
     * 
     * @param num
     *            次数
     * @return this
     */
    public SimplyRetryer<T> withTryNum(int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("设置的重试次数必须是大于0的整数");
        }
        this.tryNum = num;
        return this;
    }

    /**
     * 设置超时时间,默认永不超时
     * 
     * @param timeout
     *            超时时间
     * @return
     */
    public SimplyRetryer<T> withTimeout(long timeout) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("设置的超时时间应该大于0");
        }
        this.timeOut = timeout;
        return this;
    }

    /**
     * 构造方法
     * 
     * @param tryNum
     *            尝试次数
     * @param timeOut
     *            超时
     */
    SimplyRetryer(int tryNum, long timeOut) {
        super();
        this.tryNum = tryNum;
        this.timeOut = timeOut;
    }

    /**
     * 执行
     * 
     * @throws Throwable
     */
    public T call(Callable<T> callable) throws RetryException {
        // 确认重试次数
        for (int attemptNumber = 1;; attemptNumber++) { // 提交任务
            RetryResult<T> result = null;
            // 执行方法
            try {
                T re = executCall(callable);
                // success
                result = new RetryResult<T>(re, null);
                // fail
            } catch (RetryException e) {
                result = new RetryResult<T>(null, e);
            } catch (Exception e) {
                result = new RetryResult<T>(null, e);
            }

            // 获取结果
            if (result.isSuccess()) {
                return result.getResult();
            }
            // 重试结束
            if (shouldStop(attemptNumber)) {
                Throwable throwable = result.getThrowable();
                throw new RetryException(throwable, "重试" + (attemptNumber - 1) + "次后，仍无法获取结果");
            }

            // 重试间隔5秒
            try {
                LOG.log(Level.INFO, "5秒后进行重试,准备进行重试");
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * 获取结果
     */
    private T executCall(Callable<T> callable) throws Exception {
        // 获取结果
        T result = null;
        if (timeOut > 0) {
            // 需要超时
            result = timeOutCall(callable);
        } else {
            // 调用不会超时
            result = callable.call();
        }

        return result;
    }

    /**
     * 调用会超时
     * 
     * @throws RetryException
     * 
     */
    private T timeOutCall(Callable<T> callable) throws RetryException {
        if (executor == null) {
            // 避免限制造成资源浪费 tips: 注意任务如果没有设置超时的，线程池可能会boom哦
            executor =Executors.newCachedThreadPool();
        }

        // 提交任务
        Future<T> future = executor.submit(callable);
        try {
            T t = future.get(timeOut, TimeUnit.MILLISECONDS);
            return t;
        } catch (InterruptedException e) {
            future.cancel(true);
            throw new RetryException(e, "调用时线程被中断");
        } catch (ExecutionException e) {
            throw new RetryException(e, "调用时线程池出错");
        } catch (TimeoutException e) {
            LOG.log(Level.SEVERE, "调用服务时间超时");
            future.cancel(true);
            throw new RetryException(e, "调用服务时间超时");
        }

    }

    /**
     * 是否需要停止
     */
    private boolean shouldStop(int attemptNumber) {
        if (tryNum < attemptNumber) {
            return true;
        } else {
            return false;
        }
    }

}

/**
 * 
 * <p>
 * 类描述: 重试的结果类
 * </p>
 * 
 * @since 2018年11月16日
 * @author Jiafeng Feng
 */
class RetryResult<T> {

    private T result;

    private Throwable throwable;

    /**
     * @return the result
     */
    public T getResult() {
        return result;
    }

    /**
     * @param result
     *            要设置的 result
     */
    public void setResult(T result) {

        this.result = result;
    }

    /**
     * @return the throwable
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * 
     * @param throwable
     *            要设置的 throwable
     */
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * 构造类
     * 
     * @param result
     *            结果
     * @param throwable
     *            异常
     */
    public RetryResult(T result, Throwable throwable) {
        super();
        this.result = result;
        this.throwable = throwable;
    }

    public boolean isSuccess() {
        return this.throwable == null;
    }

}


