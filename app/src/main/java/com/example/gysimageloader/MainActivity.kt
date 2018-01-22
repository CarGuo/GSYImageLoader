package com.example.gysimageloader

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
import com.example.gysimageloader.process.fresco.BrightnessFilterPostprocessor
import com.example.gysimageloader.process.glide.BlurTransformation
import com.example.gysimageloader.process.picasso.ColorFilterTransformations
import com.facebook.drawee.view.SimpleDraweeView
import com.shuyu.gsyfrescoimageloader.GSYFrescoImageLoader
import com.shuyu.gsygiideloader.GSYGlideImageLoader
import com.shuyu.gsyimageloader.GSYImageLoaderManager
import com.shuyu.gsyimageloader.IGSYImageLoader
import com.shuyu.gsyimageloader.LoadOption
import com.shuyu.gsypicassoloader.GSYPicassoImageLoader
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.async
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        private fun getLoader(): IGSYImageLoader {
            return GSYImageLoaderManager.sInstance.imageLoader()
        }

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
         *
         */
        private fun getProcess(): Any? {
            var process: Any? = null
            when (GSYApplication.instance.getInitImageLoader()) {
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


        val adapter = if (GSYApplication.instance.getInitImageLoader() is GSYFrescoImageLoader) {
            ImageFrescoAdapter(this, GSYApplication.instance.mImageList)
        } else {
            ImageAdapter(this, GSYApplication.instance.mImageList)
        }


        imageList.adapter = adapter
        imageList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

        }

        clearCache.setOnClickListener {
            async {
                getLoader().clearCache()
                //getLoader().clearCacheKey(loadOption = getOption(GSYApplication.instance.mImageList[0]))
                //getLoader().clearCacheKey(GSYImageConst.CLEAR_ALL_CACHE, getOption(GSYApplication.instance.mImageList[0]))
                //getLoader().clearCache(GSYImageConst.CLEAR_ALL_CACHE)
            }
        }
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
        getBitmap.setOnClickListener {
            async {
                val file = getLoader().getLocalCacheBitmap(getOption(GSYApplication.instance.mImageList[0]))
                file?.let {
                    Debuger.printfLog(it.toString())
                }
            }
        }
        downLoad.setOnClickListener {
            getLoader().downloadOnly(getOption(GSYApplication.instance.mImageList[0]), object : IGSYImageLoader.Callback {
                override fun onStart() {
                    Debuger.printfLog("download onStart")
                }

                override fun onProgress(progress: Int) {
                    Debuger.printfLog("download onProgress")
                }

                override fun onFinish() {
                    Debuger.printfLog("download onFinish")
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

                override fun onProgress(progress: Int) {

                }

                override fun onFinish() {
                }

                override fun onSuccess(result: Any?) {
                }

                override fun onFail(error: Exception?) {
                }
            })
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

                override fun onProgress(progress: Int) {

                }

                override fun onFinish() {
                }

                override fun onSuccess(result: Any?) {
                }

                override fun onFail(error: Exception?) {
                }
            })
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
