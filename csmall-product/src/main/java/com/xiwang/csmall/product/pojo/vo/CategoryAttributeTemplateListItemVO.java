package com.xiwang.csmall.product.pojo.vo;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("pms_category_attribute_template")
public class CategoryAttributeTemplateListItemVO {

    private long id;
    private long categoryId;
    private long attributeTemplateId;


}
