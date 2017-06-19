package org.jasic.jhessian.client;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;
import org.jasic.jhessian.server.IHessianService;

/**
 * Hessian客户端测试代码
 *
 * @author 菜鹰
 */
public class HessianServiceWSClient {
    public static void main(String[] args) {
        String url = "http://localhost:8080/jhessian/HessianWS";

        HessianProxyFactory factory = new HessianProxyFactory();
        IHessianService basic;
        try {
            basic = (IHessianService) factory.create(IHessianService.class, url);

            System.out.println("hello(): " + basic.getUserName());
            System.out.println("getList(): " + basic.getUserName());

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
