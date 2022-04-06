package com.avon.rga.admin.dao;

import com.avon.rga.admin.core.model.XxlJobInfo;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * job info
 *
 * @author xuxueli 2016-1-12 18:03:45
 */
@Service
public class XxlJobInfoService extends BaseMongoServiceImpl<XxlJobInfo> {

    private Query pageListQuery(int offset, int pagesize, int jobGroup, int triggerStatus,
                                String jobDesc, String executorHandler, String author) {
        Query query = new Query();
        if (jobGroup > 0) {
            query.addCriteria(where("jobGroup").is(jobGroup));
        }
        if (triggerStatus >= 0) {
            query.addCriteria(where("triggerStatus").is(triggerStatus));
        }
        if (StringUtils.isNotEmpty(jobDesc)) {
            query.addCriteria(where("jobDesc").regex(jobDesc));
        }
        if (StringUtils.isNotEmpty(executorHandler)) {
            query.addCriteria(where("executorHandler").regex(executorHandler));
        }
        if (StringUtils.isNotEmpty(author)) {
            query.addCriteria(where("author").regex(author));
        }
        query.skip(offset);
        query.limit(pagesize);
        return query;
    }

    public List<XxlJobInfo> pageList(int offset,
                                     int pagesize,
                                     int jobGroup,
                                     int triggerStatus,
                                     String jobDesc,
                                     String executorHandler,
                                     String author) {
        Query query = pageListQuery(offset, pagesize, jobGroup, triggerStatus, jobDesc, executorHandler, author);
        query.with(Sort.by(Sort.Direction.DESC, "id"));
        return super.find(query);
    }

    public long pageListCount(int offset,
                              int pagesize,
                              int jobGroup,
                              int triggerStatus,
                              String jobDesc,
                              String executorHandler,
                              String author) {
        Query query = pageListQuery(offset, pagesize, jobGroup, triggerStatus, jobDesc, executorHandler, author);
        return super.count(query);
    }

    public int save(XxlJobInfo info) {
        return super.save(info);
    }

    public XxlJobInfo loadById(int id) {
        Query query = new Query(where("id").is(id));
        return super.findOne(query);
    }

    public int update(XxlJobInfo xxlJobInfo) {
        Query query = new Query(where("_id").is(xxlJobInfo.getId()));
        Update update = new Update();
        update.set("jobGroup", xxlJobInfo.getJobGroup());
        update.set("jobDesc", xxlJobInfo.getJobDesc());
        update.set("addTime", xxlJobInfo.getAddTime());
        update.set("updateTime", xxlJobInfo.getUpdateTime());
        update.set("author", xxlJobInfo.getAuthor());
        update.set("alarmEmail", xxlJobInfo.getAlarmEmail());
        update.set("executorRouteStrategy", xxlJobInfo.getExecutorRouteStrategy());
        update.set("executorHandler", xxlJobInfo.getExecutorHandler());
        update.set("executorParam", xxlJobInfo.getExecutorParam());
        update.set("executorBlockStrategy", xxlJobInfo.getExecutorBlockStrategy());
        update.set("executorTimeout", xxlJobInfo.getExecutorTimeout());
        update.set("executorFailRetryCount", xxlJobInfo.getExecutorFailRetryCount());
        update.set("glueType", xxlJobInfo.getGlueType());
        update.set("glueSource", xxlJobInfo.getGlueSource());
        update.set("glueRemark", xxlJobInfo.getGlueRemark());
        update.set("glueUpdatetime", xxlJobInfo.getGlueUpdatetime());
        update.set("childJobId", xxlJobInfo.getChildJobId());
        update.set("triggerStatus", xxlJobInfo.getTriggerStatus());
        update.set("triggerLastTime", xxlJobInfo.getTriggerLastTime());
        update.set("triggerNextTime", xxlJobInfo.getTriggerNextTime());
        UpdateResult update1 = super.update(query, update);
        return (int) update1.getModifiedCount();
    }

    public long delete(long id) {
        Query query = new Query(where("_id").is(id));
        DeleteResult remove = super.remove(query);
        return remove.getDeletedCount();
    }

    public List<XxlJobInfo> getJobsByGroup(int jobGroup) {
        Query query = new Query(where("jobGroup").is(jobGroup));
        return super.find(query);
    }

    public long findAllCount() {
        return super.count(new Query(where("_id").exists(true)));
    }

    public List<XxlJobInfo> scheduleJobQuery(long maxNextTime, int pagesize) {
        Query query = new Query();
        query.addCriteria(where("triggerStatus").is(1));
        query.addCriteria(where("triggerNextTime").lte(maxNextTime));
        query.limit(pagesize);
        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        return super.find(query);
    }

    public int scheduleUpdate(XxlJobInfo xxlJobInfo) {
        Update update = new Update();
        update.set("triggerLastTime", xxlJobInfo.getTriggerLastTime());
        update.set("triggerNextTime", xxlJobInfo.getTriggerNextTime());
        update.set("triggerStatus", xxlJobInfo.getTriggerStatus());

        Query query = new Query(where("_id").is(xxlJobInfo.getId()));
        UpdateResult update1 = super.update(query, update);
        return (int) update1.getModifiedCount();
    }


}
