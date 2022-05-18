package com.inkwy.database.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className CompareResultDto
 * @description 比较两个库返回信息
 * @author jianpingpan
 * @date 2018/12/26 14:33
 */
@Data
@ApiModel("比较两个库返回信息")
public class CompareAllResultDto implements Serializable {
    @ApiModelProperty("库名")
    private String schemaName;
    @ApiModelProperty("返回信息")
    private String msg;
    @ApiModelProperty("比较的表结果集")
    private List<CompareResultDto> compareResultDtoList;
}
