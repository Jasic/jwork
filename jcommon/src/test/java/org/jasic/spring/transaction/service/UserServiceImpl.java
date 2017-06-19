package org.jasic.spring.transaction.service;

import org.jasic.spring.transaction.dao.GenericDao;

/**
 * @Author 菜鹰.
 * @Date 2015/1/5
 */
public class UserServiceImpl implements UserService {

    private GenericDao genericDao;

    public void setGenericDao(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    public void saveUser() throws Exception {
        String userName = "user_" + Math.round(Math.random() * 10000);
        System.out.println(userName);

        StringBuilder sql = new StringBuilder();
        sql.append(" insert into t_user(username, gender) values(?,?); ");
        Object[] objs = new Object[]{userName, "1"};

        genericDao.save("A", sql.toString(), objs);

        sql.delete(0, sql.length());
        sql.append(" insert into t_user(name, sex) values(?,?); ");
        objs = new Object[]{userName, "男的"};//值超出范围
        genericDao.save("B", sql.toString(), objs);
    }

}
