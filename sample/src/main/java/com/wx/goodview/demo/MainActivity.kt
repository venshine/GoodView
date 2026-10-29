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

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wx.goodview.GoodView

/**
 * Demo
 *
 * @author venshine
 */
class MainActivity : AppCompatActivity() {

    private lateinit var goodView: GoodView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        goodView = GoodView(this)
    }

    fun good(view: View) {
        (view as ImageView).setImageResource(R.mipmap.good_checked)
        goodView.setText("+1")
        goodView.show(view)
    }

    fun good2(view: View) {
        (view as ImageView).setImageResource(R.mipmap.good_checked)
        goodView.setImage(ContextCompat.getDrawable(this, R.mipmap.good_checked)!!)
        goodView.show(view)
    }

    fun collection(view: View) {
        (view as ImageView).setImageResource(R.mipmap.collection_checked)
        goodView.setTextInfo("收藏成功", Color.parseColor("#f66467"), 12)
        goodView.show(view)
    }

    fun bookmark(view: View) {
        (view as ImageView).setImageResource(R.mipmap.bookmark_checked)
        goodView.setTextInfo("收藏成功", Color.parseColor("#ff941A"), 12)
        goodView.show(view)
    }

    fun reset(view: View) {
        findViewById<ImageView>(R.id.good).setImageResource(R.mipmap.good)
        findViewById<ImageView>(R.id.good2).setImageResource(R.mipmap.good)
        findViewById<ImageView>(R.id.collection).setImageResource(R.mipmap.collection)
        findViewById<ImageView>(R.id.bookmark).setImageResource(R.mipmap.bookmark)
        goodView.reset()
    }
}
