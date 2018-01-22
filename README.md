
## GSYImageLoader图片加载工具，集成Glide、Picasso、Fresco的kotlin工具类，一键切换图片加载，提供常用的图片相关功能接口

[![](https://jitpack.io/v/CarGuo/GSYImageLoader.svg)](https://jitpack.io/#CarGuo/GSYImageLoader)
[![Build Status](https://travis-ci.org/CarGuo/GSYImageLoader.svg?branch=master)](https://travis-ci.org/CarGuo/GSYImageLoader)
[![GitHub issues](https://img.shields.io/github/issues/CarGuo/GSYImageLoader.svg)](https://github.com/CarGuo/GSYImageLoader/issues)
[![GitHub forks](https://img.shields.io/github/forks/CarGuo/GSYImageLoader.svg)](https://github.com/CarGuo/GSYImageLoader/network)
[![GitHub stars](https://img.shields.io/github/stars/CarGuo/GSYImageLoader.svg)](https://github.com/CarGuo/GSYImageLoader/stargazers)
[![GitHub license](https://img.shields.io/github/license/CarGuo/GSYImageLoader.svg)](https://github.com/CarGuo/GSYImageLoader/blob/master/LICENSE)

状态 | 功能
-------- | ---
**已完成**|**Glide相关**
**已完成**|**Picasso相关**
**已完成**|**Fresco相关**
待完成|**超大图**
待完成|**发布远程依赖**


### 依赖版本

#### 在project下的build.gradle添加
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
#### 在module下的build.gradle添加
```
dependencies {
    implementation 'com.github.CarGuo.GSYImageLoader:GSYGlideLoader:v1.0.0'

    //选择你需要的
    implementation 'com.github.CarGuo.GSYImageLoader:GSYPicassoLoader:v1.0.0'
    implementation 'com.github.CarGuo.GSYImageLoader:GSYFrescoImageLoader:v1.0.0'
    implementation 'com.github.CarGuo.GSYImageLoader:GSYImageLoader:v1.0.0'
}

```

* GSYGlideLoader 当前版本 Glide 4.5.0
* GSYPicassoLoader 当前版本 Picasso 2.5.2
* GSYFrescoLoader 当前版本 Fresco 1.8.0


### 使用方法

#### 1、在Application中初始化

```
 override fun onCreate() {
    GSYImageLoaderManager.initialize(GSYGlideImageLoader(this))
 }
```

#### 2、加载图片

```
GSYImageLoaderManager.sInstance.imageLoader().loadImage(loadOption, holder.imageView, object : IGSYImageLoader.Callback {
       override fun onStart() {

       }

       override fun onSuccess(result: Any?) {
       }

       override fun onFail(error: Exception?) {
       }
   })
```

#### 更多使用请参考DEMO

#### [API接口](https://github.com/CarGuo/GSYImageLoader/blob/master/API.md)

[其他资料-Android图片加载开源库深度推荐](https://www.jianshu.com/p/cd058a924288)