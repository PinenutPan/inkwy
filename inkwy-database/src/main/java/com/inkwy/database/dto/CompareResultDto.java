package com.inkwy.database.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @className CompareResultDto
 * @description 比较两个schema返回信息
 * @author jianpingpan
 * @date 2018/12/26 14:33
 */
@Data
@ApiModel("比较两个schema返回信息")
public class CompareResultDto implements Serializable {
    @ApiModelProperty("表名")
    private String tableName;
    @ApiModelProperty("返回信息")
    private String msg;
}
