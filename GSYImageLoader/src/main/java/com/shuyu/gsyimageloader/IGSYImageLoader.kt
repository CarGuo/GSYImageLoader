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

    fun loadImage(loadOption: LoadOption, target: Any?, callback: Callback?, extendOption: ExtendedOptions? = null)

    fun clearCache(type: Int = GSYImageConst.CLEAR_DISK_CACHE)

    fun getLocalCache(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions? = null): File?

    fun getLocalCacheBitmap(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions? = null): Bitmap?

    fun downloadOnly(loadOption: LoadOption, callback: IGSYImageLoader.Callback?, extendOption: IGSYImageLoader.ExtendedOptions? = null)

    interface ExtendedOptions {
        fun onOptionsInit(option: Any)
    }

    @UiThread
    interface Callback {
        fun onStart()

        fun onProgress(progress: Int)

        fun onFinish()

        fun onSuccess(result: Any?)

        fun onFail(error: Exception?)
    }
}