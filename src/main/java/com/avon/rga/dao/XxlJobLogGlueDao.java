package com.avon.rga.dao;

import com.avon.rga.core.model.XxlJobLogGlue;
import com.mongodb.client.result.DeleteResult;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * job log for glue
 *
 */
@Service
public class XxlJobLogGlueDao extends BaseMongoServiceImpl<XxlJobLogGlue> {


    public List<XxlJobLogGlue> findByJobId(int jobId){
        Query query = new Query(where("jobId").is(jobId));
        return super.find(query);
    }

    public int removeOld(int jobId, int limit){
        return 0;
    }

    public int deleteByJobId(int jobId){
        Query query = new Query(where("jobId").is(jobId));
        DeleteResult deleteResult = super.remove(query);
        return (int)deleteResult.getDeletedCount();
    }

}
