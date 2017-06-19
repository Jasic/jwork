package org.jasic.jhessian.server;


import java.util.ArrayList;
import java.util.List;

import com.caucho.hessian.server.HessianServlet;

/**
 * 在Hessian中服务端的必须继承HessianServlet类同时实现相关的服务的接口
 *
 * @author longgangbai
 */
public class HessianWS extends HessianServlet implements IHessianService {
    private String _greeting = "Hello, world";

    /**
     *
     */
    public void setGreeting(String greeting) {
        _greeting = greeting;
    }

    /**
     *
     */
    public String hello() {
        return _greeting;
    }

    /**
     *
     */
    public List<String> getList() {
        List<String> strList = new ArrayList<String>();
        strList.add("a");
        strList.add("c");
        strList.add("b");
        strList.add("d");
        strList.add("e");
        strList.add("g");
        strList.add("h");
        strList.add("j");
        strList.add("k");
        strList.add("l");
        strList.add("q");
        return strList;
    }

    /**
     *
     */
    public String getUserName() {
        return _greeting;
    }

}
