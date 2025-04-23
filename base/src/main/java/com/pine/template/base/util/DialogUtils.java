package com.pine.template.base.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.pine.template.base.R;
import com.pine.template.base.component.share.bean.ShareBean;
import com.pine.template.base.component.share.manager.ShareManager;
import com.pine.template.base.widget.dialog.DateSelectDialog;
import com.pine.template.base.widget.dialog.DateTimeSelectDialog;
import com.pine.template.base.widget.dialog.InputTextDialog;
import com.pine.template.base.widget.dialog.ProgressDialog;
import com.pine.template.base.widget.dialog.ProvinceSelectDialog;
import com.pine.template.base.widget.dialog.SelectItemDialog;
import com.pine.template.base.widget.dialog.SelectMultiItemsDialog;
import com.pine.template.base.widget.dialog.TimeSelectDialog;
import com.pine.template.base.widget.view.BilingualTextView;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/7.
 */

public class DialogUtils {
    public static boolean heightTooSmall(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        float density = displayMetrics.density;
        float radio = 0.0f;
        boolean outOfScreen = false;
        if (displayMetrics.heightPixels > 0) {
            radio = displayMetrics.widthPixels / (float) displayMetrics.heightPixels;
        }
        if (radio > 2.0f || displayMetrics.heightPixels / density < 480.0f) {
            outOfScreen = true;
        }
        LogUtils.d("DialogUtils", "heightTooSmall radio:" + radio + ", outOfScreen:"
                + outOfScreen + ", displayMetrics:" + displayMetrics);
        return outOfScreen;
    }

    public static void showShortToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ResourceType")
    public static void showShortToast(Context context, int msgId) {
        Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    @SuppressLint({"ResourceType"})
    public static void showLongToast(Context context, int msgId) {
        Toast.makeText(context, msgId, Toast.LENGTH_LONG).show();
    }

    public static Dialog createLoadingDialog(Context context, int msgId, boolean bilingual) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.base_dialog_loading, null);
        LinearLayout layout = v.findViewById(R.id.dialog_loading_view);
        BilingualTextView tip_tv = v.findViewById(R.id.tip_tv);
        tip_tv.enableDual(bilingual);
        tip_tv.setText(msgId);
        Dialog loadingDialog = new Dialog(context, R.style.BaseCustomDialogStyle);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return loadingDialog;
    }

    /**
     * 加载框
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.base_dialog_loading, null);
        LinearLayout layout = v.findViewById(R.id.dialog_loading_view);
        BilingualTextView tip_tv = v.findViewById(R.id.tip_tv);
        tip_tv.setText(msg);
        Dialog loadingDialog = new Dialog(context, R.style.BaseCustomDialogStyle);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return loadingDialog;
    }

    /**
     * 下载进度提示框
     *
     * @param context
     * @return
     */
    public static ProgressDialog createDownloadProgressDialog(Context context,
                                                              int initProgress,
                                                              ProgressDialog.IDialogActionListener listener) {
        final ProgressDialog dialog = new ProgressDialog.Builder(context)
                .create(initProgress, listener);
        return dialog;
    }

    public static Dialog showTipDialog(Context context, String title, String content) {
        return showTipDialog(context, title, content, false);
    }

    public static Dialog showTipDialog(Context context, int titleResId, int contentResId,
                                       boolean fullScreenMode, boolean bilingual) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.base_dialog_tip, null);
        final Dialog dialog = new AlertDialog.Builder(context).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (fullScreenMode) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            dialog.show();
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
            dialog.show();
        }
        dialog.getWindow().setContentView(layout);
        BilingualTextView title_tv = layout.findViewById(R.id.title_tv);
        BilingualTextView content_tv = layout.findViewById(R.id.content_tv);
        BilingualTextView btn_tv = layout.findViewById(R.id.btn_tv);
        title_tv.enableDual(bilingual);
        content_tv.enableDual(bilingual);
        btn_tv.enableDual(bilingual);
        if (titleResId <= 0) {
            title_tv.setVisibility(View.GONE);
        } else {
            title_tv.setText(titleResId);
        }
        content_tv.setText(contentResId);
        btn_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * 提示框
     *
     * @param context
     * @param title
     * @param content
     * @return
     */
    public static Dialog showTipDialog(Context context, String title, String content, boolean fullScreenMode) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.base_dialog_tip, null);
        final Dialog dialog = new AlertDialog.Builder(context).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (fullScreenMode) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            dialog.show();
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
            dialog.show();
        }
        dialog.getWindow().setContentView(layout);
        BilingualTextView title_tv = layout.findViewById(R.id.title_tv);
        BilingualTextView content_tv = layout.findViewById(R.id.content_tv);
        if (TextUtils.isEmpty(title)) {
            title_tv.setVisibility(View.GONE);
        } else {
            title_tv.setText(title);
        }
        content_tv.setText(content);
        BilingualTextView btn_tv = layout.findViewById(R.id.btn_tv);
        btn_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * 确认提示框
     */
    public static Dialog showConfirmDialog(Context context, String content, final IActionListener listener) {
        return showConfirmDialog(context, "", content,
                context.getString(R.string.base_cancel), Color.parseColor("#999999"),
                context.getString(R.string.base_confirm), Color.parseColor("#70B642"),
                listener);
    }

    public static Dialog showConfirmDialog(Context context, String content, boolean fullScreenMode,
                                           final IActionListener listener) {
        return showConfirmDialog(context, "", content,
                context.getString(R.string.base_cancel), Color.parseColor("#999999"),
                context.getString(R.string.base_confirm), Color.parseColor("#70B642"),
                fullScreenMode, listener);
    }

    /**
     * 确认提示框
     */
    public static Dialog showConfirmDialog(Context context, String title, String content,
                                           final IActionListener listener) {
        return showConfirmDialog(context, title, content,
                context.getString(R.string.base_cancel), Color.parseColor("#999999"),
                context.getString(R.string.base_confirm), Color.parseColor("#70B642"),
                listener);
    }

    public static Dialog showConfirmDialog(Context context, String title, String content,
                                           boolean fullScreenMode,
                                           final IActionListener listener) {
        return showConfirmDialog(context, title, content,
                context.getString(R.string.base_cancel), Color.parseColor("#999999"),
                context.getString(R.string.base_confirm), Color.parseColor("#70B642"),
                fullScreenMode, listener);
    }

    /**
     * 提示框
     */
    public static Dialog showConfirmDialog(Context context, String title, String content,
                                           String leftBtnText, String rightBtnText,
                                           final IActionListener listener) {
        return showConfirmDialog(context, title, content,
                leftBtnText, Color.parseColor("#999999"),
                rightBtnText, Color.parseColor("#70B642"),
                listener);
    }

    public static Dialog showConfirmDialog(Context context, String title, String content,
                                           String leftBtnText, String rightBtnText,
                                           boolean fullScreenMode,
                                           final IActionListener listener) {
        return showConfirmDialog(context, title, content,
                leftBtnText, Color.parseColor("#999999"),
                rightBtnText, Color.parseColor("#70B642"),
                fullScreenMode,
                listener);
    }

    public static Dialog showConfirmDialog(Context context, String title, String content,
                                           String leftBtnText, @ColorInt int leftColor,
                                           String rightBtnText, @ColorInt int rightColor,
                                           final IActionListener listener) {
        return showConfirmDialog(context, title, content, leftBtnText, leftColor,
                rightBtnText, rightColor, false, listener);
    }

    /**
     * 提示框
     *
     * @param context
     * @param title
     * @param content
     * @param leftBtnText
     * @param leftColor
     * @param rightBtnText
     * @param rightColor
     * @param fullScreenMode
     * @param listener
     * @return
     */
    public static Dialog showConfirmDialog(Context context, String title, String content,
                                           String leftBtnText, @ColorInt int leftColor,
                                           String rightBtnText, @ColorInt int rightColor,
                                           boolean fullScreenMode,
                                           final IActionListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.base_dialog_confirm, null);
        //对话框
        final Dialog dialog = new AlertDialog.Builder(context).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (fullScreenMode) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            dialog.show();
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
            dialog.show();
        }
        dialog.getWindow().setContentView(layout);
        BilingualTextView title_tv = layout.findViewById(R.id.title_tv);
        BilingualTextView content_tv = layout.findViewById(R.id.content_tv);
        if (TextUtils.isEmpty(title)) {
            title_tv.setVisibility(View.GONE);
        } else {
            title_tv.setText(title);
        }
        content_tv.setText(content);
        BilingualTextView left_btn_tv = layout.findViewById(R.id.left_btn_tv);
        BilingualTextView right_btn_tv = layout.findViewById(R.id.right_btn_tv);
        left_btn_tv.setTextColor(leftColor);
        left_btn_tv.setText(leftBtnText);
        right_btn_tv.setTextColor(rightColor);
        right_btn_tv.setText(rightBtnText);
        left_btn_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null || !listener.onLeftBtnClick(dialog)) {
                    dialog.dismiss();
                }
            }
        });
        right_btn_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null || !listener.onRightBtnClick(dialog)) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static Dialog showConfirmDialog(Context context, int titleResId, int contentResId,
                                           boolean fullScreenMode, boolean bilingual,
                                           final IActionListener listener) {
        return showConfirmDialog(context, titleResId, contentResId,
                R.string.base_cancel, Color.parseColor("#999999"),
                R.string.base_confirm, Color.parseColor("#70B642"),
                fullScreenMode, bilingual, listener);
    }

    public static Dialog showConfirmDialog(Context context, int titleResId, int contentResId,
                                           boolean fullScreenMode, boolean bilingual,
                                           final IActionListener listener, Object... contentFormatArgs) {
        return showConfirmDialog(context, titleResId, contentResId,
                R.string.base_cancel, Color.parseColor("#999999"),
                R.string.base_confirm, Color.parseColor("#70B642"),
                fullScreenMode, bilingual, listener, contentFormatArgs);
    }

    public static Dialog showConfirmDialog(Context context, int titleResId, int contentResId,
                                           int leftBtnTextResId, @ColorInt int leftColorResId,
                                           int rightBtnTextResId, @ColorInt int rightColorResId,
                                           boolean fullScreenMode, boolean bilingual,
                                           final IActionListener listener, Object... contentFormatArgs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.base_dialog_confirm, null);
        //对话框
        final Dialog dialog = new AlertDialog.Builder(context).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (fullScreenMode) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            dialog.show();
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
            dialog.show();
        }
        dialog.getWindow().setContentView(layout);
        BilingualTextView title_tv = layout.findViewById(R.id.title_tv);
        BilingualTextView content_tv = layout.findViewById(R.id.content_tv);
        BilingualTextView left_btn_tv = layout.findViewById(R.id.left_btn_tv);
        BilingualTextView right_btn_tv = layout.findViewById(R.id.right_btn_tv);
        title_tv.enableDual(bilingual);
        content_tv.enableDual(bilingual);
        left_btn_tv.enableDual(bilingual);
        right_btn_tv.enableDual(bilingual);
        if (titleResId <= 0) {
            title_tv.setVisibility(View.GONE);
        } else {
            title_tv.setText(titleResId);
        }
        if (contentFormatArgs != null) {
            content_tv.setText(contentResId, contentFormatArgs);
        } else {
            content_tv.setText(contentResId);
        }
        left_btn_tv.setTextColor(leftColorResId);
        left_btn_tv.setText(leftBtnTextResId);
        right_btn_tv.setTextColor(rightColorResId);
        right_btn_tv.setText(rightBtnTextResId);
        left_btn_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null || !listener.onLeftBtnClick(dialog)) {
                    dialog.dismiss();
                }
            }
        });
        right_btn_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null || !listener.onRightBtnClick(dialog)) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 分享弹出框
     *
     * @param activity
     * @param shareBeanList
     * @return
     */
    public static AlertDialog createShareDialog(final Activity activity, @NonNull final ArrayList<ShareBean> shareBeanList) {
        final View shareContent = LayoutInflater.from(activity).inflate(R.layout.base_dialog_share, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(shareContent);
        final AlertDialog shareDialog = builder.create();
        shareDialog.setCanceledOnTouchOutside(true);
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 0; i < shareBeanList.size(); i++) {
            ShareBean shareBean = shareBeanList.get(i);
            Map<String, Object> map = new HashMap<>();
            map.put("img", shareBean.getIconId());
            map.put("desc", shareBean.getIconName());
            items.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(activity, items, R.layout.base_item_share,
                new String[]{"img", "desc"}, new int[]{R.id.share_img, R.id.share_desc});
        GridView gridView = shareContent.findViewById(R.id.share_grid);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShareBean shareBean = shareBeanList.get(position);
                ShareManager.getInstance().share(activity, shareBean);
                shareDialog.dismiss();
            }
        });
        return shareDialog;
    }

    /**
     * 普通文本输入框
     *
     * @param context
     * @param title
     * @param originalText
     * @param inputMaxLength
     * @param actionClickListener
     * @return
     */
    public static InputTextDialog createTextInputDialog(final Context context, String title,
                                                        String originalText, final int inputMaxLength,
                                                        final InputTextDialog.IActionClickListener actionClickListener) {
        return createTextInputDialog(context, title, originalText, inputMaxLength,
                -1, actionClickListener);
    }

    /**
     * 普通文本输入框
     *
     * @param context
     * @param title
     * @param originalText
     * @param inputMaxLength
     * @param inputType           {@link EditorInfo#inputType}
     * @param actionClickListener
     * @return
     */
    public static InputTextDialog createTextInputDialog(final Context context, String title, String originalText,
                                                        final int inputMaxLength, int inputType,
                                                        final InputTextDialog.IActionClickListener actionClickListener) {
        final InputTextDialog.Builder builder = new InputTextDialog.Builder(context);
        builder.setActionClickListener(actionClickListener);
        final InputTextDialog dialog = builder.create(title, originalText, inputMaxLength, inputType);
        return dialog;
    }

    public static InputTextDialog createTextInputDialog(final Context context, int titleResId, String originalText,
                                                        final int inputMaxLength, int inputType, boolean bilingual,
                                                        final InputTextDialog.IActionClickListener actionClickListener) {
        final InputTextDialog.Builder builder = new InputTextDialog.Builder(context);
        builder.setActionClickListener(actionClickListener);
        final InputTextDialog dialog = builder.create(titleResId, originalText, inputMaxLength, inputType, bilingual);
        return dialog;
    }

    /**
     * 有千分位分隔符数字文本输入框
     *
     * @param context
     * @param title
     * @param originalText
     * @param inputMaxLength
     * @param actionClickListener
     * @return
     */
    public static InputTextDialog createThousandthNumberInputDialog(final Context context,
                                                                    String title, String originalText,
                                                                    final int inputMaxLength,
                                                                    final InputTextDialog.IActionClickListener actionClickListener) {
        return createThousandthNumberInputDialog(context, title, originalText,
                inputMaxLength, false, 0, actionClickListener);
    }

    /**
     * 有千分位分隔符数字文本输入框
     *
     * @param context
     * @param title
     * @param originalText
     * @param inputMaxLength
     * @param allowDecimal
     * @param decimalNum
     * @param actionClickListener
     * @return
     */
    public static InputTextDialog createThousandthNumberInputDialog(final Context context,
                                                                    String title, String originalText,
                                                                    final int inputMaxLength,
                                                                    boolean allowDecimal, int decimalNum,
                                                                    final InputTextDialog.IActionClickListener actionClickListener) {
        final InputTextDialog.Builder builder = new InputTextDialog.Builder(context);
        builder.setActionClickListener(actionClickListener);
        final InputTextDialog dialog = builder.thousandthNumberInputCreate(title, originalText, inputMaxLength, allowDecimal, decimalNum);
        return dialog;
    }

    /**
     * 元素选择弹出框
     *
     * @param context
     * @param title
     * @param itemTextList
     * @param listener
     * @return
     */
    public static SelectItemDialog createItemSelectDialog(final Context context, String title, String[] itemTextList,
                                                          SelectItemDialog.IDialogSelectListener listener) {
        return new SelectItemDialog.Builder(context).create(title, itemTextList, listener);
    }

    /**
     * 元素选择弹出框
     *
     * @param context
     * @param title
     * @param itemTextList
     * @param currentPosition
     * @param listener
     * @return
     */
    public static SelectItemDialog createItemSelectDialog(final Context context, String title, String[] itemTextList,
                                                          int currentPosition, SelectItemDialog.IDialogSelectListener listener) {
        return new SelectItemDialog.Builder(context).create(title, itemTextList, currentPosition, listener);
    }

    /**
     * 元素选择弹出框
     *
     * @param context
     * @param title
     * @param itemImageList
     * @param itemTextList
     * @param listener
     * @return
     */
    public static SelectItemDialog createItemSelectDialog(final Context context, String title, int[] itemImageList, String[] itemTextList,
                                                          SelectItemDialog.IDialogSelectListener listener) {
        return new SelectItemDialog.Builder(context).create(title, itemImageList, itemTextList, listener);
    }

    /**
     * 元素选择弹出框
     *
     * @param context
     * @param title
     * @param itemImageList
     * @param itemTextList
     * @param currentPosition
     * @param listener
     * @return
     */
    public static SelectItemDialog createItemSelectDialog(final Context context, String title, int[] itemImageList, String[] itemTextList,
                                                          int currentPosition, SelectItemDialog.IDialogSelectListener listener) {
        return new SelectItemDialog.Builder(context).create(title, itemImageList, itemTextList, currentPosition, listener);
    }

    /**
     * 元素选择（多选）弹出框
     *
     * @param context
     * @param title
     * @param itemTextList
     * @param selectPosArr
     * @param listener
     * @return
     */
    public static SelectMultiItemsDialog createMultiItemSelectDialog(final Context context, String title, String[] itemTextList,
                                                                     int[] selectPosArr, SelectMultiItemsDialog.IDialogSelectListener listener) {
        return new SelectMultiItemsDialog.Builder(context).create(title, itemTextList, selectPosArr, listener);
    }

    /**
     * 元素选择（多选）弹出框
     *
     * @param context
     * @param title
     * @param itemImageList
     * @param itemTextList
     * @param selectPosArr
     * @param listener
     * @return
     */
    public static SelectMultiItemsDialog createMultiItemSelectDialog(Context context, String title, int[] itemImageList, String[] itemTextList,
                                                                     int[] selectPosArr, SelectMultiItemsDialog.IDialogSelectListener listener) {
        return new SelectMultiItemsDialog.Builder(context).create(title, itemImageList, itemTextList, selectPosArr, listener);
    }

    /**
     * 日期(年月日)选择弹出框
     *
     * @param context
     * @param startYear
     * @param endYear
     * @param dialogSelect
     * @return
     */
    public static DateSelectDialog createDateSelectDialog(final Context context,
                                                          int startYear, int endYear,
                                                          DateSelectDialog.IDialogDateSelected dialogSelect) {
        return new DateSelectDialog.Builder(context).create(dialogSelect, startYear, endYear);
    }

    /**
     * 时间(时分秒)选择弹出框
     *
     * @param context
     * @param dialogSelect
     * @return
     */
    public static TimeSelectDialog createTimeSelectDialog(final Context context,
                                                          TimeSelectDialog.IDialogTimeSelected dialogSelect) {
        return createTimeSelectDialog(context, true, true, true, dialogSelect);
    }

    /**
     * 时间(时分秒)选择弹出框
     *
     * @param context
     * @param showHour
     * @param showMinute
     * @param showSecond
     * @param dialogSelect
     * @return
     */
    public static TimeSelectDialog createTimeSelectDialog(final Context context, boolean showHour,
                                                          boolean showMinute, boolean showSecond,
                                                          TimeSelectDialog.IDialogTimeSelected dialogSelect) {
        return new TimeSelectDialog.Builder(context).create(dialogSelect, showHour, showMinute, showSecond);
    }

    public static DateTimeSelectDialog createDateTimeSelectDialog(final Context context,
                                                                  int startYear, int endYear,
                                                                  DateTimeSelectDialog.IDialogDateSelected dialogSelect) {
        return new DateTimeSelectDialog.Builder(context).create(dialogSelect, startYear, endYear, true);
    }

    /**
     * 省市区选择弹出框
     *
     * @param context
     * @param dialogSelect
     * @return
     */
    public static ProvinceSelectDialog createProvinceSelectDialog(final Context context,
                                                                  ProvinceSelectDialog.IDialogDateSelected dialogSelect) {
        return new ProvinceSelectDialog.Builder(context).create(dialogSelect);
    }

    public interface IActionListener {
        boolean onLeftBtnClick(Dialog dialog);

        boolean onRightBtnClick(Dialog dialog);
    }
}
