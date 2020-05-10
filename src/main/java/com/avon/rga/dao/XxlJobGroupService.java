package com.avon.rga.dao;

import com.avon.rga.core.model.XxlJobGroup;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by Winston.xu on 2020/03/24
 */
@Service
public class XxlJobGroupService extends BaseMongoServiceImpl<XxlJobGroup> {

    public int update(XxlJobGroup xxlJobGroup){
        Query query = new Query(where("id").is(xxlJobGroup.getId()));
        Update update = new Update();
        update.set("appName", xxlJobGroup.getAppName());
        update.set("title", xxlJobGroup.getTitle());
        update.set("order", xxlJobGroup.getOrder());
        update.set("addressType", xxlJobGroup.getAddressType());
        update.set("addressList", xxlJobGroup.getAddressList());
        UpdateResult updateResult = super.update(query, update);
        return (int) updateResult.getModifiedCount();
    }

    public int remove(int id){
        Query query = new Query(where("id").is(id));
        DeleteResult deleteResult = super.remove(query);
        return (int)deleteResult.getDeletedCount();
    }

    public List<XxlJobGroup> findByAddressType(int addressType){
        Query query = new Query(where("addressType").is(addressType));
        query.with(new Sort(Sort.Direction.ASC, "order"));
        return super.find(query);
    }

    public XxlJobGroup load(int id){
        Query query = new Query(where("id").is(id));
        return super.findOne(query);
    }
}
