package com.iseeyou.kang.uploadimgdemo.bean;

import java.io.Serializable;

/**
 * Created by kGod on 2017/3/29.
 * Email 18252032703@163.com
 * Thank you for watching my code
 */

public class UpLoadBean implements Serializable {

    @Override
    public String toString() {
        return "UpLoadBean{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", ok=" + ok +
                ", page=" + page +
                ", res='" + res + '\'' +
                ", rows=" + rows +
                ", total=" + total +
                '}';
    }

    /**
     * code : ok
     * message :
     * ok : true
     * page : null
     * res : /passenger/96828660.jpg
     * rows : null
     * total : null
     */

    private String code;
    private String message;
    private boolean ok;
    private Object page;
    private String res;
    private Object rows;
    private Object total;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Object getPage() {
        return page;
    }

    public void setPage(Object page) {
        this.page = page;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public Object getRows() {
        return rows;
    }

    public void setRows(Object rows) {
        this.rows = rows;
    }

    public Object getTotal() {
        return total;
    }

    public void setTotal(Object total) {
        this.total = total;
    }
}
