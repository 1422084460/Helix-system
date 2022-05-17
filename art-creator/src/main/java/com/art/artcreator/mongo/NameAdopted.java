package com.art.artcreator.mongo;

import com.art.artcreator.entity.NamePackage;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * description
 * mongo NameAdopted集合实体类
 * @author lou
 * @create 2022/3/25
 */
@Entity(value = "NameAdopted",noClassnameStored = true)
@Data
@Accessors(chain = true)
public class NameAdopted {
    @Id
    private Object id;
    private String email;
    private List<NamePackage> nameList;
}
