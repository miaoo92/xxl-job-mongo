package com.avon.rga.admin.core.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by xuxueli on 16/9/30.
 */
@Data
@Document(collection = "XxlJobRegistry")
public class XxlJobRegistry {

    @Id
    private int id;
    private String registryGroup;
    private String registryKey;
    private String registryValue;
    private Date updateTime;
}
