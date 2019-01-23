package com.geetion.xUtil;

import com.geetion.model.JSONModel;

/**
 * Created by Beary on 15/11/19.
 */
public class GBaseHttpResult extends JSONModel {

    public String id;
    public String message;
    public String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
