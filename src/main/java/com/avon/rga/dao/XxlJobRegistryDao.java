package com.avon.rga.dao;

import com.avon.rga.core.model.XxlJobRegistry;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by xuxueli on 16/9/30.
 */
@Service
public class XxlJobRegistryDao extends BaseMongoServiceImpl<XxlJobRegistry> {

    public List<Integer> findDead(int timeout, Date nowTime) {
        return null;
    }

    public int removeDead(List<Integer> ids) {
        Query query = new Query();
        query.addCriteria(where("id").in(ids));
        DeleteResult deleteResult = super.remove(query);
        return (int) deleteResult.getDeletedCount();
    }

    public List<XxlJobRegistry> findAll(int timeout, Date nowTime) {
        return null;
    }

    public int registryUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        Query query = new Query();
        query.addCriteria(where("registryGroup").is(registryGroup));
        query.addCriteria(where("registryKey").is(registryKey));
        query.addCriteria(where("registryValue").is(registryValue));
        Update update = new Update();
        update.set("updateTime", updateTime);
        UpdateResult updateResult = super.update(query, update);
        return (int) updateResult.getModifiedCount();
    }

    public int registrySave(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        XxlJobRegistry xxlJobRegistry = new XxlJobRegistry();
        xxlJobRegistry.setRegistryGroup(registryGroup);
        xxlJobRegistry.setRegistryKey(registryKey);
        xxlJobRegistry.setRegistryValue(registryValue);
        xxlJobRegistry.setUpdateTime(updateTime);
        super.save(xxlJobRegistry);
        return 1;
    }

    public int registryDelete(String registryGroup, String registryKey, String registryValue) {
        Query query = new Query();
        query.addCriteria(where("registryGroup").is(registryGroup));
        query.addCriteria(where("registryKey").is(registryKey));
        query.addCriteria(where("registryValue").is(registryValue));
        DeleteResult deleteResult = super.remove(query);
        return (int) deleteResult.getDeletedCount();

    }

}
