package com.example.gysimageloader.process.custom

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.shuyu.gsyimageloader.GSYImageConst
import com.shuyu.gsyimageloader.GSYImageLoader
import com.shuyu.gsyimageloader.GSYLoadOption
import java.io.File
import java.lang.IllegalStateException

/**
 * 自定义的loader
 * Created by guoshuyu on 2018/1/23.
 */

class CustomLoader(private val context: Context) : GSYImageLoader {

    init {
        val config = ImageLoaderConfiguration.Builder(context.applicationContext)
                .threadPoolSize(5)
                .threadPriority(Thread.MIN_PRIORITY + 3)
                .denyCacheImageMultipleSizesInMemory()
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build()
        ImageLoader.getInstance().init(config)
    }

    override fun loadImage(loadOption: GSYLoadOption, target: Any?, callback: GSYImageLoader.Callback?, extendOption: GSYImageLoader.ExtendedOptions?) {
        if (target !is ImageView) {
            throw IllegalStateException("target must be ImageView")
        }
        if (loadOption.mUri !is String) {
            throw IllegalStateException("loadOption.mUri must be String")
        }
        ImageLoader.getInstance().displayImage(loadOption.mUri as String, target, object : ImageLoadingListener {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                callback?.onSuccess(loadedImage)
            }

            override fun onLoadingStarted(imageUri: String?, view: View?) {
                callback?.onStart()
            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
            }

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                callback?.onFail(Exception(failReason?.toString()))
            }
        })
    }

    override fun clearCache(type: Int) {
        when (type) {
            GSYImageConst.CLEAR_ALL_CACHE -> {
                ImageLoader.getInstance().clearDiskCache()
                ImageLoader.getInstance().clearMemoryCache()
            }
            GSYImageConst.CLEAR_MEMORY_CACHE ->
                ImageLoader.getInstance().clearMemoryCache()
            GSYImageConst.CLEAR_DISK_CACHE ->
                ImageLoader.getInstance().clearDiskCache()
        }
    }

    override fun clearCacheKey(type: Int, loadOption: GSYLoadOption) {
        if (loadOption.mUri !is String) {
            throw IllegalStateException("loadOption.mUri must be String")
        }
        val diskCache = ImageLoader.getInstance().diskCache
        val memoryCache = ImageLoader.getInstance().memoryCache
        when (type) {
            GSYImageConst.CLEAR_ALL_CACHE -> {
                memoryCache.remove(loadOption.mUri as String)
                diskCache.remove(loadOption.mUri as String)
            }
            GSYImageConst.CLEAR_MEMORY_CACHE ->
                memoryCache.remove(loadOption.mUri as String)
            GSYImageConst.CLEAR_DISK_CACHE ->
                diskCache.remove(loadOption.mUri as String)
        }
    }

    override fun isCache(loadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): Boolean {
        if (loadOption.mUri !is String) {
            throw IllegalStateException("loadOption.mUri must be String")
        }
        return ImageLoader.getInstance().diskCache.get(loadOption.mUri as String) != null
    }

    override fun getLocalCache(loadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): File? {
        if (loadOption.mUri !is String) {
            throw IllegalStateException("loadOption.mUri must be String")
        }
        return ImageLoader.getInstance().diskCache.get(loadOption.mUri as String)
    }

    override fun getLocalCacheBitmap(loadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): Bitmap? {
        if (loadOption.mUri !is String) {
            throw IllegalStateException("loadOption.mUri must be String")
        }
        return ImageLoader.getInstance().memoryCache.get(loadOption.mUri as String)
    }

    override fun getCacheSize(): Long? {
        return ImageLoader.getInstance().diskCache.directory.length()
    }

    override fun downloadOnly(loadOption: GSYLoadOption, callback: GSYImageLoader.Callback?, extendOption: GSYImageLoader.ExtendedOptions?) {
        if (loadOption.mUri !is String) {
            throw IllegalStateException("loadOption.mUri must be String")
        }
        ImageLoader.getInstance().loadImage(loadOption.mUri as String, object : ImageLoadingListener {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                callback?.onSuccess(loadedImage)
            }

            override fun onLoadingStarted(imageUri: String?, view: View?) {
                callback?.onStart()
            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {
            }

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {
                callback?.onFail(Exception(failReason?.toString()))
            }
        })
    }
}
