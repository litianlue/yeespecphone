package com.geetion.microscope_android.utils;

import android.content.Context;
import android.util.Log;

import com.geetion.microscope_android.service.Constant;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * For detection and access
 * '用于检测和访问
 */
public class ExternalSD {

    private static final String TAG = ExternalSD.class.getSimpleName();

    private String[] directories;

    // Potential list of SD dirs
    private static final List<String> SD_DIRS = Collections.unmodifiableList(Arrays.asList(
            "external_SD",
            "Dog in the fog, don't bother" // this one is incorrect OFC
    ));

    private static final String STORAGE_DIR = "/storage";

    public ExternalSD(Context context) throws IllegalStateException {

        // find dir

        File file = new File(STORAGE_DIR);

        directories = file.list();

        //        String[] directories = file.list(new FilenameFilter() {
        //            @Override
        //            public boolean accept(File current, String name) {
        //                boolean isDirectory = new File(current, name).isDirectory();
        //                return isDirectory && SD_DIRS.contains(name);
        //            }
        //        });

        if (directories == null || directories.length == 0) {
            throw new IllegalStateException("Missing external SD card");
        }

        for (int i = 0; i < directories.length; i++) {
            Log.e(TAG, directories[i]);
        }
    }

    public File getDir() {
        for (int i = 0; i < directories.length; i++) {
            if (directories[i].toLowerCase().equals("sdcard0"))
                return new File(STORAGE_DIR, directories[i]);
        }
        return null;
    }

    public File getSDCardDir() {
        for (int i = 0; i < directories.length; i++) {
            if (directories[i].toLowerCase().equals("sdcard1")) {
                return new File(STORAGE_DIR, directories[i]);
            }
        }
        return null;
    }

    public File getSDCardAppDir() {
        for (int i = 0; i < directories.length; i++) {
            try {
                if (directories[i].toLowerCase().equals("sdcard1")) {
                    File file = new File(STORAGE_DIR, directories[i] + "/Android/data/com.geetion.microscope.master/" + Constant.DIR_NAME);
                    if (!file.exists())
                        file.mkdir();
                    return file;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
