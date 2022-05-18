package com.inkwy.database.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @packageName com.manage.common.dto.database
 * @className ColumnDto
 * @description 表的列名信息
 * @author jianpingpan
 * @date 2018/12/26 14:33
 */
@Data
@ApiModel("表的列名信息")
public class ColumnDto implements Serializable {
    @ApiModelProperty("表名")
    private String tableName;
    @ApiModelProperty("列名")
    private String columnName;
    @ApiModelProperty("列名描述")
    private String columnComment;
    @ApiModelProperty("数据类型")
    private String dataType;
    @ApiModelProperty("列类型")
    private String columnType;
    @ApiModelProperty("列默认值")
    private String columnDefault;
    @ApiModelProperty("是否空值")
    private String isNullable;
}
