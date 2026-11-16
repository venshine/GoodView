package com.wx.goodview

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * GoodView 回归测试：通过 show() 真实弹出 PopupWindow 后检查布局结果
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class GoodViewTest {

    private lateinit var activity: Activity
    private lateinit var root: FrameLayout
    private lateinit var anchor: View

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        root = FrameLayout(activity)
        anchor = View(activity)
        root.addView(anchor, FrameLayout.LayoutParams(100, 100).apply {
            topMargin = 300
            leftMargin = 100
        })
        activity.setContentView(root)
        idle()
    }

    private fun idle() {
        shadowOf(Looper.getMainLooper()).idle()
    }

    private fun contentText(goodView: GoodView): TextView =
        (goodView.contentView as ViewGroup).getChildAt(0) as TextView

    // 守护用例：issue #4（长文本换行）在 Robolectric mdpi / xxhdpi 下均未复现，未修改宽度计算逻辑，待真机确认
    @Test
    @Config(qualifiers = "xxhdpi")
    fun longTextStaysOnOneLine() {
        val goodView = GoodView(activity)
        for (text in listOf("+1234567", "+9999999", "收藏成功", "点赞 +1", "+1")) {
            goodView.setText(text)
            goodView.show(anchor)
            idle()

            assertEquals(text, 1, contentText(goodView).lineCount)
            goodView.dismiss()
            idle()
        }
    }

    // 先显示文本再显示图片时，空文本仍按字号占一行高度，图片被纵向拉伸（issue #2）
    @Test
    fun imageIsNotStretchedAfterText() {
        val goodView = GoodView(activity)
        goodView.setTextInfo("收藏成功", Color.RED, 30)
        val drawable = ShapeDrawable().apply {
            intrinsicWidth = 40
            intrinsicHeight = 10
        }
        goodView.setImage(drawable)
        goodView.show(anchor)
        idle()

        val content = contentText(goodView)
        assertEquals(40, content.width)
        assertEquals(10, content.height)
    }

    // reset() 只重置了字段，界面上仍显示旧的文本和颜色
    @Test
    fun resetRestoresTextAndColor() {
        val goodView = GoodView(activity)
        goodView.setTextInfo("收藏成功", Color.RED, 30)
        goodView.reset()

        val content = contentText(goodView)
        assertEquals(IGoodView.TEXT, content.text.toString())
        assertEquals(IGoodView.TEXT_COLOR, content.currentTextColor)
    }

    // 守护用例：锚点 View 离开窗口（如页面关闭）后弹窗应随之关闭；Android 7.0+ 的 PopupWindow 已自行处理
    @Test
    fun dismissedWhenAnchorDetached() {
        val goodView = GoodView(activity)
        goodView.setText("+1")
        // 动画时长较长，避免动画结束后弹窗自行关闭，影响本用例判断
        goodView.setDuration(60_000)
        goodView.show(anchor)
        assertTrue(goodView.isShowing)

        root.removeView(anchor)
        idle()

        assertFalse(goodView.isShowing)
    }

    // 上一个弹窗动画未结束时在另一个按钮上展示新文本：旧实现直接忽略这次 show，弹窗停在旧按钮上，
    // 窗口也沿用旧文本的宽度，新文本被挤成多行（issue #4，真机复现）。
    // Robolectric 中 View 动画会在 idle 时立即结束并关闭弹窗，所以在第二次 show 后直接检查窗口参数
    @Test
    fun showWhileShowingMovesToNewAnchorAndResizes() {
        val anchor2 = View(activity)
        root.addView(anchor2, FrameLayout.LayoutParams(100, 100).apply {
            topMargin = 500
            leftMargin = 200
        })
        idle()

        val goodView = GoodView(activity)
        goodView.setText("+1")
        goodView.show(anchor)
        goodView.setTextInfo("收藏成功", Color.RED, 12)
        goodView.show(anchor2)

        assertTrue(goodView.isShowing)
        val lp = goodView.contentView.rootView.layoutParams as WindowManager.LayoutParams
        assertEquals(goodView.width, lp.width)
        val location = IntArray(2)
        anchor2.getLocationInWindow(location)
        assertEquals(location[0] + anchor2.width / 2 - goodView.width / 2, lp.x)
    }
}
