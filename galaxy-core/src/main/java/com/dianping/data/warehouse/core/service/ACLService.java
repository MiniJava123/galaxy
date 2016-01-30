package com.dianping.data.warehouse.core.service;

import com.dianping.data.warehouse.core.model.UserDO;

/**
 * @Author <a href="mailto:tsensue@gmail.com">dishu.chen</a>
 * 14-3-12.
 */
public interface ACLService {
    public UserDO getUserByToken(String token);
}
