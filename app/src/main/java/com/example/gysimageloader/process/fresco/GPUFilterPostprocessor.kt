package com.example.gysimageloader.process.fresco

import android.content.Context
import android.graphics.Bitmap

import com.facebook.imagepipeline.request.BasePostprocessor

import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageFilter

abstract class GPUFilterPostprocessor(context: Context, private val filter: GPUImageFilter) : BasePostprocessor() {

    private val context: Context = context.applicationContext

    override fun process(dest: Bitmap, source: Bitmap) {
        val gpuImage = GPUImage(context)
        gpuImage.setImage(source)
        gpuImage.setFilter(filter)
        val bitmap = gpuImage.bitmapWithFilterApplied

        super.process(dest, bitmap)
    }

    override fun getName(): String {
        return javaClass.simpleName
    }

    fun <T> getFilter(): T {
        return filter as T
    }
}
