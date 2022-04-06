package com.avon.rga.admin.dao;

import com.avon.rga.admin.core.model.XxlJobUser;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author xuxueli 2019-05-04 16:44:59
 */
@Service
public class XxlJobUserService extends BaseMongoServiceImpl<XxlJobUser>  {

	private Query pageListQuery(int offset, int pagesize, String username, int role) {
		Query query = new Query();
		if (StringUtils.isNotEmpty(username)) {
			query.addCriteria(where("username").regex(username));
		}
		if (role > -1) {
			query.addCriteria(where("role").is(role));
		}
		query.skip(offset);
		query.limit(pagesize);
		return query;
	}

	public List<XxlJobUser> pageList(int offset, int pagesize, String username, int role) {
		Query query = pageListQuery(offset, pagesize, username, role);
		return super.find(query);
	}

	public int pageListCount(int offset, int pagesize, String username, int role) {
		Query query = pageListQuery(offset, pagesize, username, role);
		return (int) super.count(query);
	}

	public XxlJobUser loadByUserName(String username) {
		Query query = new Query(where("username").is(username));
		return super.findOne(query);
	}

	public int save(XxlJobUser xxlJobUser){
		return super.save(xxlJobUser);
	}

	public int update(XxlJobUser xxlJobUser) {
		Query query = new Query(where("id").is(xxlJobUser.getId()));
		Update update = new Update();
		if (StringUtils.isNotEmpty(xxlJobUser.getPassword())) {
			update.set("password", xxlJobUser.getPassword());
		}
		update.set("role", xxlJobUser.getRole());
		update.set("permission", xxlJobUser.getPermission());
		UpdateResult updateResult = super.update(query, update);
		return (int) updateResult.getModifiedCount();
	}

	public int delete(int id) {
		Query query = new Query(where("id").is(id));
		DeleteResult remove = super.remove(query);
		return (int) remove.getDeletedCount();
	}

}
