package com.dianping.data.warehouse.domain.model;

/**
 * Created by Sunny on 14-7-18.
 */
public class ExceptionAlertDO {

    private Integer id;

    private String product;

    private String description;

    private String oncall;

    public ExceptionAlertDO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getOncall() {
        return oncall;
    }

    public void setOncall(String oncall) {
        this.oncall = oncall;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
