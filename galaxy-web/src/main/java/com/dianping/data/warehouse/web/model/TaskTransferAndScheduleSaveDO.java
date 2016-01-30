package com.dianping.data.warehouse.web.model;

import com.dianping.data.warehouse.domain.model.StarShuttleDO;
import com.dianping.data.warehouse.domain.web.StarShuttleParasDO;

import javax.validation.Valid;

/**
 * Created by mt on 2014/5/6.
 */
public class TaskTransferAndScheduleSaveDO {
    @Valid
    StarShuttleDO starShuttleDO;

    @Valid
    StarShuttleParasDO starShuttleParasDO;

    public StarShuttleDO getStarShuttleDO() {
        return starShuttleDO;
    }
    public void setStarShuttleDO(StarShuttleDO starShuttleDO) {
        this.starShuttleDO = starShuttleDO;
    }
    public StarShuttleParasDO getStarShuttleParasDO() {
        return starShuttleParasDO;
    }
    public void setStarShuttleParasDO(StarShuttleParasDO starShuttleParasDO) {
        this.starShuttleParasDO = starShuttleParasDO;
    }
}
