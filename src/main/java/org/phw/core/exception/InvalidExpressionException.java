package org.phw.core.exception;

/**
 * 非法表达式异常。
 * @author BingooHuang
 *
 */
public class InvalidExpressionException extends BaseException {
    private static final long serialVersionUID = 5782368156021656402L;

    /**
     * default ctor.
     */
    public InvalidExpressionException() {
        super();
    }

    /**
     * ctor.
     * @param message 异常消息
     */
    public InvalidExpressionException(String message) {
        super(message);
    }

    /**
     * ctor.
     * @param message 异常消息
     * @param cause 
     */
    public InvalidExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * ctor.
     * @param cause 
     */
    public InvalidExpressionException(Throwable cause) {
        super(cause);
    }

    /**
     * ctor.
     * @param messageCode 异常代码
     * @param message 异常消息
     */
    public InvalidExpressionException(String messageCode, String message) {
        super(messageCode, message);
    }

    /**
     * ctor.
     * @param messageCode 异常代码
     * @param message 异常消息
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public InvalidExpressionException(String messageCode, String message, Throwable cause) {
        super(messageCode, message, cause);
    }
}
