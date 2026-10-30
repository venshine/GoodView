# GoodView  

English | [简体中文](README-CN.md)

[![JitPack](https://jitpack.io/v/venshine/GoodView.svg)](https://jitpack.io/#venshine/GoodView)  [![CI](https://github.com/venshine/GoodView/actions/workflows/ci.yml/badge.svg)](https://github.com/venshine/GoodView/actions/workflows/ci.yml)  [![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)  

A "+1" like animation for Android, supporting both text and images.

ScreenShot
--
![](https://github.com/venshine/GoodView/blob/master/screenshot/screenshot.gif)

Usage
--
##### Gradle:
1. Add the JitPack repository to `settings.gradle`:
```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = 'https://jitpack.io' }
    }
}
```
2. Add the dependency to your module's `build.gradle`:
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
The library is written in Java and can be called directly from both Java and Kotlin. It adds no Kotlin dependency to your project.

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
            goodView.setTextInfo("Saved", Color.parseColor("#f66467"), 12) // text, color, size (dp)
            goodView.show(v)
        }
        findViewById<ImageView>(R.id.like).setOnClickListener { v ->
            goodView.setImage(R.mipmap.good_checked) // image
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
| method          | description |
|:---				 |:---|
| void **setText**(String text)  	     | Set the text (optional) |
| void **setTextInfo**(String text, int textColor, int textSize)	     | Set the text, color and size (optional) |
| void **setTextColor**(int color)	     | Set the text color (optional) |
| void **setTextSize**(int textSize)	     | Set the text size in dp (optional) |
| void **setImage**(int resId) 	     | Set the image (optional) |
| void **setImage**(Drawable drawable) 	     | Set the image (optional) |
| void **setDistance**(int dis) 	     | Set the moving distance |
| void **setTranslateY**(int fromY, int toY) 	     | Set the Y-axis translation |
| void **setAlpha**(float fromAlpha, float toAlpha)  	     | Set the alpha animation |
| void **setDuration**(int duration)  	     | Set the animation duration |
| void **reset**() 	     | Reset all properties |
| void **show**(View v) 	     | Show the animation above the view (required) |

About
--
* Email: venshine.cn@gmail.com

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

