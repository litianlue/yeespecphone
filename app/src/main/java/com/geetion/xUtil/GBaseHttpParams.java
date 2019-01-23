package com.geetion.xUtil;

import com.geetion.coreOneUtil.DefaultURL;
import com.geetion.coreTwoUtil.GStringUtil;

import org.xutils.http.RequestParams;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Beary on 15/11/19.
 */
public class GBaseHttpParams extends RequestParams {

    private class DefaultParams {
        /**
         * 默认重试，3次
         * 默认超时，15秒
         */
        private static final int DEFAULT_MAX_RETRYCOUNT = 3;
        private static final int DEFAULT_CONNECTIMEOUT = 1000 * 15;

        /**
         * 默认可以立即取消http访问（因为开了新线程访问，调用cancel则立即取消这个线程）
         * 默认可以断线续传(文件上下传)
         * 默认不根据头信息（http头）自动命名文件（因为一般上传图片都会用abc.temp）
         * 默认不以JSON格式上传信息，以键值对
         * 默认不强制使用multipart表单
         */
        private static final boolean DEFAULT_CANCELFAST = true;
        private static final boolean DEFAULT_AUTO_RESUME = true;
        private static final boolean DEFAULT_AUTO_RENAME = false;
        private static final boolean DEFAULT_UPLOAD_AS_JSON = false;
        private static final boolean DEFAULT_MULTIPART = false;

    }
//    /**
//     * 默认重试，3次
//     * 默认超时，15秒
//     */
//    private static final int DEFAULT_MAX_RETRYCOUNT = 3;
//    private static final int DEFAULT_CONNECTIMEOUT = 1000 * 15;
//
//    /**
//     * 默认可以立即取消http访问（因为开了新线程访问，调用cancel则立即取消这个线程）
//     * 默认可以断线续传(文件上下传)
//     * 默认不根据头信息（http头）自动命名文件（因为一般上传图片都会用abc.temp）
//     * 默认不以JSON格式上传信息，以键值对
//     * 默认不强制使用multipart表单
//     */
//    private static final boolean DEFAULT_CANCELFAST = true;
//    private static final boolean DEFAULT_AUTO_RESUME = true;
//    private static final boolean DEFAULT_AUTO_RENAME = false;
//    private static final boolean DEFAULT_UPLOAD_AS_JSON = false;
//    private static final boolean DEFAULT_MULTIPART = false;

    /**
     * 默认构造函数，url不能为空，其他为默认值
     *
     * @param url
     */
    public GBaseHttpParams(String url) {
        this(url, DefaultURL.DEFAULT_CACHEDIRNAME, DefaultURL.DEFAULT_SAVEFILEPATH, null,
                DefaultParams.DEFAULT_MAX_RETRYCOUNT, DefaultParams.DEFAULT_CONNECTIMEOUT,
                DefaultParams.DEFAULT_CANCELFAST, DefaultParams.DEFAULT_AUTO_RESUME,
                DefaultParams.DEFAULT_AUTO_RENAME, DefaultParams.DEFAULT_UPLOAD_AS_JSON, DefaultParams.DEFAULT_MULTIPART);
    }

    /**
     * @param url
     * @param cacheDirName 缓存文件夹名字
     */
    public GBaseHttpParams(String url, String cacheDirName) {
        this(url, cacheDirName, DefaultURL.DEFAULT_SAVEFILEPATH, null,
                DefaultParams.DEFAULT_MAX_RETRYCOUNT, DefaultParams.DEFAULT_CONNECTIMEOUT,
                DefaultParams.DEFAULT_CANCELFAST, DefaultParams.DEFAULT_AUTO_RESUME,
                DefaultParams.DEFAULT_AUTO_RENAME, DefaultParams.DEFAULT_UPLOAD_AS_JSON, DefaultParams.DEFAULT_MULTIPART);
    }

    /**
     * 用于下载文件
     *
     * @param url
     * @param saveFilePath 下载文件保存路径（绝对路径）
     * @param isAutoResume 是否支持断点续传
     */
    public GBaseHttpParams(String url, String saveFilePath, String fileName, boolean isAutoResume) {
        this(url, DefaultURL.DEFAULT_CACHEDIRNAME, saveFilePath, fileName,
                DefaultParams.DEFAULT_MAX_RETRYCOUNT, DefaultParams.DEFAULT_CONNECTIMEOUT,
                DefaultParams.DEFAULT_CANCELFAST, isAutoResume,
                DefaultParams.DEFAULT_AUTO_RENAME, DefaultParams.DEFAULT_UPLOAD_AS_JSON, DefaultParams.DEFAULT_MULTIPART);
    }

    /**
     * @param url
     * @param saveFilePath 下载文件保存路径（绝对路径）
     * @param isAutoResume 是否支持断点续传
     * @param isAutoReName 是否自定命名（根据http头）
     */
    public GBaseHttpParams(String url, String saveFilePath, String fileName, boolean isAutoResume, boolean isAutoReName) {
        this(url, DefaultURL.DEFAULT_CACHEDIRNAME, saveFilePath, fileName,
                DefaultParams.DEFAULT_MAX_RETRYCOUNT, DefaultParams.DEFAULT_CONNECTIMEOUT,
                DefaultParams.DEFAULT_CANCELFAST, isAutoResume,
                isAutoReName, DefaultParams.DEFAULT_UPLOAD_AS_JSON, DefaultParams.DEFAULT_MULTIPART);
    }


    /**
     * @param url
     * @param max_retry      最大重试次数
     * @param connectTimeOut 超时时间
     */
    public GBaseHttpParams(String url, int max_retry, int connectTimeOut) {
        this(url, DefaultURL.DEFAULT_CACHEDIRNAME, DefaultURL.DEFAULT_SAVEFILEPATH, null,
                max_retry, connectTimeOut,
                DefaultParams.DEFAULT_CANCELFAST, DefaultParams.DEFAULT_AUTO_RESUME,
                DefaultParams.DEFAULT_AUTO_RENAME, DefaultParams.DEFAULT_UPLOAD_AS_JSON, DefaultParams.DEFAULT_MULTIPART);
    }

    /**
     * @param url
     * @param connectTimeOut 超时时间
     */
    public GBaseHttpParams(String url, int connectTimeOut) {
        this(url, DefaultURL.DEFAULT_CACHEDIRNAME, DefaultURL.DEFAULT_SAVEFILEPATH, null,
                DefaultParams.DEFAULT_MAX_RETRYCOUNT, connectTimeOut,
                DefaultParams.DEFAULT_CANCELFAST, DefaultParams.DEFAULT_AUTO_RESUME,
                DefaultParams.DEFAULT_AUTO_RENAME, DefaultParams.DEFAULT_UPLOAD_AS_JSON, DefaultParams.DEFAULT_MULTIPART);
    }

    /**
     * 全参数构造函数
     *
     * @param url
     * @param cacheDirName
     * @param saveFilePath
     * @param maxRetryCount
     * @param connectTimeOut
     * @param isCancelFast
     * @param isAutoResume
     * @param isAutoRename
     * @param isUploadasJson
     * @param isForceMultipart
     */
    public GBaseHttpParams(String url, String cacheDirName, String saveFilePath, String fileName,
                           int maxRetryCount, int connectTimeOut,
                           boolean isCancelFast, boolean isAutoResume,
                           boolean isAutoRename, boolean isUploadasJson,
                           boolean isForceMultipart) {
        super(url);
        OptionSetting(cacheDirName, saveFilePath, fileName, maxRetryCount, connectTimeOut, isCancelFast, isAutoResume, isAutoRename, isUploadasJson, isForceMultipart);
    }

    /**
     * 自定义文件下载地址与文件名
     *
     * @param path
     * @param fileName
     */
    public void setSaveFilePathAndName(String path, String fileName) {
        setSaveFilePath(path + fileName);
    }

    public void setSaveFileName(String name) {
        setSaveFilePath(DefaultURL.DEFAULT_SAVEFILEPATH + name);
    }


    /**
     * @param cacheDirName     缓存文件夹名称
     * @param saveFilePath     下载文件时保存文件的路径
     * @param maxRetryCount    访问最大重试次数
     * @param connectTimeOut   超时时间
     * @param isCancelFast     能否被立即取消访问
     * @param isAutoResume     上下传文件能否断点续传
     * @param isAutoRename     是否根据http头信息自动命名文件
     * @param isUploadasJson   是否以JSON格式上传信息
     * @param isForceMultipart 是否强制使用multipart表单
     */
    private void OptionSetting(String cacheDirName, String saveFilePath, String fileName,
                               int maxRetryCount, int connectTimeOut,
                               boolean isCancelFast, boolean isAutoResume,
                               boolean isAutoRename, boolean isUploadasJson,
                               boolean isForceMultipart) {
        if (!GStringUtil.isBlank(cacheDirName)) {
            setCacheDirName(DefaultURL.DEFAULT_CACHEDIRNAME);
        }
        if (!GStringUtil.isBlank(fileName)) {
            if (!GStringUtil.isBlank(saveFilePath)) {
                setSaveFilePath(DefaultURL.DEFAULT_SAVEFILEPATH + fileName);
            }
        } else {
            if (!GStringUtil.isBlank(saveFilePath)) {
                setSaveFilePath(DefaultURL.DEFAULT_SAVEFILEPATH + DefaultURL.DEFAULT_FILENAME);
            }
        }

        if (maxRetryCount == 0) {
            setMaxRetryCount(DefaultParams.DEFAULT_MAX_RETRYCOUNT);
        }
        if (connectTimeOut == 0) {
            setConnectTimeout(DefaultParams.DEFAULT_CONNECTIMEOUT);
        }
        setCancelFast(isCancelFast);
        setAutoResume(isAutoResume);
        setAutoRename(isAutoRename);
        setAsJsonContent(isUploadasJson);
        setMultipart(isForceMultipart);
    }


    /**
     * 自动封装get访问的参数
     *
     * @param params 包含上传参数的Map
     */
    public void putGETParams(Map<String, String> params) {
        Set keys = params.keySet();
        if (keys != null) {
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = params.get(key);
//                 Logger.e("key:  " + key.toString() + "   value:  " + value.toString());
                addQueryStringParameter(key.toString(), value.toString());
            }
        }
    }


    /**
     * 自动封装Post访问参数
     *
     * @param params
     */
    public void putPOSTParams(Map<String, String> params) {
        Set keys = params.keySet();
        if (keys != null) {
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = params.get(key);
                addBodyParameter(key.toString(), value.toString());
            }
        }
    }

    public void putPOSTObjectParams(Map<String, Object> params) {
        Set keys = params.keySet();
        if (keys != null) {
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Object value = params.get(key);
                addParameter(key.toString(), value);
            }
        }
    }

    /**
     * 自动封装Header访问参数
     *
     * @param params
     */
    public void putHeaderParams(Map<String, String> params) {
        Set keys = params.keySet();
        if (keys != null) {
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = params.get(key);
                // Logger.e("key:  " + key.toString() + "   value:  " + value.toString());
                addHeader(key.toString(), value.toString());
            }
        }
    }


    /**
     * 传一连串文件（参数带的是文件）
     *
     * @param params
     */
    public void putFileParams(Map<String, File> params) {
        putFileParams(params, null, null, null, null, null);
    }

    /**
     * 传一连串文件（参数带的是文件路径）
     *
     * @param params
     */
    public void putFileParamsWithPath(Map<String, String> params) {
        putFileParams(null, params, null, null, null, null);
    }

    /**
     * 上传单一的文件
     *
     * @param filekey 上传参数名
     * @param file    上传文件
     */
    public void putFileParams(String filekey, File file) {
        putFileParams(null, null, filekey, file, null, null);
    }

    /**
     * 上传单一的文件
     *
     * @param filekey  上传参数名
     * @param filePath 上传文件的路径
     */
    public void putFileParams(String filekey, String filePath) {
        putFileParams(null, null, filekey, null, null, filePath);
    }

    /**
     * @param params          map资源，存上传参数名和文件
     * @param paramsWithPath  map资源，存上传参数名和文件地址
     * @param filekey         传单个文件时候用到的参数名
     * @param filename        传单个文件时候用到的文件
     * @param filePath        传单个文件时候用到的文件地址
     * @param fileContentType 如果文件没有拓展名，最好在这个参数里面标明contentType参数
     *                        常用的参数有
     *                        image/jpeg
     *                        image/png
     *                        audio/mp3
     */
    public void putFileParams(Map<String, File> params, Map<String, String> paramsWithPath, String filekey, File filename, String filePath, String fileContentType) {
        //强制使用multipart表单上传文件
        if (!isMultipart()) {
            setMultipart(true);
        }
        //传的file
        if (params != null) {
            Set keys = params.keySet();
            if (keys != null) {
                Iterator iterator = keys.iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    File value = params.get(key);
                    if (!GStringUtil.isBlank(fileContentType)) {
                        addBodyParameter(key.toString(), value.toString(), fileContentType);
                    } else {
                        addBodyParameter(key.toString(), value.toString(), null);
                    }
                }
            }
        }
        //传filepath
        if (paramsWithPath != null) {
            Set keys = paramsWithPath.keySet();
            if (keys != null) {
                Iterator iterator = keys.iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    String value = paramsWithPath.get(key);
                    if (!GStringUtil.isBlank(fileContentType)) {
                        addBodyParameter(key.toString(), new File(value.toString()), fileContentType);
                    } else {
                        addBodyParameter(key.toString(), new File(value.toString()), null);
                    }

                }
            }
        }

        //上传单个文件(传地址)
        if (!GStringUtil.isBlank(filekey) && !GStringUtil.isBlank(filePath)) {
            if (!GStringUtil.isBlank(fileContentType)) {
                addBodyParameter(filekey, new File(filePath), fileContentType);
            } else {
                addBodyParameter(filekey, new File(filePath), null);
            }

        }
        //上传单个文件(传文件)
        if (!GStringUtil.isBlank(filekey) && filename != null && filename.isFile()) {
            if (!GStringUtil.isBlank(fileContentType)) {
                addBodyParameter(filekey, filename, fileContentType);
            } else {
                addBodyParameter(filekey, filename, null);
            }

        }
    }

}
