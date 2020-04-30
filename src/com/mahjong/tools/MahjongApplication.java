package com.mahjong.tools;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MahjongApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ActiveAndroid.initialize(this);
		initImageLoader();
	}	
	
	@Override  
    public void onTerminate() {  
        super.onTerminate();  
        ActiveAndroid.dispose();  
    } 
	
	private void initImageLoader() {
		// 创建默认的ImageLoaderConfiguration
	    ImageLoaderConfiguration configuration=ImageLoaderConfiguration.createDefault(this);
	    // 初始化ImageLoader
	    ImageLoader.getInstance().init(configuration);

	      //第二种是自定义加载
	        //自定义加载第三方图片样式
//	        DisplayImageOptions options = new DisplayImageOptions.Builder()
//	                .showImageOnLoading(R.drawable.image_nor) // 设置图片下载期间显示的图片
//	                .showImageForEmptyUri(R.drawable.image_nor) // 设置图片Uri为空或是错误的时候显示的图片
//	                .showImageOnFail(R.drawable.image_nor) // 设置图片加载或解码过程中发生错误显示的图片
//	                .resetViewBeforeLoading(true)  // default 设置图片在加载前是否重置、复位
//	                .delayBeforeLoading(200)  // 下载前的延迟时间
//	                .cacheInMemory(true) // default  设置下载的图片是否缓存在内存中
//	                .cacheOnDisk(true) // default  设置下载的图片是否缓存在SD卡中
//	                .considerExifParams(false) // default
//	                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default 设置图片以如何的编码方式显示
//	                .bitmapConfig(Bitmap.Config.RGB_565) // default 设置图片的解码类型
//	                //                .displayer(new SimpleBitmapDisplayer()) // default  还可以设置圆角图片new RoundedBitmapDisplayer(20)
//	                .displayer(new FadeInBitmapDisplayer(200))// 图片加载好后渐入的动画时间
//	                //                .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
//	                .handler(new Handler()) // default
//	                .build();
	//
//	        //设置图片缓存路径
//	        File cacheDir = StorageUtils.getCacheDirectory(this);
//	        //自定义ImageLoaderConfiguration配置
//	        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
//	                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions 内存缓存文件的最大宽、高
//	                //                .diskCacheExtraOptions(480, 800, null)  // 本地缓存的详细信息(缓存的最大宽、高)，最好不要设置这个
//	                .threadPoolSize(4) // default  线程池内加载的数量
//	                //                .threadPriority(Thread.NORM_PRIORITY - 2) // default 设置当前线程的优先级
//	                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
//	                .denyCacheImageMultipleSizesInMemory()
//	                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)) //可以通过自己的内存缓存实现
//	                .memoryCacheSize(2 * 1024 * 1024)  // 内存缓存的最大值
//	                .memoryCacheSizePercentage(13) // default
//	                //可以自定义缓存路径
//	                .diskCache(new UnlimitedDiskCache(cacheDir))
//	                .diskCacheSize(100 * 1024 * 1024) // 100 Mb sd卡(本地)缓存的最大值
//	                //                .diskCacheFileCount(100)  // 可以缓存的文件数量
//	                // default为使用HASHCODE对UIL进行加密命名， 还可以用MD5(new Md5FileNameGenerator())加密
//	                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
//	                //                 .connectTimeout (5 s), readTimeout (30 s)  //超时时间
//	                //第二个参数：连接超时时间，第三个参数：读取超时时间
//	                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000))
//	                .defaultDisplayImageOptions(options)
//	                .writeDebugLogs() // 打印debug log
//	                .build(); //开始构建
	//
//	        //初始化ImageLoader
//	        ImageLoader.getInstance().init(configuration);
	}
	
}
