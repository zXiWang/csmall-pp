package com.xiwang.csmall.passport.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色(Role)实体类
 *
 * @author 夕妄
 * @since 2022-10-10 11:59:21
 */
@Data
@TableName("ams_role")
public class Role implements Serializable {
    private static final long serialVersionUID = -39989739514909827L;
    
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 自定义排序序号
     */
    private Integer sort;
    /**
     * 数据创建时间
     */
    private Date gmtCreate;
    /**
     * 数据最后修改时间
     */
    private Date gmtModified;


}

