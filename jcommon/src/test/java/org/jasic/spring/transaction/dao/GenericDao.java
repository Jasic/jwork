package org.jasic.spring.transaction.dao;

/**
 * @Author 菜鹰.
 * @Date 2015/1/5
 */
public interface GenericDao {

    public int save(String ds, String sql, Object[] obj) throws Exception;

    public int findRowCount(String ds, String sql);

}
