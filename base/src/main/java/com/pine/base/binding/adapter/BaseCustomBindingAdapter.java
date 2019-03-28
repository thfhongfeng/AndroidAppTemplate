package com.pine.base.binding.adapter;

import android.databinding.BindingAdapter;

import com.pine.base.component.editor.bean.TextImageItemEntity;
import com.pine.base.component.editor.ui.TextImageDisplayView;

import java.util.List;

// 对于名为android:zzz的属性，DataBinding库会自动尝试查找接受兼容类型作为参数的setZzz(arg)方法，
// 但有些属性在android代码中有可能并没有对应的setter方法，这时候就需要提供自定义逻辑。
// 自定义逻辑不仅可以自定义自己的属性处理逻辑，
// 也可以覆盖android属性的默认处理逻辑（定义的绑定适配器会在发生冲突时覆盖Android框架提供的默认适配器）
public class BaseCustomBindingAdapter {
    // BindingAdapter的value值：{"xxx", "yyy", ...}（也可重新定义android属性的处理方法，如：{"android:zzz"}），
    // 在布局文件中可以设置app:xxx="@{aaa}",app:yyy="@{bbb}", ...（android属性是"android:zzz"，与原生一致），
    // 则会调用被注解的方法，并将aaa,bbb,...依次传给方法的第一个参数之后的参数（参数类型需要一致），
    // 被注解的方法的第一个参数则是与属性关联的View类型
    // requireAll值：true表示必须所有参数均被设置才会调用；false则不用全部设置也会调用。

    @BindingAdapter(value = {"title", "contentList"}, requireAll = false)
    public static void setTitle(TextImageDisplayView view, String title, List<TextImageItemEntity> contentList) {
        view.setupView(title, contentList);
    }
}
