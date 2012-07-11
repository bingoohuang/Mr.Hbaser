package org.phw.core.lang;

import java.util.concurrent.TimeUnit;

/**
 * 线程相关小函数。
 * @author BingooHuang
 *
 */
public class Threads {
    /**
     * 休眠。
     * @param millis 休眠毫秒数
     */
    public static void sleepMillis(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        }
        catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * 休眠。
     * @param seconds 休眠秒数
     */
    public static void sleepSeconds(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        }
        catch (InterruptedException e) {
            // ignore
        }
    }
}
