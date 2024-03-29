package com.avon.rga.admin.core.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "XxlJobLogReport")
public class XxlJobLogReport {

    @Id
    private int id;

    private Date triggerDay;

    private int runningCount;
    private int sucCount;
    private int failCount;

}
