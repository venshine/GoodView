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
package com.wx.goodview.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout

/**
 * 信息流：多条帖子共用一个 GoodView，点赞 / 收藏可取消，转发累加
 *
 * @author venshine
 */
class FeedActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        setToolbar(R.string.demo_feed, "setTextInfo")

        val list = findViewById<LinearLayout>(R.id.post_list)
        val inflater = LayoutInflater.from(this)
        posts().forEach { post ->
            val view = inflater.inflate(R.layout.item_post, list, false)
            (view.layoutParams as LinearLayout.LayoutParams).topMargin = dp(14f)
            list.addView(view)
            PostBinder.bind(view, post, goodView)
        }
    }

    private companion object {
        fun posts() = listOf(
            Post("林间", "8 分钟前", "周末去山里走了一圈，雾散的那一刻整片林子都亮了。", 342, 58, 21, liked = true),
            Post("Kai", "26 分钟前", "把老项目的 targetSdk 升到 36，全面屏适配比想象中顺利。", 87, 19, 4, tag = "Android"),
            Post("半糖", "1 小时前", "新学的番茄牛腩，炖了两个小时，汤汁拌饭能吃三碗。", 1024, 230, 66, starred = true),
            Post("Mori", "昨天", "读完一本关于城市设计的书：好的街道，是让人愿意多走一段路的街道。", 56, 12, 3),
        )
    }
}
