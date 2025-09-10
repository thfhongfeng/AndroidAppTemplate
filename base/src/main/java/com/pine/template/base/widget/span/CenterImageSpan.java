package com.pine.template.base.widget.span;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import com.uuzuche.lib_zxing.DisplayUtil;

/**
 * 让图片与文本垂直居中对齐的自定义ImageSpan
 */
public class CenterImageSpan extends ImageSpan {

    private int boundPx;

    public CenterImageSpan(Context context, int resId) {
        super(context, resId);
    }

    public CenterImageSpan(Context context, int resId, int boundSp) {
        super(context, resId);
        boundPx = DisplayUtil.dip2px(context, boundSp);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x,
                     int top, int y, int bottom, Paint paint) {
        // 获取图片 drawable
        Drawable drawable = getDrawable();
        if (boundPx > 0) {
            drawable.setBounds(0, 0, boundPx, boundPx);
        }
        // 获取文本的字体度量（用于计算居中位置）
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        // 计算图片垂直居中的偏移量（文本中线 - 图片中线）
        int centerY = y + fm.descent - (fm.bottom - fm.top) / 2;
        int transY = centerY - drawable.getBounds().height() / 2;

        // 保存画布状态，绘制图片后恢复
        canvas.save();
        canvas.translate(x, transY); // 调整图片位置到居中
        drawable.draw(canvas);
        canvas.restore();
    }
}
