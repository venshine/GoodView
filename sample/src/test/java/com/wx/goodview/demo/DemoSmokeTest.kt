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
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.RuntimeEnvironment
import java.time.Duration
import java.util.Calendar
import java.util.Locale

/**
 * 示例页冒烟测试：每个页面在浅色、深色下都能启动，关键交互真实弹出 GoodView 且内容符合预期
 *
 * @author venshine
 */
@RunWith(RobolectricTestRunner::class)
class DemoSmokeTest {

    @After
    fun tearDown() {
        // 让进行中的 GoodView 动画走完并自动 dismiss，避免弹窗窗口留到下一个用例
        idle(3)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun idle(seconds: Long = 0) {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(seconds))
    }

    private fun <T : Activity> launch(type: Class<T>): T {
        val activity = Robolectric.buildActivity(type).setup().get()
        idle()
        return activity
    }

    /** 只点击不推进时间：GoodView 动画结束后会自动 dismiss，断言要在动画结束前做 */
    private fun click(view: View) {
        view.performClick()
    }

    /** 最近一次弹出的 GoodView */
    private fun popup(): PopupWindow {
        val popup = shadowOf(RuntimeEnvironment.getApplication()).latestPopupWindow
        assertNotNull("应弹出 GoodView", popup)
        assertTrue("GoodView 应处于显示状态", popup!!.isShowing)
        return popup
    }

    private fun popupText(): String = ((popup().contentView as ViewGroup).getChildAt(0) as TextView).text.toString()

    private val screens = listOf(
        MainActivity::class.java, LabActivity::class.java, FeedActivity::class.java,
        ImageActivity::class.java, ComboActivity::class.java, CheckinActivity::class.java,
    )

    @Test
    fun allScreensLaunchInLightMode() {
        screens.forEach { type ->
            val activity = launch(type)
            assertTrue("${type.simpleName} 应为浅色", !DemoTheme.isNight(activity))
        }
    }

    @Test
    fun allScreensLaunchInDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        screens.forEach { type ->
            val activity = launch(type)
            assertTrue("${type.simpleName} 应为深色", DemoTheme.isNight(activity))
        }
    }

    @Test
    fun themeToggleSwitchesToDark() {
        val activity = launch(MainActivity::class.java)
        click(activity.findViewById(R.id.theme_toggle))
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.getDefaultNightMode())
    }

    @Test
    fun homeLikeTogglesCountAndPops() {
        val activity = launch(MainActivity::class.java)
        val like = activity.findViewById<View>(R.id.preview_post).findViewById<TextView>(R.id.action_like)
        assertEquals("128", like.text.toString())

        click(like)
        assertEquals("129", like.text.toString())
        assertEquals("+1", popupText())

        click(like)
        assertEquals("128", like.text.toString())
        assertEquals("-1", popupText())
    }

    @Test
    fun feedStarPopsText() {
        val activity = launch(FeedActivity::class.java)
        val first = activity.findViewById<ViewGroup>(R.id.post_list).getChildAt(0)
        click(first.findViewById(R.id.action_star))
        assertEquals("收藏成功", popupText())
    }

    @Test
    fun labSwitchesToImageAndGeneratesCode() {
        val activity = launch(LabActivity::class.java)
        val code = activity.findViewById<TextView>(R.id.code_text)
        assertTrue(code.text.contains("setTextInfo(\"+1\""))

        click(activity.findViewById(R.id.mode_image))
        assertTrue(code.text.contains("setImage(icon)"))
        // 切换后自动播放一次，图片模式下文字为空、图片作为背景
        assertEquals("", popupText())
        assertNotNull(((popup().contentView as ViewGroup).getChildAt(0)).background)

        click(activity.findViewById(R.id.toolbar_chip))
        assertTrue(code.text.contains("setTextInfo(\"+1\""))
    }

    @Test
    fun imageTilePopsImage() {
        val activity = launch(ImageActivity::class.java)
        val firstRow = activity.findViewById<ViewGroup>(R.id.icon_grid).getChildAt(0) as ViewGroup
        click(firstRow.getChildAt(0))
        assertEquals("", popupText())
        assertTrue(activity.findViewById<TextView>(R.id.code_text).text.contains("ic_heart_fill"))
    }

    @Test
    fun comboCountsAndSettles() {
        val activity = launch(ComboActivity::class.java)
        val button = activity.findViewById<View>(R.id.combo_button)
        repeat(3) { click(button) }
        assertEquals("3", activity.findViewById<TextView>(R.id.combo_count).text.toString())
        assertEquals("+1 ×3", popupText())

        idle(2)
        assertEquals("0", activity.findViewById<TextView>(R.id.combo_count).text.toString())
        assertEquals("3", activity.findViewById<TextView>(R.id.best_text).text.toString())
        assertEquals("3", activity.findViewById<TextView>(R.id.total_text).text.toString())
    }

    @Test
    fun checkinAddsTodayReward() {
        val activity = launch(CheckinActivity::class.java)
        val reward = listOf(5, 5, 10, 10, 15, 20, 50)[(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7]
        val button = activity.findViewById<TextView>(R.id.checkin_button)

        click(button)
        assertEquals("+$reward 积分", popupText())
        assertEquals("今日已签到", button.text.toString())
        idle(1)
        assertEquals(String.format(Locale.US, "%,d", 1280 + reward),
            activity.findViewById<TextView>(R.id.points_text).text.toString())

        click(activity.findViewById(R.id.reset_button))
        assertEquals("1,280", activity.findViewById<TextView>(R.id.points_text).text.toString())
    }
}
