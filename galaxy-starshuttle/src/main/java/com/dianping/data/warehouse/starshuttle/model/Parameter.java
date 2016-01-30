package com.dianping.data.warehouse.starshuttle.model;

/**
 * Created by hongdi.tang on 14-3-4.
 */
public class Parameter {
    public Parameter() {
    }

    public Parameter(Integer id, Long time, String offset) {
        this.id = id;
        this.time = time;
        this.offset = offset;
    }


    private Integer id;
    private Long time;
    private String offset;
    private String col;
    private String src;
    private String target;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public String getOffset() {
        return offset;

    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
