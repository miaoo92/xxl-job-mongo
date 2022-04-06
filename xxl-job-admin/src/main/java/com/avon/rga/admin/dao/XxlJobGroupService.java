package com.avon.rga.admin.dao;

import com.avon.rga.admin.core.model.XxlJobGroup;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by xuxueli on 16/9/30.
 */
@Service
public class XxlJobGroupService extends BaseMongoServiceImpl<XxlJobGroup> {

    public List<XxlJobGroup> findAll() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.ASC, "appname"));
        query.with(Sort.by(Sort.Direction.ASC, "title"));
        query.with(Sort.by(Sort.Direction.ASC, "id"));
        List<XxlJobGroup> xxlJobGroups = super.find(query);
        return xxlJobGroups;

    }

    public int save(XxlJobGroup xxlJobGroup) {
        return super.save(xxlJobGroup);
    }

    public List<XxlJobGroup> pageList(int offset,
                                      int pagesize,
                                      String appname,
                                      String title) {
        Query query = new Query();
        query.addCriteria(where("appname").regex(appname));
        query.addCriteria(where("title").regex(title));
        query.skip(offset);
        query.limit(pagesize);
        query.with(Sort.by(Sort.Direction.ASC, "appname", "title", "id"));
        return super.find(query);

    }

    public long pageListCount(int offset,
                              int pagesize,
                              String appname,
                              String title) {
        Query query = new Query();
        query.addCriteria(where("appname").regex(appname));
        query.addCriteria(where("title").regex(title));
        return Long.valueOf(super.count(query)).intValue();
    }

    public int update(XxlJobGroup xxlJobGroup) {
        Query query = new Query(where("id").is(xxlJobGroup.getId()));
        Update update = new Update();
        update.set("appName", xxlJobGroup.getAppname());
        update.set("title", xxlJobGroup.getTitle());
        update.set("addressType", xxlJobGroup.getAddressType());
        update.set("addressList", xxlJobGroup.getAddressList());
        update.set("updateTime", xxlJobGroup.getUpdateTime());
        UpdateResult updateResult = super.update(query, update);
        return (int) updateResult.getModifiedCount();
    }

    public int remove(int id) {
        Query query = new Query(where("id").is(id));
        DeleteResult deleteResult = super.remove(query);
        return (int) deleteResult.getDeletedCount();
    }

    public List<XxlJobGroup> findByAddressType(int addressType) {
        Query query = new Query(where("addressType").is(addressType));
        query.with(Sort.by(Sort.Direction.ASC, "appname", "title", "id"));
        return super.find(query);
    }

    public XxlJobGroup load(int id) {
        Query query = new Query(where("id").is(id));
        return super.findOne(query);
    }

}
