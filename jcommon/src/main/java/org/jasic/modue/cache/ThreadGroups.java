package org.jasic.modue.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jasic
 * @date 2011-9-8
 * All used ThreadGroups
 */
public class ThreadGroups {

    private ThreadGroups() {
    }

    private static Map<String, ThreadGroup> THREADGROUPMAP = new ConcurrentHashMap<String, ThreadGroup>();

    public static Map<String, ThreadGroup> getTHREADGROUPMAP() {
        return THREADGROUPMAP;
    }


    /**
     * getThreadGroup by name
     *
     * @param clazz
     * @return
     */
    public static ThreadGroup getThreadGroup(String tgName) {
        ThreadGroup tg = null;
        if (tgName != null) {
            tg = THREADGROUPMAP.get(tgName);
            if (tg == null) {
                tg = new ThreadGroup(tgName);
                synchronized (THREADGROUPMAP) {
                    THREADGROUPMAP.put(tgName, tg);
                }
            }
        }
        return tg;
    }
}
