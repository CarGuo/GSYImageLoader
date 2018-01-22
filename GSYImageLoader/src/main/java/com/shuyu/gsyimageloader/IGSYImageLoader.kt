package com.shuyu.gsyimageloader

/**
 * Created by guoshuyu on 2018/1/17.
 */

import android.graphics.Bitmap
import android.support.annotation.UiThread
import java.io.File

/**
 * 图片加载接口
 * Created by guoshuyu on 2018/1/18.
 */
interface IGSYImageLoader {

    /**
     * 加载图片
     * @param loadOption
     * @param target
     * @param callback
     * @param extendOption
     */
    fun loadImage(loadOption: LoadOption, target: Any?, callback: Callback?, extendOption: ExtendedOptions? = null)

    /**
     * 清除缓存
     * @param type
     */
    fun clearCache(type: Int = GSYImageConst.CLEAR_DISK_CACHE)

    /**
     * 清除指定缓存
     * @param type
     * @param loadOption
     */
    fun clearCacheKey(type: Int = GSYImageConst.CLEAR_DISK_CACHE, loadOption: LoadOption)

    /**
     * 是否已经缓存到本地
     * @param loadOption
     * @param extendOption
     * @return Boolean
     */
    fun isCache(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions? = null):Boolean

    /**
     * 获取本地缓存
     * @param loadOption
     * @param extendOption
     * @return File
     */
    fun getLocalCache(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions? = null): File?

    /**
     * 获取本地缓存bitmap
     * @param loadOption
     * @param extendOption
     * @return Bitmap
     */
    fun getLocalCacheBitmap(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions? = null): Bitmap?

    /**
     * 下载图片
     * @param loadOption
     * @param callback
     * @param extendOption
     * @return Bitmap
     */
    fun downloadOnly(loadOption: LoadOption, callback: IGSYImageLoader.Callback?, extendOption: IGSYImageLoader.ExtendedOptions? = null)

    /**
     * 额外配置支持
     */
    interface ExtendedOptions {
        fun onOptionsInit(option: Any)
    }

    //TODO cache size

    //TODO cache path

    //TODO image process

    /**
     * 回调接口
     */
    @UiThread
    interface Callback {
        fun onStart()

        fun onProgress(progress: Int)

        fun onFinish()

        fun onSuccess(result: Any?)

        fun onFail(error: Exception?)
    }
}