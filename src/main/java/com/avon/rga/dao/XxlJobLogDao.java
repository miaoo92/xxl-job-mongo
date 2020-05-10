package com.avon.rga.dao;

import com.avon.rga.core.model.XxlJobLog;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * job log
 *
 * @author xuxueli 2016-1-12 18:03:06
 */
@Service
public class XxlJobLogDao extends BaseMongoServiceImpl<XxlJobLog> {

    // exist jobId not use jobGroup, not exist use jobGroup
    public List<XxlJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId,
                                    Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        Query query = pageListQuery(offset, pagesize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        return super.find(query);

    }

    public int pageListCount(int offset, int pagesize, int jobGroup, int jobId,
                             Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        Query query = pageListQuery(offset, pagesize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        return (int) super.count(query);
    }

    public XxlJobLog load(long id) {
        return super.findOne(new Query(where("_id").is(id)));
    }

    public int updateTriggerInfo(XxlJobLog xxlJobLog) {
        Update update = new Update();
        update.set("triggerTime", xxlJobLog.getTriggerTime());
        update.set("triggerCode", xxlJobLog.getTriggerCode());
        update.set("triggerMsg", xxlJobLog.getTriggerMsg());
        update.set("executorAddress", xxlJobLog.getExecutorAddress());
        update.set("executorHandler", xxlJobLog.getExecutorHandler());
        update.set("executorParam", xxlJobLog.getExecutorParam());
        update.set("executorShardingParam", xxlJobLog.getExecutorShardingParam());
        update.set("executorFailRetryCount", xxlJobLog.getExecutorFailRetryCount());

        Query query = new Query(where("_id").is(xxlJobLog.getId()));
        UpdateResult updateResult = super.update(query, update);
        return (int) updateResult.getModifiedCount();
    }

    public int updateHandleInfo(XxlJobLog xxlJobLog) {
        Update update = new Update();
        update.set("handleTime", xxlJobLog.getHandleTime());
        update.set("handleCode", xxlJobLog.getHandleCode());
        update.set("handleMsg", xxlJobLog.getHandleMsg());
        Query query = new Query(where("_id").is(xxlJobLog.getId()));
        UpdateResult updateResult = super.update(query, update);
        return (int) updateResult.getModifiedCount();
    }

    public int delete(int jobId) {
        DeleteResult deleteResult = super.remove(new Query(where("jobId").is(jobId)));
        return (int) deleteResult.getDeletedCount();
    }

    public Map<String, Object> findLogReport(Date from, Date to) {
        //TODO
        return Collections.emptyMap();
    }

    public List<Long> findClearLogIds(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum, int pagesize) {
        Query query = new Query();
        if (jobGroup > 0) {
            query.addCriteria(where("jobGroup").is(jobGroup));
        }
        if (jobId > 0) {
            query.addCriteria(where("jobId").is(jobId));
        }
        String[] ids = new String[]{};
        if (clearBeforeNum > 0) {
            Query query1 = query;
            query1.with(new Sort(Sort.Direction.DESC, "triggerTime"));
            query1.limit(clearBeforeNum);
            List<XxlJobLog> xxlJobLogs = super.find(query1);
            if (xxlJobLogs != null) {
                ids = xxlJobLogs.stream().map(XxlJobLog::getId).toArray(String[]::new);
            }
        }
        if (clearBeforeTime != null) {
            query.addCriteria(where("triggerTime").gt(clearBeforeTime));
        }
        if (clearBeforeNum > 0) {
            query.addCriteria(where("id").nin((Object[]) ids));
        }
        query.with(new Sort(Sort.Direction.ASC, "id"));
        query.limit(pagesize);
        List<XxlJobLog> xxlJobLogs = super.find(query);
        return xxlJobLogs.stream().map(XxlJobLog::getId).collect(Collectors.toList());
    }

    public int clearLog(List<Long> logIds) {
        Query query = new Query();
        query.addCriteria(where("id").in((Object) logIds.toArray(new Long[0])));
        DeleteResult deleteResult = super.remove(query);
        return (int) deleteResult.getDeletedCount();
    }

    public List<Long> findFailJobLogIds(int pagesize) {
        //TODO
        Criteria criteria = new Criteria().andOperator(where("handleCode").is(0), where("triggerCode").nin(0, 200));
        return Collections.emptyList();

    }

    public int updateAlarmStatus(long logId,
                                 int oldAlarmStatus,
                                 int newAlarmStatus) {
        Query query = new Query();
        query.addCriteria(where("alarmStatus").is(oldAlarmStatus));
        query.addCriteria(where("id").is(logId));

        Update update = new Update();
        update.set("alarmStatus", newAlarmStatus);
        UpdateResult updateResult = super.update(query, update);
        return (int) updateResult.getModifiedCount();
    }

    private Query pageListQuery(int offset, int pagesize, int jobGroup, int jobId,
                                Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        Query query = new Query();
        if (jobId == 0 && jobGroup > 0) {
            query.addCriteria(where("jobGroup").is(jobGroup));
        }
        if (jobId > 0) {
            query.addCriteria(where("jobId").is(jobId));
        }
        if (triggerTimeStart != null && triggerTimeEnd != null) {
            query.addCriteria(where("triggerTime").gte(triggerTimeStart).lte(triggerTimeEnd));
        } else if (triggerTimeEnd != null) {
            query.addCriteria(where("triggerTime").lte(triggerTimeEnd));
        } else if (triggerTimeStart != null){
            query.addCriteria(where("triggerTime").gte(triggerTimeStart));
        }
        if (logStatus == 1) {
            query.addCriteria(where("handleCode").is(200));
        }
        if (logStatus == 2) {
            query.addCriteria(new Criteria().orOperator(where("triggerCode").nin(0, 200), where("handleCode").nin(0, 200)));
        }
        if (logStatus == 3) {
            query.addCriteria(new Criteria().andOperator(where("triggerCode").is(200), where("handleCode").is(0)));
        }
        query.with(new Sort(Sort.Direction.DESC, "triggerTime"));
        query.skip(offset);
        query.limit(pagesize);
        return query;
    }

}
