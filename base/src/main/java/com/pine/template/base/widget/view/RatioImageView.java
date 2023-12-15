package com.pine.template.base.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.pine.template.base.R;

public class RatioImageView extends AppCompatImageView {
    //宽高比，由我们自己设定
    private float ratio;

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseRatioImageView);
        //根据属性名称获取对应的值，属性名称的格式为类名_属性名
        ratio = typedArray.getFloat(R.styleable.BaseRatioImageView_base_ratio, 1.0f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取宽度的模式和尺寸
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //获取高度的模式和尺寸
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //宽确定，高不确定
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY && ratio != 0) {
            heightSize = (int) (widthSize * ratio + 0.5f);//根据宽度和比例计算高度
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        } else if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY & ratio != 0) {
            widthSize = (int) (heightSize / ratio + 0.5f);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        } else {
            heightSize = (int) (widthSize * ratio + 0.5f);//根据宽度和比例计算高度
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        //必须调用下面的两个方法之一完成onMeasure方法的重写，否则会报错
//        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置宽高比
     *
     * @param ratio
     */
    public void setRatio(float ratio) {
        this.ratio = ratio;
    }
}
