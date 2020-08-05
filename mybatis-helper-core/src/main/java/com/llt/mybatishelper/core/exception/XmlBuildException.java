package com.llt.mybatishelper.core.exception;

/**
 * @author LILONGTAO
 * @date 2020-08-05
 */
public class XmlBuildException extends MybatisHelperException{
    public XmlBuildException() {
    }

    public XmlBuildException(String message) {
        super(message);
    }

    public XmlBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlBuildException(Throwable cause) {
        super(cause);
    }

    public XmlBuildException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
