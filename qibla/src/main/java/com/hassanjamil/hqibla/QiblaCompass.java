package com.hassanjamil.hqibla;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;

public class QiblaCompass extends FrameLayout {

    private ImageView dial;
    private ImageView qibla;
    private Drawable dialSrc, qiblaSrc;

    public QiblaCompass(Context context) {
        this(context, null);
        init(context, null);
    }

    public QiblaCompass(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

        int dialDim = (int) getResources().getDimension(R.dimen.dial);
        int qiblaDim = (int) getResources().getDimension(R.dimen.qibla);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View parent = inflater.inflate(R.layout.view_qibla_compass, this, true);

        RelativeLayout.LayoutParams lpDial = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpDial.addRule(RelativeLayout.CENTER_IN_PARENT);
        dial = parent.findViewById(R.id.dial);
        dial.setImageDrawable(dialSrc);
        dial.getLayoutParams().height = dialDim;
        dial.getLayoutParams().width = dialDim;
        dial.setLayoutParams(lpDial);

        RelativeLayout.LayoutParams lpQibla = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpQibla.addRule(RelativeLayout.CENTER_IN_PARENT);
        qibla = parent.findViewById(R.id.qibla_indicator);
        qibla.setImageDrawable(qiblaSrc);
        qibla.getLayoutParams().height = qiblaDim;
        qibla.getLayoutParams().width = qiblaDim;
        qibla.setLayoutParams(lpQibla);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.compass, 0, 0);
        try {
            dialSrc = a.getDrawable(R.styleable.compass_dialSrc);
            qiblaSrc = a.getDrawable(R.styleable.compass_qiblaSrc);
        } finally {
            a.recycle();
        }
    }

    public void setDialResource(@DrawableRes int resId) {
        dial.setBackgroundResource(resId);
    }

    public void setQiblaResource(@DrawableRes int resId) {
        qibla.setBackgroundResource(resId);
    }

    public void setDialVisible(boolean visible) {
        dial.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setQiblaVisible(boolean visible) {
        qibla.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}