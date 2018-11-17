package retry.simple.util;

/**
 * 
 * <p>
 * 类描述:simplyRetryer 的构造器
 * </p>
 * 
 * @since 2018年11月16日
 * @author Jiafeng Feng
 */
public class SimplyRetryerBuilder<T> {

    /** 默认重试次数 */
    private static final int DEFAULT_TRY_NUM = 1;
    /** 默认重试时间 */
    private static final long DEFAULT_TIME_OUT = -1L;
    /** 重试 */
    private static int tryNum = DEFAULT_TRY_NUM;
    /** 超时 */
    private static long timeOut = DEFAULT_TIME_OUT;

    /** 创建实例 */
    public SimplyRetryer<T> build() {
        return new SimplyRetryer<T>(tryNum, timeOut);
    }

    /**
     * 设置重试次数，默认重试1次
     * 
     * @param num
     *            次数
     * @return this
     */
    public SimplyRetryerBuilder<T> withTryNum(int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("设置的重试次数必须是大于0的整数");
        }
        tryNum = num;
        return this;
    }

    /**
     * 设置超时时间,默认永不超时
     * 
     * @param timeout
     *            超时时间
     * @return this
     */
    public SimplyRetryerBuilder<T> withTimeout(long timeout) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("设置的超时时间应该大于0");
        }
        timeOut = timeout;
        return this;
    }

    /**
     * 创建构造器
     * 
     * @return 构造器
     */
    public static <T> SimplyRetryerBuilder<T> newBuilder() {
        return new SimplyRetryerBuilder<T>();
    }



}
