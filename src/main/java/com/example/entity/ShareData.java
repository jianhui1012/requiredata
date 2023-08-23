package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;


@Data
@TableName("sharedata")
public class ShareData extends Model<ShareData> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 需求部门 
      */
    @ApiModelProperty(value = "需求部门")
    private String requireDepart;

    /**
      * 需求部门数据资产管理员 
      */
    @ApiModelProperty(value = "需求部门数据资产管理员")
    private String requireManager;

    /**
      * 需求指标 
      */
    @ApiModelProperty(value = "需求指标")
    private String requireTarget;

    /**
      * 需求信息 
      */
    @ApiModelProperty(value = "需求信息")
    private String requireMessage;

    /**
      * 数据 
      */
    @ApiModelProperty(value = "数据")
    private String monthData;

    /**
      * 单位 
      */
    @ApiModelProperty(value = "单位")
    private String unit;

    /**
      * 数据来源部门 
      */
    @ApiModelProperty(value = "数据来源部门")
    private String sourceDepart;

    /**
      * 来源部门数据资产管理员 
      */
    @ApiModelProperty(value = "来源部门数据资产管理员")
    private String sourceManager;

    /**
      * 频次 
      */
    @ApiModelProperty(value = "频次",example = "每月")
    private String frequency;

    /**
      * 用途 
      */
    @ApiModelProperty(value = "用途")
    private String fun;

    /**
     * 统计时间
     */
    @ApiModelProperty(value = "统计时间",hidden = true,example = "0")
    private String time;

}