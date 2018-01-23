package com.shuyu.gsyfrescoimageloader

import android.content.Context
import android.graphics.Bitmap
import com.facebook.binaryresource.FileBinaryResource
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.request.ImageRequest
import com.shuyu.gsyimageloader.GSYImageConst
import com.shuyu.gsyimageloader.GSYImageLoader
import com.shuyu.gsyimageloader.GSYLoadOption
import java.io.File

/**
 * Fresco 图片加载
 * Created by guoshuyu on 2018/1/19.
 */
class GSYFrescoImageLoader(private val context: Context, private var config: ImagePipelineConfig? = null) : GSYImageLoader, GSYFrescoFactory {

    init {
        if (config == null) {
            config = ImagePipelineConfig.newBuilder(context.applicationContext)
                    .setDownsampleEnabled(true)
                    .build()
        }
        Fresco.initialize(context.applicationContext, config)
    }

    override fun loadImage(GSYLoadOption: GSYLoadOption, target: Any?, callback: GSYImageLoader.Callback?, extendOption: GSYImageLoader.ExtendedOptions?) {
        val frescoView = target as SimpleDraweeView
        try {
            initFrescoView(frescoView, GSYLoadOption)
            val request = buildImageRequestWithResource(GSYLoadOption, extendOption)
            val lowRequest = buildLowImageRequest(frescoView, GSYLoadOption, extendOption)
            frescoView.controller = buildDraweeController(frescoView, GSYLoadOption, callback, request, lowRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun clearCache(type: Int) {
        when (type) {
            GSYImageConst.CLEAR_ALL_CACHE -> {
                Fresco.getImagePipeline().clearCaches()
            }
            GSYImageConst.CLEAR_MEMORY_CACHE ->
                Fresco.getImagePipeline().clearMemoryCaches()
            GSYImageConst.CLEAR_DISK_CACHE ->
                Fresco.getImagePipeline().clearDiskCaches()
        }
    }

    override fun clearCacheKey(type: Int, GSYLoadOption: GSYLoadOption) {
        val loadUri = getUri(GSYLoadOption.mUri)
        when (type) {
            GSYImageConst.CLEAR_ALL_CACHE -> {
                Fresco.getImagePipeline().evictFromCache(loadUri)
            }
            GSYImageConst.CLEAR_MEMORY_CACHE ->
                Fresco.getImagePipeline().evictFromMemoryCache(loadUri)
            GSYImageConst.CLEAR_DISK_CACHE ->
                Fresco.getImagePipeline().evictFromDiskCache(loadUri)

        }
    }

    override fun isCache(GSYLoadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): Boolean {
        val loadUri = getUri(GSYLoadOption.mUri)
        return isCached(context, loadUri)
    }

    override fun getLocalCache(GSYLoadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): File? {
        val loadUri = getUri(GSYLoadOption.mUri)
        if (!isCached(context, loadUri))
            return null
        val imageRequest = ImageRequest.fromUri(loadUri)
        val cacheKey = DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest, context)
        val resource = ImagePipelineFactory.getInstance()
                .mainFileCache.getResource(cacheKey)
        return (resource as FileBinaryResource).file
    }

    override fun getLocalCacheBitmap(GSYLoadOption: GSYLoadOption, extendOption: GSYImageLoader.ExtendedOptions?): Bitmap? {
        val loadUri = getUri(GSYLoadOption.mUri)
        if (!isCached(context, loadUri))
            return null
        val request = buildImageRequestWithResource(GSYLoadOption, extendOption)
        val cacheKey = DefaultCacheKeyFactory.getInstance()
                .getBitmapCacheKey(request, context)
        val resource = ImagePipelineFactory.getInstance()
                .bitmapCountingMemoryCache.get(cacheKey)
        return (resource?.get() as CloseableBitmap).underlyingBitmap
    }


    override fun getCacheSize(): Long? {
       return ImagePipelineFactory.getInstance()
                .mainFileCache.size
    }

    override fun downloadOnly(GSYLoadOption: GSYLoadOption, callback: GSYImageLoader.Callback?, extendOption: GSYImageLoader.ExtendedOptions?) {
        val imageRequest = buildImageRequestWithResource(GSYLoadOption, extendOption)
        val imagePipeline = Fresco.getImagePipeline()
        val dataSource2 = imagePipeline.prefetchToDiskCache(imageRequest, context)
        dataSource2.subscribe(object : BaseDataSubscriber<Void>() {
            override fun onNewResultImpl(dataSource: DataSource<Void>) {
                val file = getLocalCache(GSYLoadOption, extendOption)
                callback?.onSuccess(file)
            }

            override fun onFailureImpl(dataSource: DataSource<Void>) {
                callback?.onFail(null)
            }
        }, CallerThreadExecutor.getInstance())

    }
}