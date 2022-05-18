package com.inkwy.database.datasource.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@ApiModel("数据源表")
public class DataSource implements Serializable {

	@ApiModelProperty(value = "id",hidden = true)
	private Long id;

	@ApiModelProperty(value = "状态：0 禁用；1 启用",hidden = true)
	private Integer status;

	@ApiModelProperty(value = "数据源名字",hidden = true)
	private String name;

	@ApiModelProperty(value = "数据源描述",hidden = true)
	private String remark;
	//1 mysql; 2 oracle ;3 hive;4 S3;5 ftp;7 txtfile
	@ApiModelProperty("数据源类型: 1 mysql; 2 oracle")
	private Integer type;

	@ApiModelProperty("账号(加密)")
	private String username;

	@ApiModelProperty("密码(加密)")
	private String password;

	@ApiModelProperty("链接地址")
	private String url;

	@ApiModelProperty(value = "数据库名")
	private String schema;

	@ApiModelProperty(value = "云endpoint",hidden = true)
	private String endpoit;

	@ApiModelProperty(value = "云bucket",hidden = true)
	private String bucketName;

	@ApiModelProperty(value = "其他参数",hidden = true)
	private String parameter;

}
