package com.art.artsearch.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Document(indexName = "user",shards = 3)
public class Use {
    @Id
    private Integer id;
    @Field(type = FieldType.Text)
    private String username;
    @Field(type = FieldType.Keyword)
    private String email;
    @Field(type = FieldType.Text)
    private String password;
    @Field(type = FieldType.Text)
    private String avatar;
    @Field(type = FieldType.Double)
    private BigDecimal money;
    @Field(type = FieldType.Text)
    private String is_avatar_prepare;
    @Field(type = FieldType.Text)
    private String status;
    @Field(type = FieldType.Text)
    private String is_admin;
    @Field(type = FieldType.Integer)
    private int score;
    @Field(type = FieldType.Text)
    private String phone_num;
    @Field(type = FieldType.Text)
    private String create_time;
}
