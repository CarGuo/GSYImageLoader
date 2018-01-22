package com.example.gysimageloader.process.fresco

import android.content.Context

import com.facebook.cache.common.CacheKey
import com.facebook.cache.common.SimpleCacheKey

import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter

/**
 * https://github.com/wasabeef/fresco-processors
 */
class BrightnessFilterPostprocessor @JvmOverloads constructor(context: Context, private val brightness: Float = 0.0f) : GPUFilterPostprocessor(context, GPUImageBrightnessFilter()) {

    init {

        val filter = getFilter<GPUImageBrightnessFilter>()
        filter.setBrightness(brightness)
    }

    override fun getPostprocessorCacheKey(): CacheKey? {
        return SimpleCacheKey("brightness=" + brightness)
    }
}