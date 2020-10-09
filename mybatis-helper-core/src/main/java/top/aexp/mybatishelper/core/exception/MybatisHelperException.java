package top.aexp.mybatishelper.core.exception;

/**
 * @author LILONGTAO
 * @date 2020-08-05
 */
public class MybatisHelperException  extends RuntimeException{
    public MybatisHelperException() {
    }

    public MybatisHelperException(String message) {
        super(message);
    }

    public MybatisHelperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MybatisHelperException(Throwable cause) {
        super(cause);
    }

    public MybatisHelperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
