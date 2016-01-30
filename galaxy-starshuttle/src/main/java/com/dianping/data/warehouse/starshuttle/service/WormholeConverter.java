package com.dianping.data.warehouse.starshuttle.service;

import com.dianping.data.warehouse.domain.model.WormholeDO;

import java.util.List;

/**
 * Created by hongdi.tang on 14-3-4.
 */
public interface WormholeConverter{
    Object convert(List<WormholeDO> wormholes);
}
