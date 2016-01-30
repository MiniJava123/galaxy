package com.dianping.data.warehouse.web;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author <a href="mailto:tsensue@gmail.com">dishu.chen</a>
 * 14-1-14.
 */
public class Result<T> {
    /**
     * 0 success
     * 1 failure
     */
    private boolean success;
    private String messages;
    private T result;
    private List<T> results = new ArrayList<T>();

    public Result(boolean success) {
        this.success = success;
    }

    public Result() {
        this.success = Boolean.FALSE;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public List<T> getResults() {
        return results;
    }

    public void addResults(T result) {
        this.results.add(result);
    }

    public void addAllResults(List<T> result) {
        this.results.addAll(result);
    }
}
