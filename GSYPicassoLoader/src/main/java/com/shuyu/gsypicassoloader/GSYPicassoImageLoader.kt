package com.shuyu.gsypicassoloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
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

class GSYPicassoImageLoader(private val context: Context, builder: Picasso.Builder? = null) : IGSYImageLoader {

    private var mPicassoLoader: Picasso = if (builder != null) {
        builder.build()
    } else {
        Picasso.with(context)
    }

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
            val cache = ReflectionHelpers.getField<Cache>(mPicassoLoader, "cache")
            cache.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun clearCacheKey(type: Int, loadOption: LoadOption) {
        val targetPath: Any? = loadOption.mUri
        when (targetPath) {
            is File -> {
                mPicassoLoader.invalidate(targetPath)
            }
            is String -> {
                mPicassoLoader.invalidate(targetPath)
            }
            is Uri -> {
                mPicassoLoader.invalidate(targetPath)
            }
        }
    }

    override fun getLocalCache(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): File? {
        Log.e(javaClass::getName.toString(), "not support for picasso")
        return null
    }

    override fun isCache(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): Boolean {
        Log.e(javaClass::getName.toString(), "not support for picasso")
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

    override fun getCacheSize(): Long? {
        return mPicassoLoader.snapshot.size.toLong()
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
                request = mPicassoLoader.load(targetPath)
            }
            is String -> {
                request = mPicassoLoader.load(targetPath)
            }
            is Uri -> {
                request = mPicassoLoader.load(targetPath)
            }
            is Int -> {
                request = mPicassoLoader.load(targetPath)
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
            if (loadOption.mTransformations.isNotEmpty()) {
                request?.transform(loadOption.mTransformations as List<Transformation>)
            }
            extendOption?.let {
                extendOption.onOptionsInit(request!!)
            }
        }
        return request
    }
}
