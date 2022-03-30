package com.art.artcommon.entity;

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
}
