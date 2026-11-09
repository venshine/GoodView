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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.wx.goodview.GoodView
import java.util.Locale

/*
 * 示例页共用的小工具：取色、单位换算、着色图标、按钮回弹、代码高亮、复制、弹出 GoodView
 */

@ColorInt
fun Context.colorOf(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)

fun Context.dp(value: Float): Int = (value * resources.displayMetrics.density + 0.5f).toInt()

fun Context.font(@FontRes id: Int): Typeface? = ResourcesCompat.getFont(this, id)

/** 着色后的图标，保留固有尺寸（GoodView.setImage 按固有尺寸显示） */
fun Context.tinted(@DrawableRes id: Int, @ColorInt color: Int): Drawable {
    val drawable = DrawableCompat.wrap(ContextCompat.getDrawable(this, id)!!.mutate())
    DrawableCompat.setTint(drawable, color)
    return drawable
}

/** 着色后的线性图标，用作 TextView 的 compound drawable */
fun Context.tintedIcon(@DrawableRes id: Int, @ColorInt color: Int, sizeDp: Float): Drawable {
    val drawable = tinted(id, color)
    val size = dp(sizeDp)
    drawable.setBounds(0, 0, size, size)
    return drawable
}

fun TextView.setStartIcon(icon: Drawable?) {
    setCompoundDrawablesRelative(icon, null, null, null)
}

/** 点按后的回弹，配合飘字让反馈更明显 */
fun View.pulse(scale: Float = 1.18f) {
    animate().cancel()
    scaleX = 1f
    scaleY = 1f
    animate().scaleX(scale).scaleY(scale).setDuration(90).withEndAction {
        animate().scaleX(1f).scaleY(1f).setDuration(220).setInterpolator(OvershootInterpolator(3f)).start()
    }.start()
}

fun Context.copyToClipboard(text: String) {
    getSystemService(ClipboardManager::class.java)?.setPrimaryClip(ClipData.newPlainText("GoodView", text))
}

/** 复制后把按钮文字短暂换成「已复制」 */
fun TextView.flashCopied(@StringRes copied: Int, @StringRes normal: Int) {
    setText(copied)
    removeCallbacks(tag as? Runnable)
    val restore = Runnable { setText(normal) }
    tag = restore
    postDelayed(restore, 1600)
}

/** 颜色转成代码里用的 #RRGGBB */
fun hex(@ColorInt color: Int): String = String.format(Locale.US, "#%06X", color and 0xFFFFFF)

/**
 * 示例里弹出 GoodView 的统一入口
 *
 * 一个页面共用一个实例，而文字、图片、距离、时长都是实例上的状态；每次先 reset()，
 * 避免上一次的图片或实验室里调过的参数带到这一次。
 */
object Pop {

    fun text(goodView: GoodView, anchor: View, text: String, @ColorInt color: Int, sizeDp: Int = 16) {
        goodView.reset()
        goodView.setTextInfo(text, color, sizeDp)
        goodView.show(anchor)
    }

    fun image(goodView: GoodView, anchor: View, image: Drawable) {
        goodView.reset()
        goodView.setImage(image)
        goodView.show(anchor)
    }
}

/**
 * 示例代码的简易高亮：关键字用强调色，引号内字符串和整行注释各用一种颜色
 */
object CodeHighlighter {

    private val STRING = Regex("\"[^\"\\n]*\"|'[^'\\n]*'")

    // 只认整行注释，避免把 'https://…' 里的 // 当成注释
    private val COMMENT = Regex("(?m)^\\s*//.*$")

    fun highlight(context: Context, code: String, keywords: List<String>): CharSequence {
        val builder = SpannableStringBuilder(code)
        val keywordColor = context.colorOf(R.color.accent_ink)
        for (keyword in keywords) {
            // 只匹配完整的单词，避免 setText 命中 setTextInfo 的前半段
            Regex("(?<![A-Za-z])" + Regex.escape(keyword) + "(?![A-Za-z])").findAll(code)
                .forEach { builder.color(keywordColor, it.range.first, it.range.last + 1) }
        }
        STRING.findAll(code).forEach { builder.color(context.colorOf(R.color.code_string), it.range.first, it.range.last + 1) }
        COMMENT.findAll(code).forEach { builder.color(context.colorOf(R.color.code_comment), it.range.first, it.range.last + 1) }
        return builder
    }

    private fun SpannableStringBuilder.color(@ColorInt color: Int, start: Int, end: Int) {
        setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}
