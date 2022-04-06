package com.avon.rga.admin.core.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@Document(collection = "sequences")
public class Sequence implements Serializable {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    @Field("current_val")
    private Long currentVal;
}
