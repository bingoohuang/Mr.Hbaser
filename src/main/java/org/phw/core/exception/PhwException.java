package org.phw.core.exception;

/**
 * Base checked exception for PHW framework.
 * @author zhuxb
 *
 */
public class PhwException extends Exception {
    private static final long serialVersionUID = 3703025185914448822L;
    private String messageCode;
    private String[] replaceStrs = null;

    /**
     * 若此字段不为null和""，则页面上显示它，
     * 忽略messageCode，不通过messageCode取值。
     */
    private String displayMsg = null;

    /**
     * default ctor.
     */
    public PhwException() {
        super();
    }

    /**
     * ctor.
     * @param message 异常消息
     */
    public PhwException(String message) {
        super(message);
    }

    /**
     * ctor.
     * @param message 异常消息
     * @param cause 
     */
    public PhwException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * ctor.
     * @param cause 
     */
    public PhwException(Throwable cause) {
        super(cause);
    }

    /**
     * ctor.
     * @param messageCode 异常代码
     * @param message 异常消息
     */
    public PhwException(String messageCode, String message) {
        super(message);
        setMessageCode(messageCode);

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
    public PhwException(String messageCode, String message, Throwable cause) {
        super(message, cause);
        setMessageCode(messageCode);
    }

    /**
     * ctor.
     * @param messageCode 异常编码
     * @param message 异常消息
     * @param replaceStrs 替换字符串
     */
    public PhwException(String messageCode, String message, String... replaceStrs) {
        super(message);

        setReplaceStrs(replaceStrs);
    }

    /**
     * ctor.
     * @param messageCode 异常编码
     * @param message 异常消息
     * @param cause 异常致因
     * @param replaceStrs 替换字符串
     */
    public PhwException(String messageCode, String message, Throwable cause, String... replaceStrs) {
        super(message, cause);
        setMessageCode(messageCode);
        setReplaceStrs(replaceStrs);
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

    /**
     * 设置替换字符串数组。
     * @param replaceStrs 替换字符串数组
     */
    public void setReplaceStrs(String[] replaceStrs) {
        this.replaceStrs = replaceStrs;
    }

    /**
     * 取得替换字符串数组。
     * @return 替换字符串数组
     */
    public String[] getReplaceStrs() {
        return replaceStrs;
    }

    public void setDisplayMsg(String displayMsg) {
        this.displayMsg = displayMsg;
    }

    public String getDisplayMsg() {
        return displayMsg;
    }

}
