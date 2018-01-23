package com.example.gysimageloader

import android.app.Application
import com.shuyu.gsyfrescoimageloader.GSYFrescoImageLoader
import com.shuyu.gsygiideloader.GSYGlideImageLoader
import com.shuyu.gsyimageloader.GSYImageLoaderManager
import com.shuyu.gsyimageloader.GSYImageLoader
import com.shuyu.gsypicassoloader.GSYPicassoImageLoader
import kotlin.properties.Delegates

/**
 * 应用的Application
 * Created by guoshuyu on 2018/1/18.
 */
class GSYApplication : Application() {

    lateinit var mImageList: List<String>

    companion object {
        //委托notNull，这个值在被获取之前没有被分配，它就会抛出一个异常。
        var instance: GSYApplication by Delegates.notNull()
        var sLoader: GSYImageLoader by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        mImageList = listOf("http://img4.imgtn.bdimg.com/it/u=1420363952,1374463682&fm=21&gp=0.jpg",
                "http://c.hiphotos.baidu.com/zhidao/pic/item/77094b36acaf2eddb990270a8f1001e9380193eb.jpg",
                "http://d.hiphotos.baidu.com/zhidao/pic/item/4ec2d5628535e5dd5c955af875c6a7efce1b6258.jpg",
                "http://imgsrc.baidu.com/forum/w%3D580/sign=a418fbfeb8014a90813e46b599763971/a8ec8a13632762d04d0ce0f3a1ec08fa513dc648.jpg",
                "http://img4.imgtn.bdimg.com/it/u=1420363952,1374463682&fm=21&gp=0.jpg",
                "https://user-images.githubusercontent.com/512439/32188373-da40378e-bd64-11e7-88f7-b6c29b81760d.gif",
                "http://imgsrc.baidu.com/forum/pic/item/64380cd7912397dd704437ee5982b2b7d0a2871f.jpg",
                "http://d.hiphotos.baidu.com/zhidao/pic/item/4ec2d5628535e5dd5c955af875c6a7efce1b6258.jpg",
                "http://img.hb.aicdn.com/d2024a8a998c8d3e4ba842e40223c23dfe1026c8bbf3-OudiPA_fw580",
                "http://img4.imgtn.bdimg.com/it/u=1420363952,1374463682&fm=21&gp=0.jpg",
                "http://c.hiphotos.baidu.com/zhidao/pic/item/77094b36acaf2eddb990270a8f1001e9380193eb.jpg",
                "http://d.hiphotos.baidu.com/zhidao/pic/item/4ec2d5628535e5dd5c955af875c6a7efce1b6258.jpg",
                "http://imgsrc.baidu.com/forum/w%3D580/sign=a418fbfeb8014a90813e46b599763971/a8ec8a13632762d04d0ce0f3a1ec08fa513dc648.jpg",
                "http://img4.imgtn.bdimg.com/it/u=1420363952,1374463682&fm=21&gp=0.jpg",
                "http://c.hiphotos.baidu.com/zhidao/pic/item/77094b36acaf2eddb990270a8f1001e9380193eb.jpg",
                "http://imgsrc.baidu.com/forum/pic/item/64380cd7912397dd704437ee5982b2b7d0a2871f.jpg",
                "http://d.hiphotos.baidu.com/zhidao/pic/item/4ec2d5628535e5dd5c955af875c6a7efce1b6258.jpg",
                "http://img.hb.aicdn.com/d2024a8a998c8d3e4ba842e40223c23dfe1026c8bbf3-OudiPA_fw580"
        )
        sLoader = getInitImageLoader()
        GSYImageLoaderManager.initialize(sLoader)

    }

    private fun getInitImageLoader(): GSYImageLoader {
        //return GSYGlideImageLoader(this)
        //return GSYPicassoImageLoader(this)
        return GSYFrescoImageLoader(this)
    }
}