package top.aexp.mybatishelper.core.exception;

/**
 * @author LILONGTAO
 * @date 2020-08-05
 */
public class SqlExecException extends MybatisHelperException{
    public SqlExecException() {
    }

    public SqlExecException(String message) {
        super(message);
    }

    public SqlExecException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlExecException(Throwable cause) {
        super(cause);
    }

    public SqlExecException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
