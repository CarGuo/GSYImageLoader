package com.shuyu.gsygiideloader

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.shuyu.gsyimageloader.GSYImageConst
import com.shuyu.gsyimageloader.IGSYImageLoader
import com.shuyu.gsyimageloader.LoadOption
import java.lang.IllegalStateException
import java.io.File


/**
 * Glide 图片加载
 * Created by guoshuyu on 2018/1/18.
 */
class GSYGlideImageLoader(private val context: Context) : IGSYImageLoader {


    override fun loadImage(loadOption: LoadOption, target: Any?, callback: IGSYImageLoader.Callback?, extendOption: IGSYImageLoader.ExtendedOptions?) {
        if (target !is ImageView) {
            throw IllegalStateException("target must be ImageView")
        }
        loadImage(loadOption, extendOption)
                .load(loadOption.mUri)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        callback?.let {
                            it.onFail(e)
                        }
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        callback?.let {
                            it.onSuccess(resource)
                        }
                        return false
                    }
                })
                .into(target)
    }

    override fun clearCache(type: Int) {
        when (type) {
            GSYImageConst.CLEAR_ALL_CACHE -> {
                Glide.get(context.applicationContext).clearMemory()
                Glide.get(context.applicationContext).clearDiskCache()
            }
            GSYImageConst.CLEAR_MENORY_CACHE ->
                Glide.get(context.applicationContext).clearMemory()
            GSYImageConst.CLEAR_DISK_CACHE ->
                Glide.get(context.applicationContext).clearDiskCache()
        }
    }

    override fun getLocalCache(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): File? {
        val future = loadImage(loadOption, extendOption)
                .asFile().load(loadOption.mUri)
        return future.submit().get()
    }

    override fun getLocalCacheBitmap(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): Bitmap? {
        val future = loadImage(loadOption, extendOption)
                .asBitmap().load(loadOption.mUri)
        return future.submit().get()
    }

    override fun downloadOnly(loadOption: LoadOption, callback: IGSYImageLoader.Callback?, extendOption: IGSYImageLoader.ExtendedOptions?) {
        loadImage(loadOption, extendOption).downloadOnly().load(loadOption.mUri).into(GSYImageDownLoadTarget(callback))
    }

    private fun loadImage(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): RequestManager {
        return Glide.with(context.applicationContext)
                .setDefaultRequestOptions(getOption(loadOption, extendOption))
    }

    @SuppressLint("CheckResult")
    private fun getOption(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): RequestOptions {
        val requestOptions = RequestOptions()
        if (loadOption.mErrorImg > 0) {
            requestOptions.error(loadOption.mErrorImg)
        }
        if (loadOption.mDefaultImg > 0) {
            requestOptions.placeholder(loadOption.mDefaultImg)
        }
        if (loadOption.isCircle) {
            requestOptions.circleCrop()
        }
        loadOption.mSize?.let {
            requestOptions.override(it.x, it.y)
        }
        extendOption?.let {
            extendOption.onOptionsInit(requestOptions)
        }
        return requestOptions
    }

}