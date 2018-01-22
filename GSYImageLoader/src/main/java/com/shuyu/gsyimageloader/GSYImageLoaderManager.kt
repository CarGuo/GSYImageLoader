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

        /**
         * 静态初始化、建议Application中初始化
         * @param imageLoader 内含GSYPicassoImageLoader、GSYFrescoImageLoader、GSYPicassoImageLoader
         */
        fun initialize(imageLoader: IGSYImageLoader) {
            sInstance = GSYImageLoaderManager(imageLoader)
        }
    }

    /**
     * 图片加载对象
     */
    fun imageLoader(): IGSYImageLoader {
        return this
    }

    /**
     * 强制转换的图片加载对象
     */
    fun <T : IGSYImageLoader> imageLoaderExtend(): T {
        return this as T
    }
}