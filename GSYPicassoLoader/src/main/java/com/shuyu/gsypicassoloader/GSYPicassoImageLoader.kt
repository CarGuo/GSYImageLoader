package com.shuyu.gsypicassoloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.shuyu.gsyimageloader.GSYImageLoader
import com.shuyu.gsyimageloader.GSYLoadOption
import com.shuyu.gsyimageloader.GSYReflectionHelpers
import com.squareup.picasso.*
import com.squareup.picasso.Target
import java.io.File
import java.io.IOException

/**
 * Picasso图片加载
 * Created by guoshuyu on 2018/1/19.
 */
class GSYPicassoImageLoader(private val context: Context, builder: Picasso.Builder? = null) : GSYImageLoader {

    private var mPicassoLoader: Picasso = if (builder != null) {
        builder.build()
    } else {
        Picasso.with(context)
    }

    override fun loadImage(GSYLoadOption: GSYLoadOption, target: Any?, callback: GSYImageLoader.Callback?, extendOption: GSYImageLoader.ExtendedOptions?) {
        getRequest(GSYLoadOption, extendOption)?.let {
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
            val cache = GSYReflectionHelpers.getField<Cache>(mPicassoLoader, "cache")
            cache.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun clearCacheKey(type: Int, GSYLoadOption: GSYLoadOption) {
        val targetPath: Any? = GSYLoadOption.mUri
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

    override fun getLocalCache(GSYLoadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): File? {
        Log.e(javaClass::getName.toString(), "not support for picasso")
        return null
    }

    override fun isCache(GSYLoadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): Boolean {
        Log.e(javaClass::getName.toString(), "not support for picasso")
        return false
    }

    override fun getLocalCacheBitmap(GSYLoadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = getRequest(GSYLoadOption, extendOption)?.get()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    override fun getCacheSize(): Long? {
        return mPicassoLoader.snapshot.size.toLong()
    }

    override fun downloadOnly(GSYLoadOption: GSYLoadOption, callback: GSYImageLoader.Callback?, extendOption: GSYImageLoader.ExtendedOptions?) {
        getRequest(GSYLoadOption, extendOption)?.into(object : Target {
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

    private fun getRequest(GSYLoadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): RequestCreator? {
        val targetPath: Any? = GSYLoadOption.mUri
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
            if (GSYLoadOption.mErrorImg > 0) {
                it.error(GSYLoadOption.mErrorImg)
            }
            if (GSYLoadOption.mDefaultImg > 0) {
                it.placeholder(GSYLoadOption.mDefaultImg)
            }
            if (GSYLoadOption.isCircle) {

            }
            GSYLoadOption.mSize?.let {
                request?.resize(it.x, it.y)
            }
            if (GSYLoadOption.mTransformations.isNotEmpty()) {
                request?.transform(GSYLoadOption.mTransformations as List<Transformation>)
            }
            extendOption?.let {
                extendOption.onOptionsInit(request!!)
            }
        }
        return request
    }
}
