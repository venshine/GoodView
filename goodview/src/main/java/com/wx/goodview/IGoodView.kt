/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wx.goodview

import android.graphics.Color

/**
 * 默认参数。const val 会编译为接口上的静态常量，Java 侧仍可用 IGoodView.TEXT 访问
 *
 * @author venshine
 */
interface IGoodView {

    companion object {
        const val DISTANCE = 60 // 默认移动距离

        const val FROM_Y_DELTA = 0 // Y轴移动起始偏移量

        const val TO_Y_DELTA = DISTANCE // Y轴移动最终偏移量

        const val FROM_ALPHA = 1.0f // 起始时透明度

        const val TO_ALPHA = 0.0f // 结束时透明度

        const val DURATION = 800 // 动画时长

        const val TEXT = "" // 默认文本

        const val TEXT_SIZE = 16 // 默认文本字体大小

        const val TEXT_COLOR = Color.BLACK // 默认文本字体颜色
    }
}
