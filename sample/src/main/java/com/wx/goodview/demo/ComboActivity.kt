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
import android.view.View
import android.widget.TextView

/**
 * 连击：上一次动画没结束就再次 show()，连击越高字越大、颜色越亮；停手 1 秒结算本轮
 *
 * @author venshine
 */
class ComboActivity : BaseActivity() {

    private var combo = 0
    private var best = 0
    private var total = 0

    private lateinit var comboText: TextView
    private val endCombo = Runnable {
        combo = 0
        render()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combo)
        setToolbar(R.string.demo_combo, "show() × N")
        comboText = findViewById(R.id.combo_count)
        findViewById<View>(R.id.combo_button).setOnClickListener { tap(it) }
        render()
    }

    private fun tap(button: View) {
        combo++
        total++
        best = maxOf(best, combo)
        comboText.removeCallbacks(endCombo)
        comboText.postDelayed(endCombo, COMBO_WINDOW_MS)

        val color = colorOf(when {
            combo >= 10 -> R.color.violet
            combo >= 5 -> R.color.gold
            else -> R.color.accent_ink
        })
        val text = if (combo == 1) "+1" else "+1 ×$combo"
        Pop.text(goodView, button, text, color, (15 + combo).coerceAtMost(MAX_TEXT_SIZE))
        button.pulse(1.08f)
        comboText.pulse(1.12f)
        render()
    }

    private fun render() {
        comboText.text = combo.toString()
        findViewById<TextView>(R.id.best_text).text = best.toString()
        findViewById<TextView>(R.id.total_text).text = total.toString()
    }

    override fun onDestroy() {
        comboText.removeCallbacks(endCombo)
        super.onDestroy()
    }

    private companion object {
        const val COMBO_WINDOW_MS = 1000L
        const val MAX_TEXT_SIZE = 30
    }
}
