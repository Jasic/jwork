package org.jasic.modue.moduleface;

import org.jasic.modue.annotation.Service;

/**
 * Created with IntelliJ IDEA.
 * User: Jasic
 * Date: 12-9-21
 */
public interface IService {

    /**
     * 服务接口入口
     */
    @Service
    public void service();
}
