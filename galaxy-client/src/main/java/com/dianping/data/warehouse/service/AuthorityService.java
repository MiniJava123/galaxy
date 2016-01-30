package com.dianping.data.warehouse.service;

import com.dianping.data.warehouse.domain.AclUserInfoBase;

import java.util.List;

/**
 * Created by Sunny on 14-4-29.
 */
public interface AuthorityService {

    /**
     * 对表tableIds进行赋权，线上权限online，线下权限offline
     */
    public String pushACLInfo(List<String> online, List<String> offline, List<Integer> tableIds);

    /**
     * 对表进行赋权，权限保存在userInfoBases
     */
    public String pushACLInfo(List<AclUserInfoBase> userInfoBases, List<Integer> tableIds);

    /**
     * 获得用户的acl用户组
     * online为true表示线上，false表示线下
     */
    public String[] getACLGroup(boolean online, int loginID);

}
