package com.xiwang.csmall.product.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 类别(PmsCategory)实体类
 *
 * @author 夕妄
 * @since 2022-09-26 09:11:15
 */
@Data
@TableName("pms_category")
public class CategoryListItemVO implements Serializable {
    private static final long serialVersionUID = -19515084917614064L;
    /**
     * 记录id
     */
    private Long id;
    /**
     * 类别名称
     */
    private String name;
    /**
     * 父级类别id，如果无父级，则为0
     */
    private Long parentId;
    /**
     * 深度，最顶级类别的深度为1，次级为2，以此类推
     */
    private Integer depth;
    /**
     * 关键词列表，各关键词使用英文的逗号分隔
     */
    private String keywords;
    /**
     * 自定义排序序号
     */
    private Integer sort;
    /**
     * 图标图片的URL
     */
    private String icon;
    /**
     * 是否启用，1=启用，0=未启用
     */
    private Integer enable;
    /**
     * 是否为父级（是否包含子级），1=是父级，0=不是父级
     */
    private Integer isParent;
    /**
     * 是否显示在导航栏中，1=启用，0=未启用
     */
    private Integer isDisplay;

}

