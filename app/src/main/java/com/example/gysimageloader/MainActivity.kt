package com.example.gysimageloader

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import com.example.gysimageloader.process.fresco.BrightnessFilterPostprocessor
import com.example.gysimageloader.process.glide.BlurTransformation
import com.example.gysimageloader.process.picasso.ColorFilterTransformations
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.shuyu.gsyfrescoimageloader.GSYFrescoImageLoader
import com.shuyu.gsygiideloader.GSYGlideImageLoader
import com.shuyu.gsyimageloader.GSYImageLoaderManager
import com.shuyu.gsyimageloader.IGSYImageLoader
import com.shuyu.gsyimageloader.LoadOption
import com.shuyu.gsypicassoloader.GSYPicassoImageLoader
import com.squareup.picasso.RequestCreator
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.async
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {

        /**
         * 获取当前加载器
         */
        private fun getLoader(): IGSYImageLoader {
            return GSYImageLoaderManager.sInstance.imageLoader()
        }

        /**
         * 获取图片加载配置
         */
        private fun getOption(url: String, po: Int = 0): LoadOption {
            val loadOption = LoadOption()
                    .setDefaultImg(R.mipmap.ic_launcher)
                    .setErrorImg(R.mipmap.ic_launcher)
                    .setCircle(po == 2)
                    .setSize(if (po == 1) Point(10, 10) else null)
                    .setUri(url)
            val process = getProcess()
            if (po == 3) {
                process?.let {
                    loadOption.setTransformations(process)
                }
            }
            return loadOption
        }

        /**
         * 获取图片处理
         */
        private fun getProcess(): Any? {
            var process: Any? = null
            when (GSYApplication.sLoader) {
                is GSYFrescoImageLoader -> {
                    process = BrightnessFilterPostprocessor(GSYApplication.instance, -0.8f)
                }
                is GSYGlideImageLoader -> {
                    process = BlurTransformation()
                }
                is GSYPicassoImageLoader -> {
                    process = ColorFilterTransformations(Color.GREEN)
                }
            }
            return process
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val adapter = if (GSYApplication.sLoader is GSYFrescoImageLoader) {
            ImageFrescoAdapter(this, GSYApplication.instance.mImageList)
        } else {
            ImageAdapter(this, GSYApplication.instance.mImageList)
        }


        imageList.adapter = adapter
        imageList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

        }

        /**
         * 清除缓存
         */
        clearCache.setOnClickListener {
            async {
                getLoader().clearCache()
                //getLoader().clearCacheKey(loadOption = getOption(GSYApplication.instance.mImageList[0]))
                //getLoader().clearCacheKey(GSYImageConst.CLEAR_ALL_CACHE, getOption(GSYApplication.instance.mImageList[0]))
                //getLoader().clearCache(GSYImageConst.CLEAR_ALL_CACHE)
            }
        }

        /**
         * 获取本地缓存
         */
        getCache.setOnClickListener {
            val isCache = getLoader().isCache(getOption(GSYApplication.instance.mImageList[0]))
            Debuger.printfLog("isCache " + isCache)
            Debuger.printfLog("Cache Size " + getLoader().getCacheSize())
            async {
                val file = getLoader().getLocalCache(getOption(GSYApplication.instance.mImageList[0]))
                file?.let {
                    Debuger.printfLog(it.absolutePath)
                }
            }
        }

        /**
         * 获取bitmap缓存
         */
        getBitmap.setOnClickListener {
            async {
                val file = getLoader().getLocalCacheBitmap(getOption(GSYApplication.instance.mImageList[0]))
                file?.let {
                    Debuger.printfLog(it.toString())
                }
            }
        }

        /**
         * 下载图片
         */
        downLoad.setOnClickListener {
            getLoader().downloadOnly(getOption(GSYApplication.instance.mImageList[0]), object : IGSYImageLoader.Callback {
                override fun onStart() {
                    Debuger.printfLog("download onStart")
                }

                override fun onSuccess(result: Any?) {
                    result?.let {
                        when (result) {
                            is File -> {
                                Debuger.printfLog("download onSuccess " + result.absolutePath)
                            }
                            is Bitmap -> {
                                Debuger.printfLog("download onSuccess " + result.toString())
                            }
                        }
                    }
                }

                override fun onFail(error: Exception?) {
                    Debuger.printfLog("download onFail")
                }
            })

        }


    }

    /**
     * 额外配置处理
     */
    class ObjectExtendOption(private val position: Int) : IGSYImageLoader.ExtendedOptions {

        /**
         * @param option 配置对象
         * Glide    com.bumptech.glide.request.RequestOptions
         * Picasso  com.squareup.picasso.RequestCreator
         * Fresco   com.facebook.imagepipeline.request.ImageRequestBuilder
         */
        @SuppressLint("CheckResult")
        override fun onOptionsInit(option: Any) {
            when (option) {
                is RequestOptions -> {
                    //Glide
                    if (position == 6) {
                        option.circleCrop()
                        option.override(100, 100)
                    }
                }
                is RequestCreator -> {
                    //Picasso
                    if (position == 2) {
                        option.rotate(60F)
                    }
                }
                is ImageRequestBuilder -> {
                    //Fresco
                    if (position == 2) {
                        option.rotationOptions = RotationOptions.forceRotation(180)
                    } else {
                        option.rotationOptions = null
                    }
                }
            }
        }
    }

    class ImageAdapter(private val context: Context, private val dataList: List<String>) : BaseAdapter() {

        override fun getView(p0: Int, convertView: View?, p2: ViewGroup?): View? {
            val holder: ViewHolder
            val view: View
            if (convertView == null) {
                holder = ViewHolder()
                view = LayoutInflater.from(context).inflate(R.layout.layout_image_item, null)
                holder.imageView = view.findViewById(R.id.image_item)
                view.tag = holder
            } else {
                view = convertView
                holder = convertView.tag as ViewHolder
            }
            val loadOption = getOption(dataList[p0], p0)
            getLoader().loadImage(loadOption, holder.imageView, object : IGSYImageLoader.Callback {
                override fun onStart() {

                }

                override fun onSuccess(result: Any?) {
                }

                override fun onFail(error: Exception?) {
                }
            }, ObjectExtendOption(p0))
            return view
        }

        override fun getItem(p0: Int): Any {
            return dataList[p0]
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return dataList.size
        }

        internal inner class ViewHolder {
            var imageView: ImageView? = null
        }
    }


    class ImageFrescoAdapter(private val context: Context, private val dataList: List<String>) : BaseAdapter() {

        override fun getView(p0: Int, convertView: View?, p2: ViewGroup?): View? {
            val holder: ViewHolder
            val view: View
            if (convertView == null) {
                holder = ViewHolder()
                view = LayoutInflater.from(context).inflate(R.layout.layout_fresco_image_item, null)
                holder.imageView = view.findViewById(R.id.image_item_fresco)
                view.tag = holder
            } else {
                view = convertView
                holder = convertView.tag as ViewHolder
            }
            val loadOption = getOption(dataList[p0], p0)
            getLoader().loadImage(loadOption, holder.imageView, object : IGSYImageLoader.Callback {
                override fun onStart() {

                }

                override fun onSuccess(result: Any?) {
                }

                override fun onFail(error: Exception?) {
                }
            }, ObjectExtendOption(p0))
            return view
        }

        override fun getItem(p0: Int): Any {
            return dataList[p0]
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return dataList.size
        }

        internal inner class ViewHolder {
            var imageView: SimpleDraweeView? = null
        }
    }

}
