package com.art.artsearch.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * description
 *
 * @author lou
 * @create 2022/5/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(indexName = "product",shards = 3,replicas = 1)
public class Product {
    @Id
    private int id;
    @Field(type = FieldType.Text)
    private String name;
    @Field(type = FieldType.Keyword)
    private String category;
}
