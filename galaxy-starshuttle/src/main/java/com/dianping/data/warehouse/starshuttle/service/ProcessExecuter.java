package com.dianping.data.warehouse.starshuttle.service;

import java.util.List;

/**
 * Created by hongdi.tang on 14-3-4.
 */
public interface ProcessExecuter {
    Integer execute(List<String> commands);
}
