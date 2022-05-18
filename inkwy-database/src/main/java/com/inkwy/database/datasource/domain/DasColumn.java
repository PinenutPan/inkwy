package com.inkwy.database.datasource.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 原始jdbc字段对象

 * @ClassName DasColumn
 * @Version 1.0
 * @create 2019/7/17 16:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DasColumn {
    private int columnIndex;

    private String columnName;

    private String columnTypeName;

    private String columnClassName;

    private String columnComment;
    private int isNull;
    private boolean isprimaryKey;
}
