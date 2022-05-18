package com.inkwy.database.datasource.domain;

import lombok.Data;

/**
 * 字段信息

 * @version 1.0
 * @create 2019/7/30
 */
@Data
public class ColumnInfo {
    private int index;
    /**
     * 字段名称
     */
    private String name;
    /**
     * 注释
     */
    private String comment;
    /**
     * 字段类型
     */
    private String type;

    /**
     * 是否是主键列
     */
    private Boolean ifPrimaryKey;
    /**
     * 是否可为null   0 不可为空  1 可以为null
     */
    private int isnull;
}
