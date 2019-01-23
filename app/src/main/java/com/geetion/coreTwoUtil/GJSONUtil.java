package com.geetion.coreTwoUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

import cn.finalteam.toolsfinal.JsonValidator;

/**
 * Create by Beary
 * 注意：以下所有的JSONObject指的是fastjson包内的JsonObject
 * <p/>
 * fastjson与以往用的gson在解析json时候有不一样的地方
 * 1.获取特定字段函数名字改变
 * 以往获取一个int字段，如 object.optInt(string);
 * 现在获取一个int字段，如 object.getIntValue(string);
 * 使用getXXXValue会判断获取的字段是否为null，如果不需要判断，则直接使用getXXX
 *
 * @version 2.0
 */
public class GJSONUtil {


    private GJSONUtil() {

    }


    /**
     * json文本转换为jsonObject
     *
     * @param str
     * @return
     */
    public static JSONObject json2JsonObject(String str) {
        return JSON.parseObject(str);
    }


    //以下两个方法是常用的获取json中某个字段的对象或者对象列表

    /**
     * 将json文本转换成bean对象
     *
     * @param jsonStr
     * @return
     */
    public static <T> T parseModel(String jsonStr, Class<T> cl) {
        return JSON.parseObject(jsonStr, cl);
    }

    /**
     * 将json文本中的list对象parse出来，并遍历后返出去
     * 也就是常用的获取Json中某个字段下面的list的方法
     *
     * @param jsonStr
     * @return
     */
    public static <T> List<T> parseModelList(JSONObject object, String jsonStr, Class<T> cl) {
        Object jsonArray = object.get(jsonStr);
        return JSON.parseArray(jsonArray.toString(), cl);
    }

    //以下两个方法，是将服务器返回来的json直接变成对象或者对象列表（因为没有额外字段）

    /**
     * 将json转换为对象
     *
     * @param str
     * @param clazz
     * @return
     */
    public static <T> T json2Model(String str, Class<T> clazz) {
        return JSON.parseObject(str, clazz);
    }

    /**
     * 将json转换为对象列表
     *
     * @param jsonStr
     * @param cl
     * @return
     */
    public static <T> List<T> json2ModelList(String jsonStr, Class<T> cl) {
        return JSON.parseArray(jsonStr, cl);
    }


    /**
     * 将bean对象转换成JSONObject
     *
     * @param model
     * @return
     */
    public static JSONObject model2JsonObject(Object model) {
        return (JSONObject) JSON.toJSON(model);
    }

    /**
     * 将bean对象转换为json文本
     *
     * @param model
     * @return
     */
    public static String model2Json(Object model) {
        return JSON.toJSONString(model);
    }


    /**
     * 将Map集合转换为Json格式的字符串
     *
     * @param map 要转换为Json格式的map集合
     * @return Json格式的字符串
     */
    public static String map2Json(Map<Object, Object> map) {
        return JSON.toJSONString(map);
    }


    /**
     * 将json格式转换成map对象
     *
     * @param jsonStr
     * @return
     */
//    public static Map<?, ?> jsonToMap(String jsonStr) {
//        Map<?, ?> objMap = null;
//        if (gson != null) {
//            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<?, ?>>() {
//            }.getType();
//            objMap = gson.fromJson(jsonStr, type);
//        }
//        return objMap;
//    }


    /**
     * pojo对象转换为map集合
     *
     * @param object
     * @return
     */
//    public static Map<String, Object> pojoToMap(Object object) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        Field[] filed = object.getClass().getDeclaredFields();
//        for (int j = 0; j < filed.length; j++) {
//            String filedName = filed[j].getName();
//            String getFiledNameMethod = "get" + filedName.substring(0, 1).toUpperCase() + filedName.substring(1);
//            Method method;
//            try {
//                method = object.getClass().getMethod(getFiledNameMethod);
//                Object value = method.invoke(object);
//                if (value == null) {
//                    value = "";
//                }
//                String className = value.getClass().getName();
//                if (className.equals("java.util.ArrayList")) {
//                    List<Object> list = (List) value;
//                    StringBuffer str = new StringBuffer("");
//                    if (list.size() > 1) {
//                        str.append("[");
//                    }
//                    boolean hasNext = false;
//                    for (int i = 0; i < list.size(); i++) {
//                        if (hasNext) {
//                            str.append(",");
//                        }
//                        str.append(pojoFormat(list.get(i), null));
//                        hasNext = true;
//                    }
//                    if (list.size() > 1) {
//                        str.append("]");
//                    }
//                    value = str;
//                } else if (className.split("\\.").length > 1 && className.split("\\.")[1].equals("lyq")) {
//                    StringBuffer str = new StringBuffer("");
//                    Class c = Class.forName(className);
//                    Object entity = c.newInstance();
//                    entity = value;
//                    str.append(pojoFormat(entity, null));
//                    value = str;
//                }
//                map.put(filedName, value.toString());
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        return map;
//    }
//

    /**
     * 检验JSON合法性（专治PHP）
     * @param json
     * @return
     */
    public static boolean isJson(String json) {
        JsonValidator jsonValidator = new JsonValidator();
        return jsonValidator.validate(json);
    }


}