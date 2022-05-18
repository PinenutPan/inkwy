package com.inkwy.database.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className TableDto
 * @description 数据库表信息
 * @author jianpingpan
 * @date 2018/12/26 14:33
 */
@Data
@ApiModel("数据库表信息")
public class TableDto implements Serializable {

    @ApiModelProperty("表名描述")
    private String tableComment;
    @ApiModelProperty("表名")
    private String tableName;
    @ApiModelProperty("列信息")
    private List<ColumnDto> columnDtoList;
}
