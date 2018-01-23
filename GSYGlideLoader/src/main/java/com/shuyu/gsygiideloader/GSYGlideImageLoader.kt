package com.shuyu.gsygiideloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.disklrucache.DiskLruCache
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.shuyu.gsyimageloader.GSYImageConst
import com.shuyu.gsyimageloader.GSYImageLoader
import com.shuyu.gsyimageloader.GSYLoadOption
import java.lang.IllegalStateException
import java.io.File
import com.bumptech.glide.signature.EmptySignature
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.load.engine.cache.MemoryCache
import com.shuyu.gsyimageloader.GSYReflectionHelpers


/**
 * Glide 图片加载
 * Created by guoshuyu on 2018/1/18.
 */
class GSYGlideImageLoader(private val context: Context) : GSYImageLoader {


    override fun loadImage(GSYLoadOption: GSYLoadOption, target: Any?, callback: GSYImageLoader.Callback?, extendOption: GSYImageLoader.ExtendedOptions?) {
        if (target !is ImageView) {
            throw IllegalStateException("target must be ImageView")
        }
        loadImage(GSYLoadOption, extendOption)
                .load(GSYLoadOption.mUri)
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
            GSYImageConst.CLEAR_MEMORY_CACHE ->
                Glide.get(context.applicationContext).clearMemory()
            GSYImageConst.CLEAR_DISK_CACHE ->
                Glide.get(context.applicationContext).clearDiskCache()
        }
    }

    override fun clearCacheKey(type: Int, GSYLoadOption: GSYLoadOption) {
        val deleteDisk = {
            val diskCache = DiskLruCacheWrapper.create(Glide.getPhotoCacheDir(context), (250 * 1024 * 1024).toLong())
            val key = GSYGlideCacheKey(GSYLoadOption.mUri as String, EmptySignature.obtain())
            diskCache.delete(key)
        }
        val deleteMemory = {
            try {
                val key = GSYGlideCacheKey(GSYLoadOption.mUri as String, EmptySignature.obtain());
                val cache = GSYReflectionHelpers.getField<MemoryCache>(Glide.get(context.applicationContext), "memoryCache")
                cache.remove(key)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        when (type) {
            GSYImageConst.CLEAR_ALL_CACHE -> {
                deleteMemory.invoke()
                deleteDisk.invoke()
            }
            GSYImageConst.CLEAR_MEMORY_CACHE -> {
                deleteMemory()
            }
            GSYImageConst.CLEAR_DISK_CACHE -> {
                deleteDisk.invoke()
            }

        }
    }

    override fun getLocalCache(GSYLoadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): File? {
        val future = loadImage(GSYLoadOption, extendOption)
                .asFile().load(GSYLoadOption.mUri)
        return future.submit().get()
    }


    override fun isCache(GSYLoadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): Boolean {
        // 寻找缓存图片
        val file = DiskLruCacheWrapper.create(Glide.getPhotoCacheDir(context), (250 * 1024 * 1024).toLong())
                .get(GSYGlideCacheKey(GSYLoadOption.mUri as String, EmptySignature.obtain()))
        return file != null
    }

    override fun getLocalCacheBitmap(GSYLoadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): Bitmap? {
        val future = loadImage(GSYLoadOption, extendOption)
                .asBitmap().load(GSYLoadOption.mUri)
        return future.submit().get()
    }


    override fun getCacheSize(): Long? {
        val cache =  DiskLruCacheWrapper.create(Glide.getPhotoCacheDir(context), (250 * 1024 * 1024).toLong())
        val diskLruCache = GSYReflectionHelpers.getField<DiskLruCache>(cache, "diskLruCache")
        return diskLruCache.size()
    }

    override fun downloadOnly(GSYLoadOption: GSYLoadOption, callback: GSYImageLoader.Callback?, extendOption: GSYImageLoader.ExtendedOptions?) {
        loadImage(GSYLoadOption, extendOption).downloadOnly().load(GSYLoadOption.mUri).into(GSYImageDownLoadTarget(callback))
    }

    private fun loadImage(GSYLoadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): RequestManager {
        return Glide.with(context.applicationContext)
                .setDefaultRequestOptions(getOption(GSYLoadOption, extendOption))
    }

    @SuppressWarnings("CheckResult")
    private fun getOption(GSYLoadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): RequestOptions {
        val requestOptions = RequestOptions()
        if (GSYLoadOption.mErrorImg > 0) {
            requestOptions.error(GSYLoadOption.mErrorImg)
        }
        if (GSYLoadOption.mDefaultImg > 0) {
            requestOptions.placeholder(GSYLoadOption.mDefaultImg)
        }
        if (GSYLoadOption.isCircle) {
            requestOptions.circleCrop()
        }
        GSYLoadOption.mSize?.let {
            requestOptions.override(it.x, it.y)
        }
        if(GSYLoadOption.mTransformations.isNotEmpty()) {
            requestOptions.transform(MultiTransformation(GSYLoadOption.mTransformations as ArrayList<Transformation<Bitmap>>))
        }
        extendOption?.let {
            extendOption.onOptionsInit(requestOptions)
        }
        return requestOptions
    }

}

