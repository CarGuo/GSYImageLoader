package com.shuyu.gsyfrescoimageloader

import android.content.Context
import android.graphics.drawable.Animatable
import android.net.Uri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.shuyu.gsyimageloader.IGSYImageLoader
import com.shuyu.gsyimageloader.LoadOption

/**
 * Created by guoshuyu on 2018/1/19.
 */
interface GSYFrescoFactory {

    fun isCached(context: Context, uri: Uri?): Boolean {
        val imagePipeline = Fresco.getImagePipeline()
        val dataSource = imagePipeline.isInDiskCache(uri) ?: return false
        val imageRequest = ImageRequest.fromUri(uri)
        val cacheKey = DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest, context)
        val resource = ImagePipelineFactory.getInstance()
                .mainFileCache.getResource(cacheKey)
        return resource != null && dataSource.result != null && dataSource.result!!
    }

    fun getUri(targetUri: Any?): Uri? {
        var loadUri: Uri? = null
        when (targetUri) {
            is String -> {
                val uri = Uri.parse(targetUri)
                loadUri = uri
            }
            is Uri -> {
                loadUri = targetUri
            }
        }
        return loadUri
    }

    fun initFrescoView(simpleDraweeView: SimpleDraweeView, loadOption: LoadOption) {
        if (loadOption.mDefaultImg > 0) {
            simpleDraweeView.hierarchy.setPlaceholderImage(loadOption.mDefaultImg)
        }
        if (loadOption.mErrorImg > 0) {
            simpleDraweeView.hierarchy.setFailureImage(loadOption.mErrorImg)
        }
        if (loadOption.isCircle) {
            setRoundingParmas(simpleDraweeView, getRoundingParams(simpleDraweeView).setRoundAsCircle(true))
        } else {
            setRoundingParmas(simpleDraweeView, getRoundingParams(simpleDraweeView).setRoundAsCircle(false))
        }
    }

    fun buildImageRequestWithResource(loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): ImageRequest? {
        val remoteTarget = loadOption.mUri
        var builder: ImageRequestBuilder? = null
        when (remoteTarget) {
            is Int -> {
                builder = ImageRequestBuilder.newBuilderWithResourceId(remoteTarget)
            }
            is Uri -> {
                builder = ImageRequestBuilder.newBuilderWithSource(remoteTarget)

            }
            is String -> {
                val uri = Uri.parse(remoteTarget)
                builder = ImageRequestBuilder.newBuilderWithSource(uri)
            }
        }
        if (loadOption.mSize != null) {
            builder?.resizeOptions = ResizeOptions(loadOption.mSize!!.x, loadOption.mSize!!.y)
        } else {
            builder?.resizeOptions = null
        }
        extendOption?.let {
            extendOption.onOptionsInit(builder!!)
        }
        return builder?.build()
    }

    fun buildLowImageRequest(simpleDraweeView: SimpleDraweeView, loadOption: LoadOption, extendOption: IGSYImageLoader.ExtendedOptions?): ImageRequest? {
        /*var lowThumbnail: String? = null
        if (TextUtils.isEmpty(fresco.getLowThumbnailUrl())) {
            return null
        }
        lowThumbnail = fresco.getLowThumbnailUrl()
        val uri = Uri.parse(lowThumbnail)
        return ImageRequest.fromUri(uri)*/
        return null
    }

    fun buildDraweeController(simpleDraweeView: SimpleDraweeView, loadOption: LoadOption, callback: IGSYImageLoader.Callback?,
                              imageRequest: ImageRequest?, lowRequest: ImageRequest?): DraweeController {
        return Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setAutoPlayAnimations(loadOption.isAnima)
                //.setTapToRetryEnabled(fresco.getTapToRetryEnabled())
                .setLowResImageRequest(lowRequest)
                .setControllerListener(object : ControllerListener<Any> {
                    override fun onSubmit(id: String?, callerContext: Any?) {
                        callback?.onStart()
                    }

                    override fun onFinalImageSet(id: String?, imageInfo: Any?, animatable: Animatable?) {
                        callback?.onSuccess(Exception(id))
                    }

                    override fun onRelease(id: String?) {

                    }

                    override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {
                        callback?.onFail(Exception(throwable))
                    }

                    override fun onFailure(id: String?, throwable: Throwable?) {
                        callback?.onFail(Exception(throwable))
                    }

                    override fun onIntermediateImageSet(id: String?, imageInfo: Any?) {
                        callback?.onSuccess(id)
                    }
                })
                .setOldController(simpleDraweeView.controller)
                .build()
    }

    fun getRoundingParams(simpleDraweeView: SimpleDraweeView): RoundingParams {
        var roundingParams = simpleDraweeView.hierarchy.roundingParams
        if (roundingParams == null) {
            roundingParams = RoundingParams()
        }
        return roundingParams
    }

    fun setRoundingParmas(simpleDraweeView: SimpleDraweeView, roundingParmas: RoundingParams?) {
        simpleDraweeView.hierarchy.roundingParams = roundingParmas
    }
}