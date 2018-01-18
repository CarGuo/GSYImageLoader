package com.shuyu.gsyimageloader

import android.graphics.Point

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

    //是否本地路径
    var isLocal: Boolean = false

    //是否gif
    var isAnima: Boolean = false

    //大小
    var mSize: Point? = null

    //图片
    var mUri: Any? = null

    fun setDefaultImg(defaultImg: Int): LoadOption {
        this.mDefaultImg = defaultImg
        return this
    }

    fun setErrorImg(errorImg: Int): LoadOption {
        this.mErrorImg = errorImg
        return this
    }

    fun setCornerRadius(cornerRadius: Int): LoadOption {
        this.mCornerRadius = cornerRadius
        return this
    }

    fun setCircle(circle: Boolean): LoadOption {
        isCircle = circle
        return this
    }

    fun setLoadLocalPath(loadLocalPath: Boolean): LoadOption {
        this.isLocal = loadLocalPath
        return this
    }

    fun setAnima(anima: Boolean): LoadOption {
        isAnima = anima
        return this
    }

    fun setSize(size: Point): LoadOption {
        this.mSize = size
        return this
    }

    fun setUri(uri: Any): LoadOption {
        this.mUri = uri
        return this
    }


}
