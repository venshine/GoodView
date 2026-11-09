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

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.util.Locale
import kotlin.math.roundToInt

/**
 * 动画实验室：切换文字 / 图片，调节颜色、字号、距离、时长与透明度，点按爱心播放并生成对应的 Kotlin 代码
 *
 * @author venshine
 */
class LabActivity : BaseActivity() {

    private class Swatch(val name: String, @ColorRes val color: Int)

    private class Picture(val name: String, @DrawableRes val res: Int, val resName: String)

    /** 一个滑块对应 GoodView 的一个参数，按 step 分档 */
    private class Slider(
        @StringRes val label: Int, val api: String,
        val min: Float, val max: Float, val step: Float, val format: (Float) -> String,
    ) {
        lateinit var seekBar: SeekBar
        lateinit var valueText: TextView
        lateinit var header: View

        val steps = ((max - min) / step).roundToInt()
        fun valueOf(progress: Int) = min + progress * step
        fun progressOf(value: Float) = ((value - min) / step).roundToInt()
    }

    private val textSize = Slider(R.string.lab_text_size, "setTextSize", 10f, 32f, 1f) { "${it.toInt()} dp" }
    private val distance = Slider(R.string.lab_distance, "setDistance", 20f, 300f, 10f) { "${it.toInt()} px" }
    private val duration = Slider(R.string.lab_duration, "setDuration", 200f, 2000f, 100f) { "${it.toInt()} ms" }
    private val toAlpha = Slider(R.string.lab_to_alpha, "setAlpha", 0f, 1f, 0.05f) {
        String.format(Locale.US, "%.2f", it)
    }
    private val sliders = listOf(textSize, distance, duration, toAlpha)
    private val values = mutableMapOf<Slider, Float>()

    private var imageMode = false
    private var textIndex = 0
    private var pictureIndex = 0
    private var swatchIndex = 0
    private var code = ""
    private val swatchViews = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab)
        // 顶栏右侧的标签位放「重置」
        setToolbar(R.string.demo_lab, getString(R.string.lab_reset))
        findViewById<TextView>(R.id.toolbar_chip).setOnClickListener { reset() }

        findViewById<TextView>(R.id.code_title).setText(R.string.lab_code)
        findViewById<View>(R.id.play_button).setOnClickListener { play() }
        findViewById<View>(R.id.mode_text).setOnClickListener { setMode(false) }
        findViewById<View>(R.id.mode_image).setOnClickListener { setMode(true) }
        findViewById<TextView>(R.id.copy_code).setOnClickListener {
            copyToClipboard(code)
            (it as TextView).flashCopied(R.string.copied, R.string.copy_code)
        }
        resetValues()
        initSwatches()
        initSliders()
        renderPresets()
        render()
    }

    /** 按当前参数播放一次；先 reset，保证每次只体现面板上的参数 */
    private fun play() {
        val anchor = findViewById<View>(R.id.play_button)
        goodView.reset()
        // 先设距离再设内容：setText / setImage 会按当前距离计算弹窗高度
        goodView.setDistance(value(distance).toInt())
        goodView.setDuration(value(duration).toInt())
        goodView.setAlpha(1f, value(toAlpha))
        if (imageMode) {
            goodView.setImage(tinted(PICTURES[pictureIndex].res, color()))
        } else {
            goodView.setTextInfo(TEXTS[textIndex], color(), value(textSize).toInt())
        }
        goodView.show(anchor)
        anchor.pulse()
    }

    private fun setMode(image: Boolean) {
        if (imageMode == image) return
        imageMode = image
        renderPresets()
        render()
        play()
    }

    private fun reset() {
        imageMode = false
        textIndex = 0
        pictureIndex = 0
        swatchIndex = 0
        resetValues()
        sliders.forEach { it.seekBar.progress = it.progressOf(value(it)) }
        renderPresets()
        render()
    }

    private fun resetValues() {
        values[textSize] = DEFAULT_TEXT_SIZE
        values[distance] = DEFAULT_DISTANCE
        values[duration] = DEFAULT_DURATION
        values[toAlpha] = DEFAULT_TO_ALPHA
    }

    private fun value(slider: Slider) = values.getValue(slider)

    @ColorInt
    private fun color() = colorOf(SWATCHES[swatchIndex].color)

    private fun initSwatches() {
        val row = findViewById<LinearLayout>(R.id.swatch_row)
        SWATCHES.forEachIndexed { i, swatch ->
            val view = View(this).apply {
                contentDescription = swatch.name
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    swatchIndex = i
                    render()
                    play()
                }
            }
            row.addView(view, LinearLayout.LayoutParams(dp(44f), dp(44f)).apply { if (i > 0) marginStart = dp(12f) })
            swatchViews += view
        }
    }

    private fun initSliders() {
        val container = findViewById<LinearLayout>(R.id.slider_container)
        val tint = ColorStateList.valueOf(colorOf(R.color.accent_ink))
        sliders.forEach { slider ->
            slider.header = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                addView(TextView(this@LabActivity).apply {
                    text = getString(slider.label)
                    textSize = 13f
                    setTextColor(colorOf(R.color.text))
                })
                addView(TextView(this@LabActivity).apply {
                    text = slider.api
                    textSize = 11f
                    typeface = font(R.font.jetbrains_mono)
                    setTextColor(colorOf(R.color.text_dim))
                }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = dp(6f) })
                slider.valueText = TextView(this@LabActivity).apply {
                    textSize = 13f
                    typeface = font(R.font.jetbrains_mono)
                    setTextColor(colorOf(R.color.accent_ink))
                }
                addView(slider.valueText)
            }
            container.addView(slider.header, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(18f) })

            slider.seekBar = SeekBar(this).apply {
                max = slider.steps
                progress = slider.progressOf(value(slider))
                progressTintList = tint
                thumbTintList = tint
                contentDescription = getString(slider.label)
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        values[slider] = slider.valueOf(progress)
                        render()
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}

                    // 松手后播放一次，拖动过程中不反复弹出
                    override fun onStopTrackingTouch(seekBar: SeekBar) = play()
                })
            }
            container.addView(slider.seekBar, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                dp(36f)).apply { topMargin = dp(4f) })
        }
    }

    /** 文字模式列出文案，图片模式列出图标；选中态由 render 刷新 */
    private fun renderPresets() {
        val row = findViewById<LinearLayout>(R.id.preset_row)
        row.removeAllViews()
        findViewById<TextView>(R.id.preset_label).setText(
            if (imageMode) R.string.lab_presets_image else R.string.lab_presets_text)
        val count = if (imageMode) PICTURES.size else TEXTS.size
        for (i in 0 until count) {
            val chip: View = if (imageMode) {
                ImageView(this).apply {
                    setImageResource(PICTURES[i].res)
                    setPadding(dp(11f), dp(11f), dp(11f), dp(11f))
                    contentDescription = PICTURES[i].name
                }
            } else {
                TextView(this).apply {
                    text = TEXTS[i]
                    textSize = 14f
                    gravity = Gravity.CENTER
                    minWidth = dp(52f)
                    setPadding(dp(14f), 0, dp(14f), 0)
                    setTextColor(getColorStateList(R.color.toggle_text))
                }
            }
            chip.setBackgroundResource(R.drawable.bg_toggle_chip)
            chip.isClickable = true
            chip.isFocusable = true
            chip.setOnClickListener {
                if (imageMode) pictureIndex = i else textIndex = i
                render()
                play()
            }
            val width = if (imageMode) dp(44f) else ViewGroup.LayoutParams.WRAP_CONTENT
            row.addView(chip, LinearLayout.LayoutParams(width, dp(44f)).apply { if (i > 0) marginStart = dp(8f) })
        }
    }

    private fun render() {
        findViewById<View>(R.id.mode_text).isSelected = !imageMode
        findViewById<View>(R.id.mode_image).isSelected = imageMode
        findViewById<TextView>(R.id.mode_label).text = if (imageMode) "setImage" else "setTextInfo"
        val row = findViewById<LinearLayout>(R.id.preset_row)
        val selected = if (imageMode) pictureIndex else textIndex
        for (i in 0 until row.childCount) {
            val chip = row.getChildAt(i)
            chip.isSelected = i == selected
            (chip as? ImageView)?.imageTintList =
                ColorStateList.valueOf(colorOf(if (i == selected) R.color.accent_ink else R.color.text_sub))
        }
        swatchViews.forEachIndexed { i, view -> view.background = swatchDrawable(colorOf(SWATCHES[i].color), i == swatchIndex) }
        findViewById<ImageView>(R.id.play_button).imageTintList = ColorStateList.valueOf(color())

        sliders.forEach { it.valueText.text = it.format(value(it)) }
        // 图片按固有尺寸显示，字号不起作用
        textSize.header.alpha = if (imageMode) 0.4f else 1f
        textSize.seekBar.isEnabled = !imageMode
        textSize.seekBar.alpha = if (imageMode) 0.4f else 1f

        code = buildCode()
        findViewById<TextView>(R.id.code_text).text = CodeHighlighter.highlight(this, code,
            listOf("GoodView", "setDistance", "setDuration", "setAlpha", "setTextInfo", "setImage", "show"))
    }

    private fun buildCode(): String {
        val colorHex = hex(color())
        val content = if (imageMode) {
            """
            |val icon = ContextCompat.getDrawable(context, R.drawable.${PICTURES[pictureIndex].resName})!!.mutate()
            |icon.setTint(Color.parseColor("$colorHex"))
            |goodView.setImage(icon)
            """.trimMargin()
        } else {
            "goodView.setTextInfo(\"${TEXTS[textIndex]}\", Color.parseColor(\"$colorHex\"), ${value(textSize).toInt()})"
        }
        return """
            |val goodView = GoodView(context)
            |// 先设距离再设内容，弹窗高度按距离计算
            |goodView.setDistance(${value(distance).toInt()})
            |goodView.setDuration(${value(duration).toInt()})
            |goodView.setAlpha(1f, ${String.format(Locale.US, "%.2f", value(toAlpha))}f)
            |$content
            |goodView.show(view)
        """.trimMargin()
    }

    /** 外圈描边表示选中，内圈是颜色本身 */
    private fun swatchDrawable(@ColorInt color: Int, selected: Boolean): Drawable {
        val ring = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setStroke(dp(2f), if (selected) color else Color.TRANSPARENT)
        }
        val dot = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
        return LayerDrawable(arrayOf(ring, dot)).apply {
            val inset = dp(6f)
            setLayerInset(1, inset, inset, inset, inset)
        }
    }

    private companion object {
        const val DEFAULT_TEXT_SIZE = 18f
        const val DEFAULT_DISTANCE = 120f
        const val DEFAULT_DURATION = 800f
        const val DEFAULT_TO_ALPHA = 0f
        val TEXTS = listOf("+1", "赞", "收藏成功", "+10 积分", "❤️")
        val PICTURES = listOf(
            Picture("爱心", R.drawable.ic_heart_fill, "ic_heart_fill"),
            Picture("星星", R.drawable.ic_star_fill, "ic_star_fill"),
            Picture("点赞", R.drawable.ic_thumb_fill, "ic_thumb_fill"),
            Picture("火焰", R.drawable.ic_flame_fill, "ic_flame_fill"),
            Picture("闪光", R.drawable.ic_sparkle_fill, "ic_sparkle_fill"),
        )
        val SWATCHES = listOf(
            Swatch("强调色", R.color.accent_ink), Swatch("金色", R.color.gold),
            Swatch("蓝色", R.color.blue), Swatch("紫色", R.color.violet), Swatch("墨色", R.color.text),
        )
    }
}
