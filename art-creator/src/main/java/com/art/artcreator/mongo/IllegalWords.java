package com.art.artcreator.mongo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description
 * 非法字符类
 * @author lou
 * @create 2022/10/14
 */
@Entity(value = "IllegalWords",noClassnameStored = true)
@Data
@Accessors(chain = true)
public class IllegalWords {

    @Id
    private Object id;
    private String word;
}
