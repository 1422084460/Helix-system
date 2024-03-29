package com.art.artcreator.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description
 *
 * @author lou
 * @create 2022/3/22
 */
@Data
@Accessors(chain = true)
public class NamePackage {

    private int nid;
    private String name;
    private String style;
    private String category;
    private String area;
    private int score;
    private String nameId;
    private String email;
    private boolean isAdopted;
}
