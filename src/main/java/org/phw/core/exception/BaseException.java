package org.phw.core.exception;

/**
 * 基础异常。
 * @author BingooHuang
 *
 */
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 7014095800433392597L;

    private String messageCode;

    /**
     * 构造函数。
     */
    public BaseException() {
        super();
    }

    /**
     * 构造函数。
     * @param message 异常消息
     */
    public BaseException(String message) {
        super(message);
    }

    /**
     * 构造函数。
     * @param message 异常消息
     * @param cause 异常致因
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数。
     * @param cause 异常致因
     */
    public BaseException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造函数。
     * @param messageCode 异常编码
     * @param message 异常消息
     */
    public BaseException(String messageCode, String message) {
        super(message);
        setMessageCode(messageCode);

    }

    /**
     * 构造函数。
     * @param messageCode 异常编码
     * @param message 异常消息
     * @param cause 异常致因
     */
    public BaseException(String messageCode, String message, Throwable cause) {
        super(message, cause);
        setMessageCode(messageCode);
    }

    /**
     * 取得异常编码。
     * @return 异常编码
     */
    public String getMessageCode() {
        return messageCode;
    }

    /**
     * 设置异常编码。
     * @param messageCode 异常编码
     */
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

}
