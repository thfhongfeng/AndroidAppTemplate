package com.pine.template.base.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pine.template.base.R;
import com.pine.template.base.remote.BaseRouterClient;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class BaseBottomTabNavigationBar extends FrameLayout implements View.OnClickListener {

    private Context mContext;
    private ImageView bottom_main_home_iv, bottom_user_center_iv;
    private LinearLayout bottom_main_home_ll, bottom_user_center_ll;
    private View layout_view;
    private int mCurrentItem = 0;
    private IOnItemClickListener mListener;

    public BaseBottomTabNavigationBar(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public BaseBottomTabNavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.BaseBottomTabNavigationBar);
        mCurrentItem = typedArray.getInt(R.styleable.BaseBottomTabNavigationBar_base_bbtnb_SelectedItem, 0);
        initView();
    }

    private void initView() {
        layout_view = LayoutInflater.from(mContext).inflate(R.layout.base_bottom_tab_navigation_bar,
                this, true);
        setBackgroundColor(Color.TRANSPARENT);
        bottom_main_home_iv = (ImageView) layout_view.findViewById(R.id.bottom_main_home_iv);
        bottom_user_center_iv = (ImageView) layout_view.findViewById(R.id.bottom_user_center_iv);
        bottom_main_home_ll = (LinearLayout) layout_view.findViewById(R.id.bottom_main_home_ll);
        bottom_user_center_ll = (LinearLayout) layout_view.findViewById(R.id.bottom_user_center_ll);

        bottom_main_home_ll.setOnClickListener(this);
        bottom_user_center_ll.setOnClickListener(this);

        switch (mCurrentItem) {
            case 0:
                bottom_main_home_iv.setSelected(true);
                break;
            case 1:
                bottom_user_center_iv.setSelected(true);
                break;
        }
    }

    public void init(IOnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.bottom_main_home_ll) {
            if (mCurrentItem != 0) {
                if (mListener != null) {
                    mListener.onItemClick(view, mCurrentItem, 0);
                } else {
                    BaseRouterClient.goMainHomeActivity(mContext, null, null);
                }
            }
        } else if (id == R.id.bottom_user_center_ll) {
            if (mCurrentItem != 1) {
                if (mListener != null) {
                    mListener.onItemClick(view, mCurrentItem, 1);
                } else {
                    BaseRouterClient.goUserHomeActivity(mContext, null, null);
                }
            }
        }
    }

    public interface IOnItemClickListener {
        void onItemClick(View view, int preItemIndex, int clickItemIndex);
    }
}
