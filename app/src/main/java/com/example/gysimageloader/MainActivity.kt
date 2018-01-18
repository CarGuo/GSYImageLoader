package com.example.gysimageloader

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import com.shuyu.gsyimageloader.GSYImageLoaderManager
import com.shuyu.gsyimageloader.IGSYImageLoader
import com.shuyu.gsyimageloader.LoadOption
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val adapter = ImageAdapter(this, GSYApplication.instance.mImageList);
        imageList.adapter = adapter
        imageList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

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
            val loadOption: LoadOption = LoadOption()
                    .setDefaultImg(R.mipmap.ic_launcher)
                    .setErrorImg(R.mipmap.ic_launcher)
                    .setUri(dataList[p0])
            GSYImageLoaderManager.sInstance.imageLoader().loadImage(context, loadOption, holder.imageView, object : IGSYImageLoader.Callback {
                override fun onStart() {

                }

                override fun onProgress(progress: Int) {

                }

                override fun onFinish() {
                }

                override fun onSuccess() {
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
}
