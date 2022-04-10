package com.avon.rga.admin.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xuxueli 2019-05-04 16:43:12
 */
@Data
@Document(collection = "user")
public class XxlJobUser {

	@Id
	private String id;

	private String password;

	private String userId;

	private String acctTyp;

	private String emailAddrTxt;

	private int role;				// 角色：0-普通用户、1-管理员

	private String permission;	    // 权限：执行器ID列表，多个逗号分割

	// plugin
	public boolean validPermission(int jobGroup){
		if (this.role == 1) {
			return true;
		} else {
			if (StringUtils.hasText(this.permission)) {
				for (String permissionItem : this.permission.split(",")) {
					if (String.valueOf(jobGroup).equals(permissionItem)) {
						return true;
					}
				}
			}
			return false;
		}

	}

}
