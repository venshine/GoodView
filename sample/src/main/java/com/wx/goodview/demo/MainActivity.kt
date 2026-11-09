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

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

/**
 * 首页：一条可点赞的帖子作为实时预览 + 示例入口 + 接入方式
 *
 * @author venshine
 */
class MainActivity : BaseActivity() {

    /** 示例 App 与库同版本发布，版本号只在 sample/build.gradle 的 versionName 维护一处 */
    private val version: String by lazy {
        packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.source_button).setOnClickListener { openSource() }
        findViewById<TextView>(R.id.version_chip).text = "v$version"
        PostBinder.bind(findViewById(R.id.preview_post), previewPost(), goodView)
        initCards()
        initInstall()
    }

    private fun initCards() {
        findViewById<View>(R.id.card_lab).setOnClickListener { open(LabActivity::class.java) }
        bindCard(R.id.card_feed, R.drawable.ic_feed, R.string.demo_feed, R.string.demo_feed_desc,
            FeedActivity::class.java)
        bindCard(R.id.card_image, R.drawable.ic_image, R.string.demo_image, R.string.demo_image_desc,
            ImageActivity::class.java)
        bindCard(R.id.card_combo, R.drawable.ic_bolt, R.string.demo_combo, R.string.demo_combo_desc,
            ComboActivity::class.java)
        bindCard(R.id.card_checkin, R.drawable.ic_calendar, R.string.demo_checkin, R.string.demo_checkin_desc,
            CheckinActivity::class.java)
    }

    private fun bindCard(@IdRes id: Int, @DrawableRes icon: Int, @StringRes title: Int, @StringRes desc: Int,
                         target: Class<out Activity>) {
        val card = findViewById<View>(id)
        card.findViewById<ImageView>(R.id.card_icon).setImageResource(icon)
        card.findViewById<TextView>(R.id.card_title).setText(title)
        card.findViewById<TextView>(R.id.card_desc).setText(desc)
        card.setOnClickListener { open(target) }
    }

    private fun initInstall() {
        val dependency = "com.github.venshine:GoodView:$version"
        val code = "// settings.gradle\nmaven { url = 'https://jitpack.io' }\n\n// build.gradle\nimplementation '$dependency'"
        findViewById<TextView>(R.id.install_code).text =
            CodeHighlighter.highlight(this, code, listOf("implementation", "maven"))
        findViewById<TextView>(R.id.copy_dependency).apply {
            setStartIcon(tintedIcon(R.drawable.ic_copy, colorOf(R.color.text), 16f))
            setOnClickListener {
                copyToClipboard("implementation '$dependency'")
                flashCopied(R.string.home_copied_dependency, R.string.home_copy_dependency)
            }
        }
    }

    private fun open(target: Class<out Activity>) = startActivity(Intent(this, target))

    private fun openSource() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_URL)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, SOURCE_URL, Toast.LENGTH_LONG).show()
        }
    }

    private companion object {
        const val SOURCE_URL = "https://github.com/venshine/GoodView"

        fun previewPost() = Post(
            author = "venshine",
            time = "刚刚 · GoodView",
            content = "新版示例上线了：点赞、收藏、转发，每个按钮都会在上方飘出一句反馈。试试连点，或者再点一次取消。",
            likes = 128, stars = 36, shares = 12, tag = "Demo",
        )
    }
}
