package com.shuyu.gsypicassoloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.shuyu.gsyimageloader.IGSYImageLoader
import com.shuyu.gsyimageloader.LoadOption
import com.shuyu.gsyimageloader.ReflectionHelpers
import com.squareup.picasso.*
import com.squareup.picasso.Target
import java.io.File
import java.io.IOException

/**
 * Picasso图片加载
 * Created by guoshuyu on 2018/1/19.
 */

class GSYPicassoImageLoader(private val context: Context) : IGSYImageLoader {

    override fun loadImage(loadOption: LoadOption, target: Any?, callback: IGSYImageLoader.Callback?, extendOption: IGSYImageLoader.ExtendedOptions?) {
        getRequest(loadOption, extendOption)?.let {
            it.into(target as ImageView, object : Callback {
                override fun onSuccess() {
                    callback?.onSuccess(null)
                }

                override fun onError() {
                    callback?.onFail(null)
                }
            })
        }
    }

    override fun clearCache(type: Int) {
        try {
            val cache = ReflectionHelpers.getField<Cache>(Picasso.with(context), "cache")
            cache.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getLocalCache(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): File? {
        //picasso没有获取本地缓存接口
        return null
    }

    override fun isCache(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): Boolean {
        //not support now
        return false
    }

    override fun getLocalCacheBitmap(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = getRequest(loadOption, extendOption)?.get()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    override fun downloadOnly(loadOption: LoadOption, callback: IGSYImageLoader.Callback?, extendOption: IGSYImageLoader.ExtendedOptions?) {
        getRequest(loadOption, extendOption)?.into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                callback?.onStart()
            }

            override fun onBitmapFailed(errorDrawable: Drawable?) {
                callback?.onFail(null);
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                callback?.onSuccess(bitmap)
            }
        })
    }

    private fun getRequest(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): RequestCreator? {
        val targetPath: Any? = loadOption.mUri
        var request: RequestCreator? = null
        when (targetPath) {
            is File -> {
                request = Picasso.with(context).load(targetPath)
            }
            is String -> {
                request = Picasso.with(context).load(targetPath)
            }
            is Uri -> {
                request = Picasso.with(context).load(targetPath)
            }
            is Int -> {
                request = Picasso.with(context).load(targetPath)
            }
        }
        request?.let {
            if (loadOption.mErrorImg > 0) {
                it.error(loadOption.mErrorImg)
            }
            if (loadOption.mDefaultImg > 0) {
                it.placeholder(loadOption.mDefaultImg)
            }
            if (loadOption.isCircle) {

            }
            loadOption.mSize?.let {
                request?.resize(it.x, it.y)
            }
            extendOption?.let {
                extendOption.onOptionsInit(request!!)
            }
        }
        return request
    }
}
