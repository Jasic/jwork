package org.jasic.jhessian.server;

/**
 * @Author 菜鹰.
 * @Date 14-9-29
 */

import java.util.List;

/**
 * 声明服务端相关的服务
 *
 * @author 菜鹰
 */
public interface IHessianService {

    public String getUserName();

    public List<String> getList();

    public void setGreeting(String greeting);

    public String hello();

}
