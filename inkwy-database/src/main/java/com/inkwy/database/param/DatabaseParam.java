package com.inkwy.database.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@ApiModel("比较数据传参数")
public class DatabaseParam implements Serializable {
    @ApiModelProperty("数据源类型: 1 mysql; 2 oracle")
    private Integer type;
    @ApiModelProperty("源数据库url")
    private String sourceUrl;
    @ApiModelProperty("源数据库用户名")
    private String sourceUsername;
    @ApiModelProperty("源数据库密码")
    private String sourcePassword;
    @ApiModelProperty("源数据的库名")
    private String sourceSchema;
    @ApiModelProperty("目标数据库url")
    private String targetUrl;
    @ApiModelProperty("目标数据库用户名")
    private String targetUsername;
    @ApiModelProperty("目标数据库密码")
    private String targetPassword;
    @ApiModelProperty("目标数据的库名")
    private String targetSchema;

}
