package com.example.gysimageloader.process.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.renderscript.RSRuntimeException

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool

/**
 * https://github.com/wasabeef/glide-transformations
 */
class BlurTransformation @JvmOverloads constructor(private val radius: Int = MAX_RADIUS, private val sampling: Int = DEFAULT_DOWN_SAMPLING) : BitmapTransformation() {

    override fun transform(context: Context, pool: BitmapPool,
                           toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {

        val width = toTransform.width
        val height = toTransform.height
        val scaledWidth = width / sampling
        val scaledHeight = height / sampling

        var bitmap: Bitmap? = pool.get(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap!!)
        canvas.scale(1 / sampling.toFloat(), 1 / sampling.toFloat())
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(toTransform, 0f, 0f, paint)

        bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                RSBlur.blur(context, bitmap, radius)
            } catch (e: RSRuntimeException) {
                FastBlur.blur(bitmap, radius, true)
            }

        } else {
            FastBlur.blur(bitmap, radius, true)
        }

        return bitmap!!
    }

    override fun key(): String {
        return "BlurTransformation(radius=$radius, sampling=$sampling)"
    }

    companion object {

        private val MAX_RADIUS = 25
        private val DEFAULT_DOWN_SAMPLING = 1
    }
}