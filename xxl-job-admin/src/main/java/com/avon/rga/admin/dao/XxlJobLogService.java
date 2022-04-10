package com.avon.rga.admin.dao;

import com.avon.rga.admin.core.model.XxlJobLog;
import com.avon.rga.admin.service.IdGenerator;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

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
public class XxlJobLogService extends BaseMongoServiceImpl<XxlJobLog> {

    @Autowired
    private IdGenerator idGenerator;

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
        } else if (triggerTimeStart != null) {
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
        query.with(Sort.by(Sort.Direction.DESC, "triggerTime"));
        query.skip(offset);
        query.limit(pagesize);
        return query;
    }

    // exist jobId not use jobGroup, not exist use jobGroup
    public List<XxlJobLog> pageList(int offset,
                                    int pagesize,
                                    int jobGroup,
                                    int jobId,
                                    Date triggerTimeStart,
                                    Date triggerTimeEnd,
                                    int logStatus) {
        Query query = pageListQuery(offset, pagesize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        return super.find(query);
    }

    public int pageListCount(int offset,
                             int pagesize,
                             int jobGroup,
                             int jobId,
                             Date triggerTimeStart,
                             Date triggerTimeEnd,
                             int logStatus) {
        Query query = pageListQuery(offset, pagesize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        return (int) super.count(query);
    }

    public XxlJobLog load(long id) {
        return super.findOne(new Query(where("_id").is(id)));
    }

    public int save(XxlJobLog xxlJobLog) {
        Long next = idGenerator.getNext(XxlJobLog.class);
        xxlJobLog.setId(next);
        return super.save(xxlJobLog);
    }

    public long updateTriggerInfo(XxlJobLog xxlJobLog) {
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
        return updateResult.getModifiedCount();
    }

    public long updateHandleInfo(XxlJobLog xxlJobLog) {
        Update update = new Update();
        update.set("handleTime", xxlJobLog.getHandleTime());
        update.set("handleCode", xxlJobLog.getHandleCode());
        update.set("handleMsg", xxlJobLog.getHandleMsg());
        Query query = new Query(where("_id").is(xxlJobLog.getId()));
        UpdateResult updateResult = super.update(query, update);
        return updateResult.getModifiedCount();
    }

    public long delete(int jobId) {
        DeleteResult deleteResult = super.remove(new Query(where("jobId").is(jobId)));
        return deleteResult.getDeletedCount();
    }

    public Map<String, Object> findLogReport(Date from,
                                             Date to) {
        ConditionalOperators.Cond triggerDayCountRunningCond = ConditionalOperators.Cond
                .when(new Criteria().andOperator(where("triggerCode").in(0, 200), where("handleCode").is(0))).then(1).otherwise(0);
        ConditionalOperators.Cond triggerDayCountSucCond = ConditionalOperators.Cond
                .when(where("handleCode").is(200)).then(1).otherwise(0);
        Criteria period = where("triggerTime").gt(from).lt(to);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(period),
                Aggregation.group("handleCode").count().as("triggerDayCount")
                        .sum(triggerDayCountRunningCond).as("triggerDayCountRunning")
                        .sum(triggerDayCountSucCond).as("triggerDayCountSuc")
        );
        Map xxlJobLog = mongoTemplate.aggregate(aggregation, "xxlJobLog", Map.class).getUniqueMappedResult();
        return xxlJobLog;
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
            query1.with(Sort.by(Sort.Direction.DESC, "triggerTime"));
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
        query.with(Sort.by(Sort.Direction.ASC, "id"));
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

    public List<Long> findFailJobLogIds(int pageSize) {
        Criteria criteria = new Criteria().andOperator(where("handleCode").is(0), where("triggerCode").nin(0, 200));
        Criteria codeCriteria = new Criteria().andOperator(
                new Criteria().orOperator(criteria, where("handleCode").is(200)).not(), where("alarmStatus").is(200));
        Query query = new Query().addCriteria(codeCriteria).limit(pageSize)
                .with(Sort.by(Sort.Direction.ASC, "id"));
        List<XxlJobLog> xxlJobLogs = super.find(query);
        return xxlJobLogs.stream().map(XxlJobLog::getId).collect(Collectors.toList());

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

    public List<Long> findLostJobIds(Date losedTime) {
        return null;
    }

}
