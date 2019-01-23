package com.geetion.fresco;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

/**
 * Created by mac on 15/5/4.
 */
public class FrescoConfig {
    private static int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();//分配的可用内存
    public static int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 2;//使用的缓存数量
    public static int MAX_SMALL_DISK_VERYLOW_CACHE_SIZE = 5 * ByteConstants.MB;//小图极低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    public static int MAX_SMALL_DISK_LOW_CACHE_SIZE = 10 * ByteConstants.MB;//小图低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    public static int MAX_SMALL_DISK_CACHE_SIZE = 20 * ByteConstants.MB;//小图磁盘缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    public static int MAX_DISK_CACHE_VERYLOW_SIZE = 10 * ByteConstants.MB;//默认图极低磁盘空间缓存的最大值
    public static int MAX_DISK_CACHE_LOW_SIZE = 30 * ByteConstants.MB;//默认图低磁盘空间缓存的最大值
    public static int MAX_DISK_CACHE_SIZE = 50 * ByteConstants.MB;//默认图磁盘缓存的最大值
    private static String IMAGE_PIPELINE_SMALL_CACHE_DIR = "small_cache";//小图所放路径的文件夹名
    private static String IMAGE_PIPELINE_CACHE_DIR = "normal_cache";//默认图所放路径的文件夹名
    private static int version; //图片版本号， 使用app的版本号
    private ImagePipelineConfig sImagePipelineConfig;
    private Context context;
    private MemoryCacheParams bitmapCacheParams;
    private DiskCacheConfig diskSmallCacheConfig;
    private DiskCacheConfig diskCacheConfig;
    private static FrescoConfig frescoConfig;

    public int getMAX_HEAP_SIZE() {
        return MAX_HEAP_SIZE;
    }

    public FrescoConfig setMAX_HEAP_SIZE(int MAX_HEAP_SIZE) {
        FrescoConfig.MAX_HEAP_SIZE = MAX_HEAP_SIZE;
        return this;
    }

    public int getMAX_MEMORY_CACHE_SIZE() {
        return MAX_MEMORY_CACHE_SIZE;
    }

    public FrescoConfig setMAX_MEMORY_CACHE_SIZE(int MAX_MEMORY_CACHE_SIZE) {
        FrescoConfig.MAX_MEMORY_CACHE_SIZE = MAX_MEMORY_CACHE_SIZE;
        return this;
    }

    public int getMAX_SMALL_DISK_VERYLOW_CACHE_SIZE() {
        return MAX_SMALL_DISK_VERYLOW_CACHE_SIZE;
    }

    public FrescoConfig setMAX_SMALL_DISK_VERYLOW_CACHE_SIZE(int MAX_SMALL_DISK_VERYLOW_CACHE_SIZE) {
        FrescoConfig.MAX_SMALL_DISK_VERYLOW_CACHE_SIZE = MAX_SMALL_DISK_VERYLOW_CACHE_SIZE;
        return this;
    }

    public int getMAX_SMALL_DISK_LOW_CACHE_SIZE() {
        return MAX_SMALL_DISK_LOW_CACHE_SIZE;
    }

    public FrescoConfig setMAX_SMALL_DISK_LOW_CACHE_SIZE(int MAX_SMALL_DISK_LOW_CACHE_SIZE) {
        FrescoConfig.MAX_SMALL_DISK_LOW_CACHE_SIZE = MAX_SMALL_DISK_LOW_CACHE_SIZE;
        return this;
    }

    public int getMAX_SMALL_DISK_CACHE_SIZE() {
        return MAX_SMALL_DISK_CACHE_SIZE;
    }

    public FrescoConfig setMAX_SMALL_DISK_CACHE_SIZE(int MAX_SMALL_DISK_CACHE_SIZE) {
        FrescoConfig.MAX_SMALL_DISK_CACHE_SIZE = MAX_SMALL_DISK_CACHE_SIZE;
        return this;
    }

    public int getMAX_DISK_CACHE_VERYLOW_SIZE() {
        return MAX_DISK_CACHE_VERYLOW_SIZE;
    }

    public FrescoConfig setMAX_DISK_CACHE_VERYLOW_SIZE(int MAX_DISK_CACHE_VERYLOW_SIZE) {
        FrescoConfig.MAX_DISK_CACHE_VERYLOW_SIZE = MAX_DISK_CACHE_VERYLOW_SIZE;
        return this;
    }

    public int getMAX_DISK_CACHE_LOW_SIZE() {
        return MAX_DISK_CACHE_LOW_SIZE;
    }

    public FrescoConfig setMAX_DISK_CACHE_LOW_SIZE(int MAX_DISK_CACHE_LOW_SIZE) {
        FrescoConfig.MAX_DISK_CACHE_LOW_SIZE = MAX_DISK_CACHE_LOW_SIZE;
        return this;
    }

    public int getMAX_DISK_CACHE_SIZE() {
        return MAX_DISK_CACHE_SIZE;
    }

    public FrescoConfig setMAX_DISK_CACHE_SIZE(int MAX_DISK_CACHE_SIZE) {
        FrescoConfig.MAX_DISK_CACHE_SIZE = MAX_DISK_CACHE_SIZE;
        return this;
    }

    public String getIMAGE_PIPELINE_SMALL_CACHE_DIR() {
        return IMAGE_PIPELINE_SMALL_CACHE_DIR;
    }

    public FrescoConfig setIMAGE_PIPELINE_SMALL_CACHE_DIR(String IMAGE_PIPELINE_SMALL_CACHE_DIR) {
        FrescoConfig.IMAGE_PIPELINE_SMALL_CACHE_DIR = IMAGE_PIPELINE_SMALL_CACHE_DIR;
        return this;
    }

    public String getIMAGE_PIPELINE_CACHE_DIR() {
        return IMAGE_PIPELINE_CACHE_DIR;
    }

    public FrescoConfig setIMAGE_PIPELINE_CACHE_DIR(String IMAGE_PIPELINE_CACHE_DIR) {
        FrescoConfig.IMAGE_PIPELINE_CACHE_DIR = IMAGE_PIPELINE_CACHE_DIR;
        return this;
    }

    public ImagePipelineConfig getsImagePipelineConfig() {
        return sImagePipelineConfig;
    }

    public FrescoConfig setsImagePipelineConfig(ImagePipelineConfig sImagePipelineConfig) {
        this.sImagePipelineConfig = sImagePipelineConfig;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public FrescoConfig setContext(Context context) {
        this.context = context;
        return this;
    }

    public FrescoConfig getFrescoConfig() {
        return frescoConfig;
    }

    public FrescoConfig setFrescoConfig(FrescoConfig frescoConfig) {
        FrescoConfig.frescoConfig = frescoConfig;
        return this;
    }

    public MemoryCacheParams getBitmapCacheParams() {
        return bitmapCacheParams;
    }

    public FrescoConfig setBitmapCacheParams(MemoryCacheParams bitmapCacheParams) {
        this.bitmapCacheParams = bitmapCacheParams;
        return this;
    }

    public DiskCacheConfig getDiskSmallCacheConfig() {
        return diskSmallCacheConfig;
    }

    public FrescoConfig setDiskSmallCacheConfig(DiskCacheConfig diskSmallCacheConfig) {
        this.diskSmallCacheConfig = diskSmallCacheConfig;
        return this;
    }

    public DiskCacheConfig getDiskCacheConfig() {
        return diskCacheConfig;
    }

    public FrescoConfig setDiskCacheConfig(DiskCacheConfig diskCacheConfig) {
        this.diskCacheConfig = diskCacheConfig;
        return this;
    }

    private FrescoConfig(Context context) {
        this.context = context;

    }

    /**
     * 初始化配置，单例
     */
    public static FrescoConfig getInstance(Context context) {
        if (frescoConfig == null) {
            frescoConfig = new FrescoConfig(context);
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = null;//getPackageName()是你当前类的包名，0代表是获取版本信息
            try {
                pi = pm.getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            version = pi.versionCode;
        }
        return frescoConfig;
    }

    public ImagePipelineConfig init() {
        if (sImagePipelineConfig == null) {
            sImagePipelineConfig = configureCaches();
        }
        return sImagePipelineConfig;
    }

    /**
     * 初始化配置
     */
    private ImagePipelineConfig configureCaches() {
        //内存配置
        bitmapCacheParams = new MemoryCacheParams(
                FrescoConfig.MAX_MEMORY_CACHE_SIZE, // 内存缓存中总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,                     // 内存缓存中图片的最大数量。
                FrescoConfig.MAX_MEMORY_CACHE_SIZE, // 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,                     // 内存缓存中准备清除的总图片的最大数量。
                Integer.MAX_VALUE);                    // 内存缓存中单个图片的最大大小。

        //修改内存图片缓存数量，空间策略（这个方式有点恶心）
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };

        //小图片的磁盘配置
        diskSmallCacheConfig = DiskCacheConfig.newBuilder()
                .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())//缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_SMALL_CACHE_DIR)//文件夹名
//    		  .setCacheErrorLogger(cacheErrorLogger)//日志记录器用于日志错误的缓存。
//    		  .setCacheEventListener(cacheEventListener)//缓存事件侦听器。
//    		  .setDiskTrimmableRegistry(diskTrimmableRegistry)//类将包含一个注册表的缓存减少磁盘空间的环境。
                .setMaxCacheSize(FrescoConfig.MAX_SMALL_DISK_CACHE_SIZE)//默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_SMALL_DISK_LOW_CACHE_SIZE)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_SMALL_DISK_VERYLOW_CACHE_SIZE)//缓存的最大大小,当设备极低磁盘空间
                .setVersion(version)
                .build();

        //默认图片的磁盘配置
        diskCacheConfig = DiskCacheConfig.newBuilder()
                .setBaseDirectoryPath(Environment.getExternalStorageDirectory().getAbsoluteFile())//缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)//文件夹名
//		      .setCacheErrorLogger(cacheErrorLogger)//日志记录器用于日志错误的缓存。
//            .setCacheEventListener(cacheEventListener)//缓存事件侦听器。
//		      .setDiskTrimmableRegistry(diskTrimmableRegistry)//类将包含一个注册表的缓存减少磁盘空间的环境。
                .setMaxCacheSize(FrescoConfig.MAX_DISK_CACHE_SIZE)//默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERYLOW_SIZE)//缓存的最大大小,当设备极低磁盘空间
                .setVersion(version)
                .build();
        //缓存图片配置
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context)
//    		  .setAnimatedImageFactory(AnimatedImageFactory animatedImageFactory)//图片加载动画
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)//内存缓存配置（一级缓存，已解码的图片）
//            .setCacheKeyFactory(cacheKeyFactory)//缓存Key工厂
//        	  .setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier)//内存缓存和未解码的内存缓存的配置（二级缓存）
//        	  .setExecutorSupplier(executorSupplier)//线程池配置
//        	  .setImageCacheStatsTracker(imageCacheStatsTracker)//统计缓存的命中率
//        	  .setImageDecoder(ImageDecoder imageDecoder) //图片解码器配置
//        	  .setIsPrefetchEnabledSupplier(Supplier<Boolean> isPrefetchEnabledSupplier)//图片预览（缩略图，预加载图等）预加载到文件缓存
                .setMainDiskCacheConfig(diskCacheConfig)//磁盘缓存配置（总，三级缓存）
//        	  .setMemoryTrimmableRegistry(memoryTrimmableRegistry) //内存用量的缩减,有时我们可能会想缩小内存用量。比如应用中有其他数据需要占用内存，不得不把图片缓存清除或者减小 或者我们想检查看看手机是否已经内存不够了。
//        	  .setNetworkFetchProducer(networkFetchProducer)//自定的网络层配置：如OkHttp，Volley
//                .setPoolFactory(poolFactory)//线程池工厂配置
//        	  .setProgressiveJpegConfig(progressiveJpegConfig)//渐进式JPEG图
//        	  .setRequestListeners(requestListeners)//图片请求监听
//        	  .setResizeAndRotateEnabledForNetwork(boolean resizeAndRotateEnabledForNetwork)//调整和旋转是否支持网络图片
                .setSmallImageDiskCacheConfig(diskSmallCacheConfig)//磁盘缓存配置（小图片，可选～三级缓存的小图优化缓存）
                ;
        return configBuilder.build();
    }
}
