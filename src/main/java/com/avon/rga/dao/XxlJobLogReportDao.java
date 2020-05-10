package com.avon.rga.dao;

import com.avon.rga.core.model.XxlJobLogReport;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * job log
 *
 * @author xuxueli 2019-11-22
 */
@Service
public class XxlJobLogReportDao extends BaseMongoServiceImpl<XxlJobLogReport> {

    public int update(XxlJobLogReport xxlJobLogReport){
        Query query = new Query(where("triggerDay").is(xxlJobLogReport.getTriggerDay()));
        Update update = new Update();
        update.set("runningCount", xxlJobLogReport.getRunningCount());
        update.set("sucCount", xxlJobLogReport.getSucCount());
        update.set("failCount", xxlJobLogReport.getFailCount());
        UpdateResult updateResult = super.update(query, update);
        return (int)updateResult.getModifiedCount();
    }

    public List<XxlJobLogReport> queryLogReport(Date triggerDayFrom, Date triggerDayTo){
        Query query = new Query();
        query.addCriteria(where("triggerDay").gt(triggerDayFrom).lt(triggerDayTo));
        query.with(new Sort(Sort.Direction.ASC, "triggerDay"));
        return super.find(query);
    }

    public XxlJobLogReport queryLogReportTotal(){
        //TODO
        return null;

    }

}
