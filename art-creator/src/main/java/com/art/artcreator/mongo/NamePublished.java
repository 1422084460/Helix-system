package com.art.artcreator.mongo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description
 * 已生成名集合实体类
 * @author lou
 * @create 2022/9/15
 */
@Entity(value = "NamePublished",noClassnameStored = true)
@Data
@Accessors(chain = true)
public class NamePublished {

    @Id
    private Object id;
    private int nid;
    private String name;
    private String style;
    private String category;
    private String area;
    private int score;
    private String nameId;
    private String email;
    private Boolean isAdopted;
}
