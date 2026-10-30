# GoodView  

[English](README.md) | 简体中文

[![JitPack](https://jitpack.io/v/venshine/GoodView.svg)](https://jitpack.io/#venshine/GoodView)  [![CI](https://github.com/venshine/GoodView/actions/workflows/ci.yml/badge.svg)](https://github.com/venshine/GoodView/actions/workflows/ci.yml)  [![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://developer.android.com/guide/topics/manifest/uses-sdk-element#ApiLevels)  

Android点赞+1效果，支持文本和图像

[**下载示例 APK**](https://github.com/venshine/GoodView/releases/download/2.0.1/GoodView-sample-2.0.1.apk)

ScreenShot
--
![](https://github.com/venshine/GoodView/blob/master/screenshot/screenshot.gif)

Usage
--
##### Gradle:
1. 在 `settings.gradle` 中添加 JitPack 仓库：
```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = 'https://jitpack.io' }
    }
}
```
2. 在模块的 `build.gradle` 中添加依赖：
```groovy
implementation 'com.github.venshine:GoodView:2.0.1'
```

##### Gradle Kotlin DSL:
```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

// build.gradle.kts
implementation("com.github.venshine:GoodView:2.0.1")
```

Demo
--
库本身用 Java 编写，Java 和 Kotlin 都可以直接调用，不会给项目额外引入 Kotlin 依赖。

##### Kotlin:
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var goodView: GoodView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goodView = GoodView(this)
        findViewById<ImageView>(R.id.good).setOnClickListener { v ->
            goodView.setText("+1")
            goodView.show(v)
        }
        findViewById<ImageView>(R.id.collection).setOnClickListener { v ->
            goodView.setTextInfo("收藏成功", Color.parseColor("#f66467"), 12) // 文本、颜色、大小（dp）
            goodView.show(v)
        }
        findViewById<ImageView>(R.id.like).setOnClickListener { v ->
            goodView.setImage(R.mipmap.good_checked) // 图片
            goodView.show(v)
        }
    }
}
```

##### Java:
```Java
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GoodView goodView = new GoodView(this);
        findViewById(R.id.good).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodView.setText("+1");
                goodView.show(v);
            }
        });
    }
}
```

##### Methods:
| method 方法          | description 描述 |
|:---				 |:---|
| void **setText**(String text)  	     | 设置文本（optional） |
| void **setTextInfo**(String text, int textColor, int textSize)	     | 设置文本信息（optional） |
| void **setTextColor**(int color)	     | 设置文本颜色（optional） |
| void **setTextSize**(int textSize)	     | 设置文本大小，单位 dp（optional） |
| void **setImage**(int resId) 	     | 设置图片（optional） |
| void **setImage**(Drawable drawable) 	     | 设置图片（optional） |
| void **setDistance**(int dis) 	     | 设置移动距离 |
| void **setTranslateY**(int fromY, int toY) 	     | 设置Y轴移动属性 |
| void **setAlpha**(float fromAlpha, float toAlpha)  	     | 设置透明度属性 |
| void **setDuration**(int duration)  	     | 设置动画时长 |
| void **reset**() 	     | 重置属性 |
| void **show**(View v) 	     | 展示（required） |

About
--
* Email：venshine.cn@gmail.com

License
--
    Copyright (C) 2016 venshine.cn@gmail.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

