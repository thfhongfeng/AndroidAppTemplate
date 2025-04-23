package com.pine.template.base.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.aigestudio.wheelpicker.WheelPicker;
import com.pine.template.base.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/2/27.
 */

public class DateTimeSelectDialog extends BaseDialog {
    private Builder builder;

    private DateTimeSelectDialog(@NonNull Context context) {
        super(context);
    }

    private DateTimeSelectDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public Builder getBuilder() {
        return builder;
    }

    public interface IDialogDateSelected {
        void onSelected(Calendar calendar);
    }

    public static class Builder {

        private int themeResId = R.style.BaseDialogStyle;
        TextView cancelBtn, clearBtn, confirmBtn;
        WheelPicker wheelYear, wheelMonth, wheelDay;
        WheelPicker wheelHour, wheelMinute, wheelSecond;
        private Context context;
        private Calendar selectedDate;
        private Calendar startDate, endDate;
        private int startYear, endYear, startMonth, endMonth;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder(Context context, int themeResId) {
            this.context = context;
            this.themeResId = themeResId;
        }

        public DateTimeSelectDialog create(IDialogDateSelected dialogSelect) {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            Calendar startDate = Calendar.getInstance();
            startDate.set(year, 0, 1);
            Calendar endDate = Calendar.getInstance();
            endDate.set(year + 1, 11, 31);
            return this.create(dialogSelect, startDate, endDate);
        }

        public DateTimeSelectDialog create(IDialogDateSelected dialogSelect, int startYear, int endYear) {
            Calendar startDate = Calendar.getInstance();
            startDate.set(startYear, 0, 1);
            Calendar endDate = Calendar.getInstance();
            endDate.set(endYear, 11, 31);
            return this.create(dialogSelect, startDate, endDate);
        }

        public DateTimeSelectDialog create(IDialogDateSelected dialogSelect, int startYear, int endYear, boolean showSecond) {
            Calendar startDate = Calendar.getInstance();
            startDate.set(startYear, 0, 1);
            Calendar endDate = Calendar.getInstance();
            endDate.set(endYear, 11, 31);
            return this.create(dialogSelect, startDate, endDate, showSecond);
        }

        public DateTimeSelectDialog create(IDialogDateSelected dialogSelect, Calendar startDate, Calendar endDate) {
            return this.create(dialogSelect, startDate, endDate, true);
        }


        public DateTimeSelectDialog create(IDialogDateSelected dialogSelect, Calendar startDate, Calendar endDate, boolean showSecond) {
            if (startDate.getTimeInMillis() >= endDate.getTimeInMillis()) {
                return null;
            }
            this.startDate = startDate;
            this.endDate = endDate;
            this.startYear = startDate.get(Calendar.YEAR);
            this.endYear = endDate.get(Calendar.YEAR);
            this.startMonth = startDate.get(Calendar.MONTH);
            this.endMonth = endDate.get(Calendar.MONTH);
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final DateTimeSelectDialog dialog = new DateTimeSelectDialog(context, themeResId);
            View layout = inflater.inflate(R.layout.base_dialog_date_time_select, null);
            cancelBtn = (TextView) layout.findViewById(R.id.cancel_tv);
            clearBtn = (TextView) layout.findViewById(R.id.clear_tv);
            confirmBtn = (TextView) layout.findViewById(R.id.confirm_tv);
            wheelYear = (WheelPicker) layout.findViewById(R.id.wheel_one);
            wheelMonth = (WheelPicker) layout.findViewById(R.id.wheel_two);
            wheelDay = (WheelPicker) layout.findViewById(R.id.wheel_three);
            wheelHour = layout.findViewById(R.id.wheel_four);
            wheelMinute = layout.findViewById(R.id.wheel_five);
            wheelSecond = layout.findViewById(R.id.wheel_six);
            initView(dialog, dialogSelect, showSecond);
            dialog.setContentView(layout);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
            p.width = d.getWidth(); //宽度设置为屏幕
            dialog.getWindow().setAttributes(p); //设置生效
            dialog.builder = this;
            return dialog;
        }

        private void initView(final DateTimeSelectDialog dialog, final IDialogDateSelected dialogSelect, boolean showSecond) {
            selectedDate = Calendar.getInstance();
            selectedDate.setTime(new Date());
            showYear();
            showMonth();
            showDay();
            showTime(showSecond);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (dialogSelect != null) {
                        dialogSelect.onSelected(null);
                    }
                }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (dialogSelect != null) {
                        dialogSelect.onSelected(selectedDate);
                    }
                }
            });
            wheelYear.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                    selectedDate.set(Calendar.YEAR, startYear + i);
                    showMonth();
                    showDay();
                }
            });
            wheelMonth.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                    int year = selectedDate.get(Calendar.YEAR);
                    int start = 0;
                    if (year == startYear) {
                        start = startDate.get(Calendar.MONTH);
                    }
                    setMonth(i + start);
                    showDay();
                }
            });
            wheelDay.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                    int year = selectedDate.get(Calendar.YEAR);
                    int month = selectedDate.get(Calendar.MONTH);
                    int start = 1;
                    if (year == startYear && month == startMonth) {
                        start = startDate.get(Calendar.DAY_OF_MONTH);
                    }
                    selectedDate.set(Calendar.DAY_OF_MONTH, i + start);
                }
            });
        }

        private void setMonth(int monthIndex) {
            int oldDay = selectedDate.get(Calendar.DAY_OF_MONTH);
            Calendar calendar = Calendar.getInstance();
            calendar.set(selectedDate.get(Calendar.YEAR), monthIndex, 1);
            int newMonthDayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (oldDay > newMonthDayCount) {
                selectedDate.set(Calendar.DAY_OF_MONTH, newMonthDayCount);
                selectedDate.set(Calendar.MONTH, monthIndex);
            } else {
                selectedDate.set(Calendar.MONTH, monthIndex);
            }
        }

        public void showYear() {
            int position = 0;
            int year = selectedDate.get(Calendar.YEAR);
            List<String> yearList = new ArrayList<>();
            for (int i = 0; i <= endYear - startYear; i++) {
                yearList.add(context.getString(R.string.base_date_year, String.valueOf(startYear + i)));
                if (year == startYear + i) {
                    position = i;
                }
            }
            wheelYear.setData(yearList);
            wheelYear.setSelectedItemPosition(position);
            selectedDate.set(Calendar.YEAR, startYear + position);
        }

        public void showMonth() {
            int position = 0;
            int year = selectedDate.get(Calendar.YEAR);
            int month = selectedDate.get(Calendar.MONTH);
            int start = 0;
            int end = 11;
            if (year == startYear) {
                start = startDate.get(Calendar.MONTH);
            }
            if (year == endYear) {
                end = endDate.get(Calendar.MONTH);
            }
            List<String> monthList = new ArrayList<>();
            for (int i = start; i <= end; i++) {
                String monthStr = String.valueOf(i + 1);
                if (i < 9) {
                    monthStr = "0" + monthStr;
                }
                monthList.add(context.getString(R.string.base_date_month, monthStr));
                if (month == i) {
                    position = i - start;
                }
            }
            wheelMonth.setData(monthList);
            wheelMonth.setSelectedItemPosition(position);
            setMonth(start + position);
        }

        public void showDay() {
            int position = 0;
            int year = selectedDate.get(Calendar.YEAR);
            int month = selectedDate.get(Calendar.MONTH);
            int day = selectedDate.get(Calendar.DAY_OF_MONTH);
            int start = 0;
            int end = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH) - 1;
            if (year == startYear && month == startMonth) {
                start = startDate.get(Calendar.DAY_OF_MONTH) - 1;
            }
            if (year == endYear && month == endMonth) {
                end = endDate.get(Calendar.DAY_OF_MONTH) - 1;
            }
            List<String> dayList = new ArrayList<>();
            for (int i = start; i <= end; i++) {
                String dayStr = String.valueOf(i + 1);
                if (i < 9) {
                    dayStr = "0" + dayStr;
                }
                dayList.add(context.getString(R.string.base_date_day, dayStr));
                if (day - 1 == i) {
                    position = i - start;
                }
            }
            wheelDay.setData(dayList);
            wheelDay.setSelectedItemPosition(position);
            selectedDate.set(Calendar.DAY_OF_MONTH, start + position + 1);
        }

        private void showTime(boolean showSecond) {
            List<String> hourList = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                String hour = String.valueOf(i);
                if (i < 10) {
                    hour = "0" + hour;
                }
                hourList.add(context.getString(R.string.base_time_hour, hour));
            }
            wheelHour.setData(hourList);
            wheelHour.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                    selectedDate.set(Calendar.HOUR_OF_DAY, i);
                }
            });
            int hour = selectedDate.get(Calendar.HOUR_OF_DAY);
            wheelHour.setSelectedItemPosition(hour);

            List<String> minuteList = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                String minute = String.valueOf(i);
                if (i < 10) {
                    minute = "0" + minute;
                }
                minuteList.add(context.getString(R.string.base_time_minute, minute));
            }
            wheelMinute.setData(minuteList);
            wheelMinute.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                    selectedDate.set(Calendar.MINUTE, i);
                }
            });
            int minute = selectedDate.get(Calendar.MINUTE);
            wheelMinute.setSelectedItemPosition(minute);
            if (showSecond) {
                List<String> secondList = new ArrayList<>();
                for (int i = 0; i < 60; i++) {
                    String second = String.valueOf(i);
                    if (i < 10) {
                        second = "0" + second;
                    }
                    secondList.add(context.getString(R.string.base_time_second, second));
                }
                wheelSecond.setData(secondList);
                wheelSecond.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                        selectedDate.set(Calendar.SECOND, i);
                    }
                });
                int second = selectedDate.get(Calendar.SECOND);
                wheelSecond.setSelectedItemPosition(second);
            } else {
                wheelSecond.setVisibility(View.GONE);
            }
        }
    }
}
