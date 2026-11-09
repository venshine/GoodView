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

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Calendar
import java.util.Locale

/**
 * 签到积分：奖励提示调大移动距离和时长，飘得更高、停得更久
 *
 * @author venshine
 */
class CheckinActivity : BaseActivity() {

    /** 今天是本周第几天，周一为 0 */
    private val today = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
    private val reward get() = REWARDS[today]

    private var points = START_POINTS
    private var checked = false
    private val cells = mutableListOf<LinearLayout>()
    private var counter: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)
        setToolbar(R.string.demo_checkin, "setDistance")
        initDays()
        findViewById<View>(R.id.checkin_button).setOnClickListener { checkIn() }
        findViewById<View>(R.id.reset_button).setOnClickListener { reset() }
        initCode()
        render()
    }

    private fun initDays() {
        val row = findViewById<LinearLayout>(R.id.day_row)
        DAYS.forEachIndexed { i, day ->
            val cell = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.bg_day)
                setPadding(0, dp(10f), 0, dp(10f))
                addView(TextView(this@CheckinActivity).apply {
                    text = day
                    textSize = 12f
                    setTextColor(colorOf(R.color.text_muted))
                })
                addView(TextView(this@CheckinActivity).apply {
                    text = "+${REWARDS[i]}"
                    textSize = 14f
                    typeface = font(R.font.space_grotesk)
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4f) })
            }
            row.addView(cell, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                if (i > 0) marginStart = dp(6f)
            })
            cells += cell
        }
    }

    private fun checkIn() {
        if (checked) return
        checked = true
        goodView.reset()
        goodView.setDistance(DISTANCE)
        goodView.setDuration(DURATION)
        goodView.setTextInfo(getString(R.string.checkin_pop_format, reward), colorOf(R.color.gold), 18)
        goodView.show(cells[today])
        cells[today].pulse(1.12f)
        countTo(points + reward)
        render()
    }

    private fun reset() {
        counter?.cancel()
        checked = false
        points = START_POINTS
        render()
    }

    /** 积分数字滚动到新值 */
    private fun countTo(target: Int) {
        counter?.cancel()
        counter = ValueAnimator.ofInt(points, target).apply {
            duration = 700
            addUpdateListener { pointsText().text = format(it.animatedValue as Int) }
            start()
        }
        points = target
    }

    private fun render() {
        if (counter?.isRunning != true) pointsText().text = format(points)
        // 今天之前默认已签到；连续天数含今天时加一
        findViewById<TextView>(R.id.streak_text).text =
            getString(R.string.checkin_streak_format, today + if (checked) 1 else 0)
        cells.forEachIndexed { i, cell ->
            val done = i < today || (i == today && checked)
            cell.isSelected = done
            cell.isActivated = i == today && !checked
            (cell.getChildAt(1) as TextView).setTextColor(colorOf(if (done || i == today) R.color.gold else R.color.text_dim))
        }
        findViewById<TextView>(R.id.checkin_button).apply {
            isEnabled = !checked
            text = if (checked) getString(R.string.checkin_done) else getString(R.string.checkin_button_format, reward)
            setBackgroundResource(if (checked) R.drawable.bg_button_disabled else R.drawable.bg_button_accent)
            setTextColor(colorOf(if (checked) R.color.text_dim else R.color.on_accent))
        }
    }

    private fun initCode() {
        val code = """
            |goodView.setDistance($DISTANCE)   // 默认 60，单位 px
            |goodView.setDuration($DURATION)  // 默认 800 ms
            |goodView.setTextInfo("${getString(R.string.checkin_pop_format, reward)}", Color.parseColor("${hex(colorOf(R.color.gold))}"), 18)
            |goodView.show(todayCell)
        """.trimMargin()
        findViewById<TextView>(R.id.code_text).text =
            CodeHighlighter.highlight(this, code, listOf("setDistance", "setDuration", "setTextInfo", "show"))
        findViewById<TextView>(R.id.copy_code).setOnClickListener {
            copyToClipboard(code)
            (it as TextView).flashCopied(R.string.copied, R.string.copy_code)
        }
    }

    private fun pointsText() = findViewById<TextView>(R.id.points_text)

    private fun format(value: Int) = String.format(Locale.US, "%,d", value)

    override fun onDestroy() {
        counter?.cancel()
        super.onDestroy()
    }

    private companion object {
        const val START_POINTS = 1280
        const val DISTANCE = 240
        const val DURATION = 1400
        val DAYS = listOf("一", "二", "三", "四", "五", "六", "日")
        val REWARDS = listOf(5, 5, 10, 10, 15, 20, 50)
    }
}
