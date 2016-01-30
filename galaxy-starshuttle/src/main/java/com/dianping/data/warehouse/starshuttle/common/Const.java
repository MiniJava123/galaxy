package com.dianping.data.warehouse.starshuttle.common;

/**
 * Created by hongdi.tang on 14-3-18.
 */
public class Const {
    public static final Integer RET_SUCCESS = 0;
    public static final Integer RET_PARA_ERROR = 7;
    public static final Integer RET_INTERNAL_ERROR = 8;

    public static enum PARAMETERS {
        time("calculate end time"),
        id("scheduler task id"),
        offset("time offset");

        private String desc;
        private PARAMETERS(String desc){
            this.desc = desc;
        }

        public String getDesc(){
            return this.desc;
        }

    }

//    public static final String XML_HOME="D:";
//


}
