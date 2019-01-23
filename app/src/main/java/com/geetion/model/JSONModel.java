package com.geetion.model;

import com.alibaba.fastjson.JSONObject;
import com.geetion.coreTwoUtil.GJSONUtil;

import java.util.List;

public class JSONModel {

    public static <T> T parseModel(String jsonStr, Class<T> cl) {
        return GJSONUtil.parseModel(jsonStr, cl);
    }

    public static <T> List<T> parseList(JSONObject object, String jsonStr, Class<T> cl) {
        return GJSONUtil.parseModelList(object, jsonStr, cl);
    }

}
