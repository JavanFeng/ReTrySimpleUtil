import retry.simple.util.RetryException;
import retry.simple.util.SimplyRetryer;
import retry.simple.util.SimplyRetryerBuilder;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  测试重试方法
 */
public class MainTest {
    /**
     * 日志
     */
    private static final Logger LOG = Logger.getLogger(SimplyRetryer.class.getName());

    public static void main(String[] args) {


        SimplyRetryer<Integer> retry = SimplyRetryerBuilder.<Integer>newBuilder().withTimeout(1000L).withTryNum(1).build();

        Callable<Integer> callable = new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                Thread.sleep(100000L);
                return 90;
            }

        };

        Integer call;
        try {
            call = retry.call(callable);
        } catch (RetryException e) {
            LOG.log(Level.SEVERE, "", e);
        }

    }
}
