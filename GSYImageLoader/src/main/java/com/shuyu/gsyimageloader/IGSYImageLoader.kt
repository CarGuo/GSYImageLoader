package com.shuyu.gsyimageloader

/**
 * Created by guoshuyu on 2018/1/17.
 */

import android.content.Context
import android.support.annotation.UiThread


interface IGSYImageLoader {

    fun loadImage(context: Context, loadOption: LoadOption, target: Any?, callback: Callback?)

    @UiThread
    interface Callback {
        fun onStart()

        fun onProgress(progress: Int)

        fun onFinish()

        fun onSuccess()

        fun onFail(error: Exception?)
    }
}