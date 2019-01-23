package com.geetion.microscope_android.utils;

/**
 * Created by ltl on 2017/11/2.
 */

public class FileUtil {
    public static String  replaceFileName(String filename){
        String substring = filename.substring(7);

        if(substring.indexOf(".bmp")!=-1){
            return substring.replace(".bmp","");
        }else if(substring.indexOf(".mp4")!=-1){
            return substring.replace(".mp4","");
        }
        return  substring;
    }
}
