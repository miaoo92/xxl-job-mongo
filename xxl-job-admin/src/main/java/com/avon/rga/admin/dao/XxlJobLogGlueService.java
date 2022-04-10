package com.avon.rga.admin.dao;

import com.avon.rga.admin.core.model.XxlJobLogGlue;
import com.avon.rga.admin.service.IdGenerator;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * job log for glue
 * @author xuxueli 2016-5-19 18:04:56
 */
@Service
public class XxlJobLogGlueService extends BaseMongoServiceImpl<XxlJobLogGlue> {

	public int save(XxlJobLogGlue xxlJobLogGlue){
		return super.save(xxlJobLogGlue);
	}

	public List<XxlJobLogGlue> findByJobId(int jobId){
		Query query = new Query(where("jobId").is(jobId));
		return super.find(query);
	}

	public long removeOld(String jobId, int limit){
		Query query = new Query(where("jobId").is(jobId));
		query.limit(limit);
		List<XxlJobLogGlue> xxlJobLogGlues = super.find(query);
		List<Integer> collect = xxlJobLogGlues.stream().map(XxlJobLogGlue::getId).collect(Collectors.toList());
		Query query1 = new Query(where("id").nin(collect)).addCriteria(where("jobId").is(jobId));
		DeleteResult remove = super.remove(query1);
		return remove.getDeletedCount();
	}

	public int deleteByJobId(int jobId){
		Query query = new Query(where("jobId").is(jobId));
		DeleteResult deleteResult = super.remove(query);
		return (int)deleteResult.getDeletedCount();
	}
}
