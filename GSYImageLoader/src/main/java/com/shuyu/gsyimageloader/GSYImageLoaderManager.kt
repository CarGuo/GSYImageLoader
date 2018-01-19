package com.shuyu.gsyimageloader

import kotlin.properties.Delegates

/**
 * 图片加载管理
 * Created by guoshuyu on 2018/1/18.
 */
class GSYImageLoaderManager private constructor(private var mImageLoader: IGSYImageLoader) : IGSYImageLoader by mImageLoader {

    companion object {
        //委托notNull，这个值在被获取之前没有被分配，它就会抛出一个异常。
        var sInstance: GSYImageLoaderManager by Delegates.notNull()

        fun initialize(imageLoader: IGSYImageLoader) {
            sInstance = GSYImageLoaderManager(imageLoader)
        }
    }

    fun imageLoader(): IGSYImageLoader {
        return this
    }

    fun <T : IGSYImageLoader> imageLoaderExtend(): T {
        return this as T
    }
}