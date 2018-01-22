package com.example.gysimageloader.process.picasso

import android.graphics.*
import com.squareup.picasso.Transformation

/**
 * Created by guoshuyu on 2018/1/22.
 * https://github.com/wasabeef/picasso-transformations
 */
class ColorFilterTransformations(private val mColor: Int) : Transformation {

    override fun transform(source: Bitmap): Bitmap {

        val width = source.width
        val height = source.height

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.colorFilter = PorterDuffColorFilter(mColor, PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(source, 0f, 0f, paint)
        source.recycle()

        return bitmap
    }

    override fun key(): String {
        return "ColorFilterTransformation(color=$mColor)"
    }
}