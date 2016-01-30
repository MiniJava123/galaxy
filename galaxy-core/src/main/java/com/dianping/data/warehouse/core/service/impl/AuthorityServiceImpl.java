package com.dianping.data.warehouse.core.service.impl;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.core.common.GlobalResources;
import com.dianping.data.warehouse.domain.AclUserInfoBase;
import com.dianping.data.warehouse.service.AuthorityService;
import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunny on 14-4-29.
 */

@Service("authorityServiceImpl")
public class AuthorityServiceImpl implements AuthorityService {
    private Logger logger = LoggerFactory.getLogger(AuthorityServiceImpl.class);

    /**
     * 对表进行赋权，线上权限online，线下权限offline
     */
    @Override
    public String pushACLInfo(List<String> online, List<String> offline, List<Integer> tableIds) {
        String url = getEmpowerUrl(online, offline, tableIds);
        return empowerTable(url, Const.EMPOWER_RETRY_NUMBER);
    }

    /**
     * 对表进行赋权，权限保存在userInfoBases
     */
    public String pushACLInfo(List<AclUserInfoBase> userInfoBases, List<Integer> tableIds) {
        String url = getEmpowerUrl(userInfoBases, tableIds);
        return empowerTable(url, Const.EMPOWER_RETRY_NUMBER);
    }

    /**
     * 获得用户的acl用户组
     * isOnline为true表示线上，false表示线下
     */
    @Override
    public String[] getACLGroup(boolean isOnline, int loginId) {
        String url = getACLGroupUrl(isOnline, loginId);
        return getACLGroupByUrl(url);


    }

    /**
     * 获得给表赋权的url
     */
    private String getEmpowerUrl(List<String> online, List<String> offline, List<Integer> tableIds) {
        JSONObject pushInfo = getPushInfo(online, offline, tableIds);
        String aclAddress = null;
        ConfigCache configCache = null;
        try {
            configCache = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
            aclAddress = configCache.getProperty("galaxy.acl_rul");
        } catch (LionException e) {
            logger.error("lion get aclAddress fails");
        }

        String url = aclAddress + "/pushPrivsForDatatool?access_token=" + GlobalResources.TOKEN + "&push_info=" + pushInfo.toString();
        logger.info("push acl info :" + url);
        return url;
    }

    /**
     * 获得给表赋权的url
     */
    private String getEmpowerUrl(List<AclUserInfoBase> aclUserInfoBases, List<Integer> tableIds) {
        JSONObject pushInfo = getPushInfo(aclUserInfoBases, tableIds);
        String aclAddress = null;
        ConfigCache configCache = null;
        try {
            configCache = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
            aclAddress = configCache.getProperty("galaxy.acl_rul");
        } catch (LionException e) {
            logger.error("lion get aclAddress fails");
        }

        String url = aclAddress + "/pushPrivsForDatatool?access_token=" + GlobalResources.TOKEN + "&push_info=" + pushInfo.toString();
        logger.info("push acl info :" + url);
        return url;
    }

    /**
     * 获得合成表赋权url需要的pushInfo
     */
    private JSONObject getPushInfo(List<String> online, List<String> offline, List<Integer> tableIds) {
        JSONObject pushInfo = new JSONObject();
        pushInfo.put("code", 200);

        JSONArray table = getTableJson(tableIds);
        JSONArray msg = new JSONArray();

        for (String group : online) {
            if (group != null) {
                msg.add(getOnlineGroupJson(group, table));
            }
        }
        for (String group : offline) {
            if (group != null) {
                msg.add(getOfflineGroupJson(group, table));
            }
        }
        pushInfo.put("msg", msg);
        return pushInfo;
    }

    /**
     * 获得合成表赋权url需要的pushInfo
     */
    private JSONObject getPushInfo(List<AclUserInfoBase> aclUserInfoBases, List<Integer> tableIds) {
        JSONObject pushInfo = new JSONObject();
        pushInfo.put("code", 200);

        JSONArray table = getTableJson(tableIds);
        JSONArray msg = new JSONArray();

        for (AclUserInfoBase aclUserInfoBase : aclUserInfoBases) {
            if (aclUserInfoBase != null)
                msg.add(getGroupJson(aclUserInfoBase, table));
        }
        pushInfo.put("msg", msg);
        return pushInfo;
    }


    private JSONArray getTableJson(List<Integer> tableIds) {
        JSONArray table = new JSONArray();
        for (int tableID : tableIds) {
            JSONObject t = new JSONObject();
            t.put("table_id", tableID);
            t.put("priv", 1);
            table.add(t);
        }
        return table;
    }

    private JSONObject getOnlineGroupJson(String group, JSONArray table) {
        JSONObject o = new JSONObject();
        o.put("type", 6);
        o.put("user_type", 0);
        o.put("usage_type", 0);
        o.put("user_name", group);
        o.put("table", table);
        return o;
    }

    private JSONObject getOfflineGroupJson(String group, JSONArray table) {
        JSONObject o = new JSONObject();
        o.put("type", 6);
        o.put("user_type", 0);
        o.put("usage_type", 1);
        o.put("user_name", group);
        o.put("table", table);
        return o;
    }

    private JSONObject getGroupJson(AclUserInfoBase aclUserInfoBase, JSONArray table) {
        JSONObject o = new JSONObject();
        o.put("type", 6);
        o.put("user_type", aclUserInfoBase.getUser_type());
        o.put("usage_type", aclUserInfoBase.getUsage_type());
        o.put("user_name", aclUserInfoBase.getUser_name());
        o.put("table", table);
        return o;
    }

    /**
     * 通过url进行赋权，最多充实retryNumber次
     */
    private String empowerTable(String url, int retryNumber) {
        String context = null;
        //授权操作默认重试三次
        for (int i = 0; i < retryNumber; i++) {
            try {
                context = Jsoup.connect(url).timeout(3000).execute().body();
            } catch (IOException e) {
                logger.info("push acl 网络错误。重试次数:" + i + "error:" + e);
            }
            JSONObject object = JSONObject.fromObject(context);
            if (context != null && context != "" && object.has("code"))
                return context;
        }
        throw new RuntimeException("授权请求发送失败:网络错误");
    }

    /**
     * 返回获取组账号信息的url
     */
    private String getACLGroupUrl(boolean isOnline, int loginId) {
        int type = isOnline ? 0 : 1;
        String aclAddress = null;
        ConfigCache configCache = null;
        try {
            configCache = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
            aclAddress = configCache.getProperty("galaxy.acl_rul");
        } catch (LionException e) {
            logger.error("lion get aclAddress fails");
        }

        String url = aclAddress + "/getUserInfoForEmployee?login_id=" + loginId + "&usage_type=" + type;
        return url;
    }

    /**
     * 根据url获取组账号
     */
    private String[] getACLGroupByUrl(String url) {
        String[] groups = new String[10];
        String context = null;
        try {
            context = Jsoup.connect(url).timeout(60000).execute().body();
        } catch (IOException e) {
            throw new RuntimeException("网络错误导致获取组帐号失败");
        }
        //包装线上组帐号list
        if (context != null) {
            JSONObject jsonObj = JSONObject.fromObject(context);
            JSONArray onlineList = jsonObj.getJSONObject("msg").getJSONArray("infos");
            groups = new String[onlineList.size()];
            for (int i = 0; i < onlineList.size(); i++) {
                groups[i] = onlineList.getJSONObject(i).get("user_name").toString();
            }
        }
        return groups;
    }


    private AclUserInfoBase getAclUserInfoBase(JSONObject object) {
        AclUserInfoBase aclUserInfoBase = new AclUserInfoBase();
        aclUserInfoBase.setUsage_type(Integer.parseInt(object.get("usage_type").toString()));
        aclUserInfoBase.setUser_type(Integer.parseInt(object.get("user_type").toString()));
        aclUserInfoBase.setUser_name(object.get("user_name").toString());
        return aclUserInfoBase;
    }
}
