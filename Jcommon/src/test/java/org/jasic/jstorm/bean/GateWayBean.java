package org.jasic.jstorm.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author
 * @Date 2016/3/22
 * @Explain:
 */
@Data
public class GateWayBean implements Serializable{

    private static final long serialVersionUID = 98688304660468928L;

    private String beanName;

    private Long beanId;

    private Integer count;
}
