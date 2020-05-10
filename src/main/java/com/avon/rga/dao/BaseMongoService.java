package com.avon.rga.dao;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * @author Winston.xu
 * @date 2020/3/24
 */
public interface BaseMongoService<T> {
    /**
     * save
     * @param entity
     * @return
     */
    public int save(T entity);

    /**
     * Update according to conditions
     * @param query
     * @param update
     * @return
     */
    public UpdateResult update(Query query, Update update);

    /**
     * remove by condition
     * @param query
     */
    public DeleteResult remove(Query query);

    /**
     * Get all records
     */
    public List<T> findAll();

    /**
     * Query by condition
     * @param query
     * @return
     */
    public List<T> find(Query query);

    /**
     * Query by condition, return only first record
     * @param query
     * @return
     */
    public T findOne(Query query);

    /**
     * Get the total num based on conditions
     * @param query
     * @return
     */
    public long count(Query query);

}
