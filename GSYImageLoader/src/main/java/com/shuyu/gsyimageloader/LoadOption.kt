package com.shuyu.gsyimageloader

import android.graphics.Point
import java.util.HashMap

/**
 * 图片加载配置
 */
class LoadOption {

    //默认图片
    var mDefaultImg: Int = 0

    //圆角
    var mCornerRadius: Int = 0

    //错误图片
    var mErrorImg: Int = 0

    //是否圆形
    var isCircle: Boolean = false

    //是否播放gif
    var isPlayGif: Boolean = false

    //大小
    var mSize: Point? = null

    //图片
    var mUri: Any? = null

    val mTransformations: ArrayList<Any> = ArrayList()

    fun setDefaultImg(defaultImg: Int): LoadOption {
        this.mDefaultImg = defaultImg
        return this
    }

    fun setErrorImg(errorImg: Int): LoadOption {
        this.mErrorImg = errorImg
        return this
    }

    //todo 对圆角的支持
    fun setCornerRadius(cornerRadius: Int): LoadOption {
        this.mCornerRadius = cornerRadius
        return this
    }

    fun setCircle(circle: Boolean): LoadOption {
        isCircle = circle
        return this
    }

    //todo 对GIF的支持
    fun setPlayGif(playGif: Boolean): LoadOption {
        isPlayGif = playGif
        return this
    }

    fun setSize(size: Point?): LoadOption {
        this.mSize = size
        return this
    }

    fun setUri(uri: Any): LoadOption {
        this.mUri = uri
        return this
    }

    /**
     *
     * https://github.com/wasabeef/picasso-transformations
     * https://github.com/wasabeef/glide-transformations
     * https://github.com/wasabeef/fresco-processors
     */
    fun setTransformations(transform:Any): LoadOption  {
        mTransformations.add(transform)
        return this
    }
}
