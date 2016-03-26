package org.jasic.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * User: Jasic
 * Date: 13-10-18
 */
public abstract class ExceptionUtil {

    /**
     * 根据异常返回异常堆栈字符串
     *
     * @return
     */
    public static String getExceptionStackTrace(Exception e, String charsetName) {
        StringBuilder sb = new StringBuilder();
        PrintWriter pw = null;
        ByteArrayOutputStream b = null;
        try {
            b = new ByteArrayOutputStream();
            pw = new PrintWriter(b);
            e.printStackTrace(pw);
            pw.close();
            sb.append(b.toString(charsetName));
        } catch (Exception e2) {
            sb.append("\n " + ExceptionUtil.class + "Exception happen ");
        } finally {
            if (pw != null) pw.close();
            if (b != null) try {
                b.close();
            } catch (IOException e1) {
            }
        }
        return sb.toString();
    }

    /**
     * 根据异常返回异常堆栈字符串
     *
     * @return
     */
    public static String getExceptionStackTrace(Exception e) {
        return getExceptionStackTrace(e, Charset.defaultCharset().name());
    }

    /**
     * 根据异常返回异常堆栈字符串
     *
     * @param e
     * @return
     */
    public static String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder(e.toString() + "\n\t");
        StackTraceElement[] elements = e.getStackTrace();
        if (elements != null)
            for (StackTraceElement element : elements) {
                sb.append(element + "\n\t");
            }
        return sb.toString();
    }
}
