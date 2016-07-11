# GoodView  
[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)  

Android点赞+1效果，支持文本和图像

ScreenShot
--
![](https://github.com/venshine/GoodView/blob/master/screenshot/screenshot.gif)

Usage
--
##### Gradle:
```groovy
compile 'com.wx.goodview:goodview:1.0.0'
```

Demo
--

##### Java:
```Java
    public class MainActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main)

            final GoodView goodView = new GoodView(this);
            Button button = new Button(this);
            button.setOnClickListener(new View.OnClickListener() {
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

