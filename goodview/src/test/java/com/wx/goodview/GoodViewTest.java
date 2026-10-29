package com.wx.goodview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

/**
 * GoodView 回归测试：通过 show() 真实弹出 PopupWindow 后检查布局结果
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class GoodViewTest {

    private Activity activity;
    private FrameLayout root;
    private View anchor;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(Activity.class).setup().get();
        root = new FrameLayout(activity);
        anchor = new View(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);
        params.topMargin = 300;
        params.leftMargin = 100;
        root.addView(anchor, params);
        activity.setContentView(root);
        idle();
    }

    private void idle() {
        shadowOf(Looper.getMainLooper()).idle();
    }

    private TextView contentText(GoodView goodView) {
        return (TextView) ((ViewGroup) goodView.getContentView()).getChildAt(0);
    }

    // 守护用例：issue #4（长文本换行）在 Robolectric mdpi / xxhdpi 下均未复现，未修改宽度计算逻辑，待真机确认
    @Test
    @Config(qualifiers = "xxhdpi")
    public void longTextStaysOnOneLine() {
        GoodView goodView = new GoodView(activity);
        for (String text : new String[]{"+1234567", "+9999999", "收藏成功", "点赞 +1", "+1"}) {
            goodView.setText(text);
            goodView.show(anchor);
            idle();

            assertEquals(text, 1, contentText(goodView).getLineCount());
            goodView.dismiss();
            idle();
        }
    }

    // 先显示文本再显示图片时，空文本仍按字号占一行高度，图片被纵向拉伸（issue #2）
    @Test
    public void imageIsNotStretchedAfterText() {
        GoodView goodView = new GoodView(activity);
        goodView.setTextInfo("收藏成功", Color.RED, 30);
        ShapeDrawable drawable = new ShapeDrawable();
        drawable.setIntrinsicWidth(40);
        drawable.setIntrinsicHeight(10);
        goodView.setImage(drawable);
        goodView.show(anchor);
        idle();

        TextView content = contentText(goodView);
        assertEquals(40, content.getWidth());
        assertEquals(10, content.getHeight());
    }

    // reset() 只重置了字段，界面上仍显示旧的文本和颜色
    @Test
    public void resetRestoresTextAndColor() {
        GoodView goodView = new GoodView(activity);
        goodView.setTextInfo("收藏成功", Color.RED, 30);
        goodView.reset();

        TextView content = contentText(goodView);
        assertEquals(IGoodView.TEXT, content.getText().toString());
        assertEquals(IGoodView.TEXT_COLOR, content.getCurrentTextColor());
    }

    // 守护用例：锚点 View 离开窗口（如页面关闭）后弹窗应随之关闭；Android 7.0+ 的 PopupWindow 已自行处理
    @Test
    public void dismissedWhenAnchorDetached() {
        GoodView goodView = new GoodView(activity);
        goodView.setText("+1");
        // 动画时长较长，避免动画结束后弹窗自行关闭，影响本用例判断
        goodView.setDuration(60_000);
        goodView.show(anchor);
        assertTrue(goodView.isShowing());

        root.removeView(anchor);
        idle();

        assertFalse(goodView.isShowing());
    }
}
