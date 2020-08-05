package com.llt.mybatishelper.core.exception;

/**
 * @author LILONGTAO
 * @date 2020-08-05
 */
public class MapperBuildException extends MybatisHelperException{
    public MapperBuildException() {
    }

    public MapperBuildException(String message) {
        super(message);
    }

    public MapperBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperBuildException(Throwable cause) {
        super(cause);
    }

    public MapperBuildException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
