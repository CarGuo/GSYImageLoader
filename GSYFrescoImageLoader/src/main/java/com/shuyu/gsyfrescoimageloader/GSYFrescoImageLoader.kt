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
import com.shuyu.gsyimageloader.IGSYImageLoader
import com.shuyu.gsyimageloader.LoadOption
import java.io.File

/**
 * Fresco 图片加载
 * Created by guoshuyu on 2018/1/19.
 */
class GSYFrescoImageLoader(private val context: Context) : IGSYImageLoader, GSYFrescoFactory {

    init {
        val config = ImagePipelineConfig.newBuilder(context.applicationContext)
                .setDownsampleEnabled(true)
                .build()
        Fresco.initialize(context.applicationContext, config)
    }

    override fun loadImage(loadOption: LoadOption, target: Any?, callback: IGSYImageLoader.Callback?, extendOption: IGSYImageLoader.ExtendedOptions?) {
        val frescoView = target as SimpleDraweeView
        try {
            initFrescoView(frescoView, loadOption)
            val request = buildImageRequestWithResource(loadOption, extendOption)
            val lowRequest = buildLowImageRequest(frescoView, loadOption, extendOption)
            frescoView.controller = buildDraweeController(frescoView, loadOption, callback, request, lowRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun clearCache(type: Int) {
        when (type) {
            GSYImageConst.CLEAR_ALL_CACHE -> {
                Fresco.getImagePipeline().clearCaches()
            }
            GSYImageConst.CLEAR_MENORY_CACHE ->
                Fresco.getImagePipeline().clearMemoryCaches()
            GSYImageConst.CLEAR_DISK_CACHE ->
                Fresco.getImagePipeline().clearDiskCaches()
        }
    }


    override fun getLocalCache(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): File? {
        val loadUri = getUri(loadOption.mUri)
        if (!isCached(context, loadUri))
            return null
        val imageRequest = ImageRequest.fromUri(loadUri)
        val cacheKey = DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest, context)
        val resource = ImagePipelineFactory.getInstance()
                .mainFileCache.getResource(cacheKey)
        return (resource as FileBinaryResource).file
    }

    override fun getLocalCacheBitmap(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): Bitmap? {
        val loadUri = getUri(loadOption.mUri)
        if (!isCached(context, loadUri))
            return null
        val request = buildImageRequestWithResource(loadOption, extendOption)
        val cacheKey = DefaultCacheKeyFactory.getInstance()
                .getBitmapCacheKey(request, context)
        val resource = ImagePipelineFactory.getInstance()
                .bitmapCountingMemoryCache.get(cacheKey)
        return (resource?.get() as CloseableBitmap).underlyingBitmap
    }

    override fun downloadOnly(loadOption: LoadOption, callback: IGSYImageLoader.Callback?, extendOption: IGSYImageLoader.ExtendedOptions?) {
        val imageRequest = buildImageRequestWithResource(loadOption, extendOption)
        val imagePipeline = Fresco.getImagePipeline()
        val dataSource2 = imagePipeline.prefetchToDiskCache(imageRequest, context)
        dataSource2.subscribe(object : BaseDataSubscriber<Void>() {
            override fun onNewResultImpl(dataSource: DataSource<Void>) {
                val file = getLocalCache(loadOption, extendOption)
                callback?.onSuccess(file)
            }

            override fun onFailureImpl(dataSource: DataSource<Void>) {
                callback?.onFail(null)
            }
        }, CallerThreadExecutor.getInstance())

    }
}