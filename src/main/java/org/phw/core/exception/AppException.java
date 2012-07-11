package org.phw.core.exception;

/**
 * 应用异常。
 * @author BingooHuang
 *
 */
public class AppException extends BaseException {
    private static final long serialVersionUID = -1846968432286788402L;

    /**
     * 构造函数。
     */
    public AppException() {
        super();
    }

    /**
     * 构造函数。
     * @param message 异常消息
     */
    public AppException(String message) {
        super(message);
    }

    /**
     * 构造函数。
     * @param message 异常消息
     * @param cause 异常致因
     */
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数。
     * @param cause 异常致因
     */
    public AppException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造函数。
     * @param messageCode 异常编码
     * @param message 异常消息
     */
    public AppException(String messageCode, String message) {
        super(messageCode, message);

    }

    /**
     * 构造函数。
     * @param messageCode 异常编码
     * @param message 异常消息
     * @param cause 异常致因
     */
    public AppException(String messageCode, String message, Throwable cause) {
        super(messageCode, message, cause);
    }
}
