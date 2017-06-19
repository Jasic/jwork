package org.jasic.spring.transaction.dao;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @Author 菜鹰.
 * @Date 2015/1/5
 */
public class GenericDaoImpl implements GenericDao {

    private JdbcTemplate jdbcTemplateA;
    private JdbcTemplate jdbcTemplateB;

    public void setJdbcTemplateA(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplateA = jdbcTemplate;
    }

    public void setJdbcTemplateB(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplateB = jdbcTemplate;
    }

    public int save(String ds, String sql, Object[] obj) throws Exception {
        if (null == ds || "".equals(ds)) return -1;
        try {
            if (ds.equals("A")) {
                return this.jdbcTemplateA.update(sql, obj);
            } else {
                return this.jdbcTemplateB.update(sql, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("执行" + ds + "数据库时失败！");
        }
    }

    public int findRowCount(String ds, String sql) {
        if (null == ds || "".equals(ds)) return -1;

        if (ds.equals("A")) {
            return this.jdbcTemplateA.queryForInt(sql);
        } else {
            return this.jdbcTemplateB.queryForInt(sql);
        }
    }

}

