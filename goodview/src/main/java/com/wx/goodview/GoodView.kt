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

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import com.wx.goodview.IGoodView.Companion.DISTANCE
import com.wx.goodview.IGoodView.Companion.DURATION
import com.wx.goodview.IGoodView.Companion.FROM_ALPHA
import com.wx.goodview.IGoodView.Companion.FROM_Y_DELTA
import com.wx.goodview.IGoodView.Companion.TEXT
import com.wx.goodview.IGoodView.Companion.TEXT_COLOR
import com.wx.goodview.IGoodView.Companion.TEXT_SIZE
import com.wx.goodview.IGoodView.Companion.TO_ALPHA
import com.wx.goodview.IGoodView.Companion.TO_Y_DELTA

/**
 * 点赞效果
 *
 * 类和公开方法保持 open，与 Java 版一致，不破坏已有的子类
 *
 * @author venshine
 */
open class GoodView(private val context: Context) : PopupWindow(context), IGoodView {

    private var fromY = FROM_Y_DELTA

    private var toY = TO_Y_DELTA

    private var fromAlpha = FROM_ALPHA

    private var toAlpha = TO_ALPHA

    private var duration = DURATION

    private var distance = DISTANCE

    private var changed = false

    private val good = TextView(context).apply {
        includeFontPadding = false
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE.toFloat())
        setTextColor(TEXT_COLOR)
        text = TEXT
        layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.CENTER_HORIZONTAL)
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        }
    }

    private var animationSet: AnimationSet

    init {
        contentView = RelativeLayout(context).apply { addView(good) }
        resizeToContent()
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isFocusable = false
        isTouchable = false
        isOutsideTouchable = false
        animationSet = createAnimation()
    }

    /**
     * 设置文本
     */
    open fun setText(text: String) {
        require(text.isNotEmpty()) { "text cannot be null." }
        good.text = text
        setTextBackground(ColorDrawable(Color.TRANSPARENT))
        setContentSize(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val w = good.paint.measureText(text).toInt()
        width = w
        height = distance + getTextViewHeight(good, w)
    }

    /**
     * 设置文本颜色
     */
    open fun setTextColor(color: Int) {
        good.setTextColor(color)
    }

    /**
     * 设置文本大小，单位 dp
     */
    open fun setTextSize(textSize: Int) {
        good.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
    }

    /**
     * 设置文本信息
     */
    open fun setTextInfo(text: String, textColor: Int, textSize: Int) {
        setTextColor(textColor)
        setTextSize(textSize)
        setText(text)
    }

    /**
     * 设置图片
     */
    open fun setImage(resId: Int) {
        // 库不依赖 androidx.core，minSdk 14 下只能用这个已废弃的重载
        @Suppress("DEPRECATION")
        setImage(context.resources.getDrawable(resId))
    }

    /**
     * 设置图片
     */
    open fun setImage(drawable: Drawable) {
        setTextBackground(drawable)
        good.text = ""
        // 没有固有尺寸的 Drawable（如 ColorDrawable）返回 -1，按自适应处理
        val w = drawable.intrinsicWidth.takeIf { it > 0 } ?: ViewGroup.LayoutParams.WRAP_CONTENT
        val h = drawable.intrinsicHeight.takeIf { it > 0 } ?: ViewGroup.LayoutParams.WRAP_CONTENT
        setContentSize(w, h)
        width = drawable.intrinsicWidth
        height = distance + drawable.intrinsicHeight
    }

    /**
     * 设置移动距离
     */
    open fun setDistance(dis: Int) {
        distance = dis
        toY = dis
        changed = true
        height = distance + good.measuredHeight
    }

    /**
     * 设置Y轴移动属性
     */
    open fun setTranslateY(fromY: Int, toY: Int) {
        this.fromY = fromY
        this.toY = toY
        changed = true
    }

    /**
     * 设置透明度属性
     */
    open fun setAlpha(fromAlpha: Float, toAlpha: Float) {
        this.fromAlpha = fromAlpha
        this.toAlpha = toAlpha
        changed = true
    }

    /**
     * 设置动画时长
     */
    open fun setDuration(duration: Int) {
        this.duration = duration
        changed = true
    }

    /**
     * 重置属性
     */
    open fun reset() {
        fromY = FROM_Y_DELTA
        toY = TO_Y_DELTA
        fromAlpha = FROM_ALPHA
        toAlpha = TO_ALPHA
        duration = DURATION
        distance = DISTANCE
        changed = false
        animationSet = createAnimation()

        // 同步界面，否则仍显示重置前的文本、颜色或图片
        good.text = TEXT
        good.setTextColor(TEXT_COLOR)
        good.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE.toFloat())
        setTextBackground(ColorDrawable(Color.TRANSPARENT))
        setContentSize(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        resizeToContent()
    }

    /**
     * 在 v 上方展示
     */
    open fun show(v: View) {
        val offsetX = v.width / 2 - width / 2
        val offsetY = -v.height - height
        if (isShowing) {
            // 上一次动画未结束时再次展示：移到新的锚点并按新内容调整窗口大小，
            // 否则弹窗停在旧位置，且沿用旧文本的宽度导致新文本换行（issue #4）
            update(v, offsetX, offsetY, width, height)
        } else {
            showAsDropDown(v, offsetX, offsetY)
        }
        if (changed) {
            animationSet = createAnimation()
            changed = false
        }
        good.startAnimation(animationSet)
    }

    /**
     * 按内容的自然尺寸设置弹窗大小
     */
    private fun resizeToContent() {
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        good.measure(spec, spec)
        width = good.measuredWidth
        height = distance + good.measuredHeight
    }

    /**
     * 设置内容 View 的尺寸，图片模式下固定为图片尺寸，避免空文本的行高把图片拉伸
     */
    private fun setContentSize(width: Int, height: Int) {
        good.layoutParams = good.layoutParams.apply {
            this.width = width
            this.height = height
        }
    }

    private fun setTextBackground(drawable: Drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            good.background = drawable
        } else {
            @Suppress("DEPRECATION")
            good.setBackgroundDrawable(drawable)
        }
    }

    private fun createAnimation(): AnimationSet {
        val set = AnimationSet(true)
        set.addAnimation(TranslateAnimation(0f, 0f, fromY.toFloat(), -toY.toFloat()))
        set.addAnimation(AlphaAnimation(fromAlpha, toAlpha))
        set.duration = duration.toLong()
        set.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) = Unit

            override fun onAnimationEnd(animation: Animation) {
                if (isShowing) {
                    Handler(Looper.getMainLooper()).post { dismiss() }
                }
            }

            override fun onAnimationRepeat(animation: Animation) = Unit
        })
        return set
    }

    private fun getTextViewHeight(textView: TextView, width: Int): Int {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        textView.measure(widthSpec, heightSpec)
        return textView.measuredHeight
    }
}
