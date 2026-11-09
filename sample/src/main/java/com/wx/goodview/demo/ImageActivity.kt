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
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

/**
 * 图片飘出：着色矢量图走 setImage，Emoji 走 setText；下方代码随最近一次点按更新
 *
 * @author venshine
 */
class ImageActivity : BaseActivity() {

    private class Icon(val label: String, @DrawableRes val res: Int, val resName: String, @ColorRes val color: Int)

    private var code = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        setToolbar(R.string.demo_image, "setImage")
        initIcons()
        initEmoji()
        findViewById<TextView>(R.id.copy_code).setOnClickListener {
            copyToClipboard(code)
            (it as TextView).flashCopied(R.string.copied, R.string.copy_code)
        }
        showIconCode(ICONS[0])
    }

    private fun initIcons() {
        val grid = findViewById<LinearLayout>(R.id.icon_grid)
        ICONS.chunked(COLUMNS).forEach { rowIcons ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            rowIcons.forEachIndexed { i, icon ->
                if (i > 0) row.addView(Space(this), LinearLayout.LayoutParams(dp(10f), 0))
                row.addView(tile(icon), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            }
            grid.addView(row, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12f) })
        }
    }

    private fun tile(icon: Icon): LinearLayout {
        val image = ImageView(this).apply {
            setImageDrawable(tinted(icon.res, colorOf(icon.color)))
            importantForAccessibility = ImageView.IMPORTANT_FOR_ACCESSIBILITY_NO
        }
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundResource(R.drawable.bg_tile)
            setPadding(0, dp(16f), 0, dp(12f))
            isClickable = true
            isFocusable = true
            contentDescription = icon.label
            addView(image, LinearLayout.LayoutParams(dp(32f), dp(32f)))
            addView(TextView(this@ImageActivity).apply {
                text = icon.label
                textSize = 13f
                setTextColor(colorOf(R.color.text))
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8f) })
            setOnClickListener {
                // 每次 show 都传新的着色 Drawable，避免与格子里显示的图标共用状态
                Pop.image(goodView, image, tinted(icon.res, colorOf(icon.color)))
                image.pulse(1.25f)
                showIconCode(icon)
            }
        }
    }

    private fun initEmoji() {
        val row = findViewById<LinearLayout>(R.id.emoji_row)
        for (i in 0 until row.childCount) {
            val chip = row.getChildAt(i) as TextView
            chip.setOnClickListener {
                val emoji = chip.text.toString()
                Pop.text(goodView, chip, emoji, colorOf(R.color.text), 26)
                chip.pulse(1.1f)
                showCode("goodView.setTextInfo(\"$emoji\", Color.BLACK, 26)\ngoodView.show(view)")
            }
        }
    }

    private fun showIconCode(icon: Icon) {
        showCode("""
            |// 矢量图着色后传入，按固有尺寸（32dp）显示
            |val icon = ContextCompat.getDrawable(context, R.drawable.${icon.resName})!!.mutate()
            |icon.setTint(Color.parseColor("${hex(colorOf(icon.color))}"))
            |goodView.setImage(icon)
            |goodView.show(view)
        """.trimMargin())
    }

    private fun showCode(value: String) {
        code = value
        findViewById<TextView>(R.id.code_text).text =
            CodeHighlighter.highlight(this, code, listOf("setImage", "setTextInfo", "show", "setTint"))
    }

    private companion object {
        const val COLUMNS = 3
        val ICONS = listOf(
            Icon("爱心", R.drawable.ic_heart_fill, "ic_heart_fill", R.color.icon_red),
            Icon("星星", R.drawable.ic_star_fill, "ic_star_fill", R.color.icon_amber),
            Icon("点赞", R.drawable.ic_thumb_fill, "ic_thumb_fill", R.color.icon_blue),
            Icon("火焰", R.drawable.ic_flame_fill, "ic_flame_fill", R.color.icon_orange),
            Icon("闪光", R.drawable.ic_sparkle_fill, "ic_sparkle_fill", R.color.icon_violet),
            Icon("书签", R.drawable.ic_bookmark_fill, "ic_bookmark_fill", R.color.icon_mint),
        )
    }
}
