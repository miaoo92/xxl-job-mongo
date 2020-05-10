package com.avon.rga.service;

import com.avon.rga.core.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author winston.xu@avon.com
 * @date last modified: 2020/5/10
 */

@Component
@Slf4j
public class IdGenerator {

    @Autowired
    private MongoTemplate mongoTemplate;

    private volatile ConcurrentHashMap<Class<?>, Long> id = new ConcurrentHashMap<>();

    private volatile boolean firstGet;

    private final int size = 10;

    private static HashMap<Class<?>, String> seqMap;

    static {
        seqMap = new HashMap<>();
        seqMap.put(XxlJobGroup.class, "xxlJobGroup_seq");
        seqMap.put(XxlJobInfo.class, "xxlJobInfo_seq");
        seqMap.put(XxlJobLog.class, "xxlJobLog_seq");
        seqMap.put(XxlJobLogGlue.class, "xxlJobLogGlue_seq");
        seqMap.put(XxlJobLogReport.class, "xxlJobLogReport_seq");
        seqMap.put(XxlJobRegistry.class, "xxlJobRegistry_seq");
        seqMap.put(XxlJobUser.class, "xxlJobUser_seq");
    }

    @PostConstruct
    private void init() {
        long startTime = System.currentTimeMillis();

        seqMap.forEach((k, v) -> {
            Sequence seq = mongoTemplate.findAndModify(
                    new Query(where("name").is(v)),
                    new Update().inc("current_val", size),
                    new FindAndModifyOptions().upsert(true).returnNew(true),
                    Sequence.class, "sequences");
            id.put(k, seq.getCurrentVal() - size);
        });

        firstGet = true;

        log.debug("Time spent on generating id when booting up: [{}]", System.currentTimeMillis() - startTime);
    }

    public synchronized Long getNext(Class<?> clazz) {
        Long currentId = id.get(clazz);
        currentId ++;

        if (!firstGet && (id.get(clazz) - 1L) % size == 0) {
            Sequence seq = mongoTemplate.findAndModify(
                    new Query(where("name").is(seqMap.get(clazz))),
                    new Update().inc("current_val", size),
                    new FindAndModifyOptions().returnNew(true),
                    Sequence.class, "sequences");

            id.put(clazz, seq.getCurrentVal() - size + 1L);
        }

        firstGet = false;
        return currentId;
    }

}




