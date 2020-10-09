package top.aexp.mybatishelper.core.exception;

/**
 * @author LILONGTAO
 * @date 2020-08-05
 */
public class EntityBuildException extends MybatisHelperException{
    public EntityBuildException() {
    }

    public EntityBuildException(String message) {
        super(message);
    }

    public EntityBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityBuildException(Throwable cause) {
        super(cause);
    }

    public EntityBuildException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
