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
package com.wx.goodview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * 点赞效果
 *
 * @author venshine
 */
public class GoodView extends PopupWindow implements IGoodView {

    private String mText = TEXT;

    private int mTextColor = TEXT_COLOR;

    private int mTextSize = TEXT_SIZE;

    private int mFromY = FROM_Y_DELTA;

    private int mToY = TO_Y_DELTA;

    private float mFromAlpha = FROM_ALPHA;

    private float mToAlpha = TO_ALPHA;

    private int mDuration = DURATION;

    private int mDistance = DISTANCE;

    private AnimationSet mAnimationSet;

    private boolean mChanged = false;

    private Context mContext = null;

    private TextView mGood = null;

    public GoodView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        RelativeLayout layout = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        mGood = new TextView(mContext);
        mGood.setIncludeFontPadding(false);
        mGood.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize);
        mGood.setTextColor(mTextColor);
        mGood.setText(mText);
        mGood.setLayoutParams(params);
        layout.addView(mGood);
        setContentView(layout);

        resizeToContent();
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setFocusable(false);
        setTouchable(false);
        setOutsideTouchable(false);

        mAnimationSet = createAnimation();
    }

    /**
     * 设置文本
     *
     * @param text
     */
    public void setText(@NonNull String text) {
        if (TextUtils.isEmpty(text)) {
            throw new IllegalArgumentException("text cannot be null.");
        }
        mText = text;
        mGood.setText(text);
        mGood.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentSize(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int w = (int) mGood.getPaint().measureText(text);
        setWidth(w);
        setHeight(mDistance + getTextViewHeight(mGood, w));
    }

    /**
     * 按内容的自然尺寸设置弹窗大小
     */
    private void resizeToContent() {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mGood.measure(w, h);
        setWidth(mGood.getMeasuredWidth());
        setHeight(mDistance + mGood.getMeasuredHeight());
    }

    /**
     * 设置内容 View 的尺寸，图片模式下固定为图片尺寸，避免空文本的行高把图片拉伸
     */
    private void setContentSize(int width, int height) {
        ViewGroup.LayoutParams params = mGood.getLayoutParams();
        params.width = width;
        params.height = height;
        mGood.setLayoutParams(params);
    }

    private static int getTextViewHeight(TextView textView, int width) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    /**
     * 设置文本颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        mTextColor = color;
        mGood.setTextColor(color);
    }

    /**
     * 设置文本大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        mTextSize = textSize;
        mGood.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
    }

    /**
     * 设置文本信息
     *
     * @param text
     * @param textColor
     * @param textSize
     */
    public void setTextInfo(@NonNull String text, int textColor, int textSize) {
        setTextColor(textColor);
        setTextSize(textSize);
        setText(text);
    }

    /**
     * 设置图片
     *
     * @param resId
     */
    public void setImage(int resId) {
        setImage(mContext.getResources().getDrawable(resId));
    }

    /**
     * 设置图片
     *
     * @param drawable
     */
    public void setImage(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("drawable cannot be null.");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mGood.setBackground(drawable);
        } else {
            mGood.setBackgroundDrawable(drawable);
        }
        mGood.setText("");
        // 没有固有尺寸的 Drawable（如 ColorDrawable）返回 -1，按自适应处理
        int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : ViewGroup.LayoutParams.WRAP_CONTENT;
        setContentSize(width, height);
        setWidth(drawable.getIntrinsicWidth());
        setHeight(mDistance + drawable.getIntrinsicHeight());
    }

    /**
     * 设置移动距离
     *
     * @param dis
     */
    public void setDistance(int dis) {
        mDistance = dis;
        mToY = dis;
        mChanged = true;
        setHeight(mDistance + mGood.getMeasuredHeight());
    }

    /**
     * 设置Y轴移动属性
     *
     * @param fromY
     * @param toY
     */
    public void setTranslateY(int fromY, int toY) {
        mFromY = fromY;
        mToY = toY;
        mChanged = true;
    }

    /**
     * 设置透明度属性
     *
     * @param fromAlpha
     * @param toAlpha
     */
    public void setAlpha(float fromAlpha, float toAlpha) {
        mFromAlpha = fromAlpha;
        mToAlpha = toAlpha;
        mChanged = true;
    }

    /**
     * 设置动画时长
     *
     * @param duration
     */
    public void setDuration(int duration) {
        mDuration = duration;
        mChanged = true;
    }

    /**
     * 重置属性
     */
    public void reset() {
        mText = TEXT;
        mTextColor = TEXT_COLOR;
        mTextSize = TEXT_SIZE;
        mFromY = FROM_Y_DELTA;
        mToY = TO_Y_DELTA;
        mFromAlpha = FROM_ALPHA;
        mToAlpha = TO_ALPHA;
        mDuration = DURATION;
        mDistance = DISTANCE;
        mChanged = false;
        mAnimationSet = createAnimation();

        // 同步界面，否则仍显示重置前的文本、颜色或图片
        mGood.setText(mText);
        mGood.setTextColor(mTextColor);
        mGood.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize);
        mGood.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentSize(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        resizeToContent();
    }

    /**
     * 展示
     *
     * @param v
     */
    public void show(@NonNull View v) {
        if (!isShowing()) {
            int offsetY = -v.getHeight() - getHeight();
            showAsDropDown(v, v.getWidth() / 2 - getWidth() / 2, offsetY);
            if (mAnimationSet == null || mChanged) {
                mAnimationSet = createAnimation();
                mChanged = false;
            }
            mGood.startAnimation(mAnimationSet);
        }
    }

    /**
     * 动画
     *
     * @return
     */
    private AnimationSet createAnimation() {
        mAnimationSet = new AnimationSet(true);
        TranslateAnimation translateAnim = new TranslateAnimation(0, 0, mFromY, -mToY);
        AlphaAnimation alphaAnim = new AlphaAnimation(mFromAlpha, mToAlpha);
        mAnimationSet.addAnimation(translateAnim);
        mAnimationSet.addAnimation(alphaAnim);
        mAnimationSet.setDuration(mDuration);
        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isShowing()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    });
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return mAnimationSet;
    }
}
