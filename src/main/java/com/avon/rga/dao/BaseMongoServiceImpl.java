package com.avon.rga.dao;

import com.avon.rga.core.util.ReflectionUtils;
import com.avon.rga.service.IdGenerator;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Winston.xu
 * @date 2020/3/24
 */
public class BaseMongoServiceImpl<T> implements BaseMongoService<T> {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * save
     *
     * @param entity
     * @return
     */
    @Override
    public int save(T entity) {
        try {
            Long next = idGenerator.getNext(getEntityClass());
            Field idField = getEntityClass().getDeclaredField("id");
            idField.setAccessible(true);
            if (idField.getType().equals(Long.class)) {
                idField.setLong(entity, next);
            } else {
                idField.setInt(entity, Integer.parseInt(next.toString()));
            }
            mongoTemplate.insert(entity);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Update according to conditions
     *
     * @param query
     * @param update
     * @return
     */
    @Override
    public UpdateResult update(Query query, Update update) {
        if (update == null) {
            return null;
        }
        update.set("updateDate", LocalDateTime.now());
        return mongoTemplate.updateMulti(query, update, this.getEntityClass());
    }

    /**
     * remove by condition
     *
     * @param query
     */
    @Override
    public DeleteResult remove(Query query) {
        return mongoTemplate.remove(query, this.getEntityClass());
    }

    /**
     * @return
     */
    @Override
    public List<T> findAll() {
        return mongoTemplate.findAll(this.getEntityClass());
    }

    /**
     * Query by condition
     *
     * @param query
     * @return
     */
    @Override
    public List<T> find(Query query) {
        return mongoTemplate.find(query, this.getEntityClass());
    }

    /**
     * Query by condition, return only first record
     *
     * @param query
     * @return
     */
    @Override
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, this.getEntityClass());
    }

    /**
     * Get the total num based on conditions
     *
     * @param query
     * @return
     */
    @Override
    public long count(Query query) {
        return mongoTemplate.count(query, this.getEntityClass());
    }


    /**
     * Get a generic class
     */
    public Class<T> getEntityClass() {
        return ReflectionUtils.getSuperClassGenricType(getClass());
    }
}
