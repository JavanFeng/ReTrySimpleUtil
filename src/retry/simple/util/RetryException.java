package retry.simple.util;

/**
 * 
 * <p>
 * 类描述:错误信息
 * </p>
 * 
 * @since 2018年11月16日
 * @author Jiafeng Feng
 */
public class RetryException extends Exception {
    /** serialVersionUID */
    private static final long serialVersionUID = 5208927601358056313L;
    /** 原来的错误 */
    private Throwable originalThrowable;
    /** 错误信息 */
    private String msg;

    /**
     * @return the originalThrowable
     */
    public Throwable getOriginalThrowable() {
        return originalThrowable;
    }

    /**
     * @param originalThrowable
     *            要设置的 originalThrowable
     */
    public void setOriginalThrowable(Throwable originalThrowable) {
        this.originalThrowable = originalThrowable;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg
     *            要设置的 msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 构造
     * 
     * @param originalThrowable
     *            原始错误
     * @param msg
     *            错误信息
     */
    public RetryException(Throwable originalThrowable, String msg) {
        super();
        this.originalThrowable = originalThrowable;
        this.msg = msg;
    }

    private StackTraceElement[] concat(StackTraceElement[] a, StackTraceElement[] b) {
        StackTraceElement[] c = new StackTraceElement[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#printStackTrace()
     */
    @Override
    public void printStackTrace() {
        if (originalThrowable != null) {
            StackTraceElement[] stackTrace = super.getStackTrace();
            StackTraceElement[] concat = concat(stackTrace, originalThrowable.getStackTrace());
            super.setStackTrace(concat);
        }
        super.printStackTrace();
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        if (originalThrowable != null) {
            this.msg = originalThrowable.getMessage();
        }
        return this.msg;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();
        build.append(this.getClass().getName());
        build.append(":");
        build.append(msg);
        build.append("\r\n");

        StackTraceElement[] stackTrace = this.originalThrowable.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            build.append("\t");
            build.append("at ");
            build.append(stackTraceElement.toString());
            build.append("\r\n");
        }

        build.delete(build.length() - 2, build.length());

        return build.toString();
    }

}