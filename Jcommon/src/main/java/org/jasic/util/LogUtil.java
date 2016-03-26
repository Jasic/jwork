package org.jasic.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Jasic
 * Date: 13-9-18
 */
public abstract class LogUtil {

    public static Logger getLogger(Class<?> clazz, String logHeader) {
        return LoggerFactory.getLogger(clazz);
    }

}
