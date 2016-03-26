package org.jasic.spring.transaction;

import org.jasic.spring.transaction.service.UserService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author 菜鹰.
 * @Date 2015/1/5
 */
public class TestUserService {

    private static UserService userService;

    @BeforeClass
    public static void init() {
        ApplicationContext app = new ClassPathXmlApplicationContext("applicationContext-atomikos.xml");
        userService = (UserService) app.getBean("userService");
    }

    @Test
    public void save() {
        System.out.println("begin...");
        try {
            userService.saveUser();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("finish...");
    }

}

