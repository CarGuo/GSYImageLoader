package com.example.gysimageloader.process.glide

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.*

/**
 * Created by guoshuyu on 2018/1/22.
 */
object RSBlur {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Throws(RSRuntimeException::class)
    fun blur(context: Context, bitmap: Bitmap, radius: Int): Bitmap {
        var rs: RenderScript? = null
        var input: Allocation? = null
        var output: Allocation? = null
        var blur: ScriptIntrinsicBlur? = null
        try {
            rs = RenderScript.create(context)
            rs!!.messageHandler = RenderScript.RSMessageHandler()
            input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT)
            output = Allocation.createTyped(rs, input!!.type)
            blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

            blur!!.setInput(input)
            blur.setRadius(radius.toFloat())
            blur.forEach(output)
            output!!.copyTo(bitmap)
        } finally {
            if (rs != null) {
                rs.destroy()
            }
            if (input != null) {
                input.destroy()
            }
            if (output != null) {
                output.destroy()
            }
            if (blur != null) {
                blur.destroy()
            }
        }

        return bitmap
    }
}