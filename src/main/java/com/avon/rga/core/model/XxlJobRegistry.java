package com.avon.rga.core.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by xuxueli on 16/9/30.
 */
@Data
@Document(collection = "xxlJobRegistry")
public class XxlJobRegistry {

    private int id;
    private String registryGroup;
    private String registryKey;
    private String registryValue;
    private Date updateTime;

}
