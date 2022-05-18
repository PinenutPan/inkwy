package com.inkwy.database.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className SchemaDto
 * @description 数据库的库信息
 * @author jianpingpan
 * @date 2018/12/26 14:33
 */
@Data
@ApiModel("数据库的库信息")
public class SchemaDto implements Serializable {

    @ApiModelProperty("库名")
    private String schemaName;
    @ApiModelProperty("表信息")
    private List<TableDto> tableDtoList;
}
