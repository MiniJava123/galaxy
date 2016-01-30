package com.dianping.data.warehouse.starshuttle.common;

import com.dianping.data.warehouse.domain.model.WormholeDO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hongdi.tang on 14-4-9.
 */
public class MockData {
    public static List<WormholeDO> mockWormholeList(){
        List<WormholeDO> list = new ArrayList<WormholeDO>();

        WormholeDO cfg = new WormholeDO();
        cfg.setTaskId(10506);
        cfg.setConnectProps("mysql_DianPingDW2");
        cfg.setType("reader");
        cfg.setParameterMapStr("{\"needSplit\":\"false\",\"countSql\":null,\"sql\":\"select id,division_id,division_name,ou,gidnumber,now() from dpods_acl_ldap_map where ${CAL_YYYYMMDD}\",\"tableName\":null,\"concurrency\":\"1\",\"columns\":null,\"encoding\":\"utf-8\",\"params\":null,\"where\":null,\"connectProps\":\"DianPingDW2\",\"preCheck\":null}");
        list.add(cfg);
        WormholeDO cfg1 = new WormholeDO();
        cfg1.setTaskId(10506);
        cfg1.setConnectProps("hive_bi");
        cfg1.setType("writer");
        cfg1.setParameterMapStr("{\"hive_table_add_partition_condition\":null,\"nullchar\":null,\"replace_char\":null,\"createLzoIndexFile\":null,\"buffer_size\":\"4096\",\"file_type\":\"TXT_COMP\",\"dir\":\"${hdfs_dir}\",\"encoding\":\"UTF-8\",\"concurrency\":\"5\",\"prefix_filename\":\"bi_dpods_acl_ldap_map\",\"dataTransformClass\":null,\"line_split\":\"\\\\n\",\"hive_table_add_partition_switch\":\"true\",\"dataTransformParams\":null,\"connectProps\":\"DianPingDW2\",\"field_split\":\"\\\\005\",\"codec_class\":\"com.hadoop.compression.lzo.LzopCodec\"}");
        list.add(cfg1);
        return list;
    }
}
