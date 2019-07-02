package com.lezhitech.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lezhitech.mobilesafe.R;

public class SettingItemView extends RelativeLayout {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.lezhitech.mobilesafe";
    private TextView tv_des;
    private CheckBox cb_box;
    private static final String tag = "SettingItemView";
    private String mDesTitle;
    private String mDesOn;
    private String mDesOff;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //xml--->view	将设置界面的一个条目转换成view对象,直接添加到了当前SettingItemView对应的view中
        //等同于以下两行代码
		/*View view = View.inflate(context, R.layout.setting_item_view, null);
		this.addView(view);*/
        View.inflate(context, R.layout.setting_item_view, this);

        //自定义组合控件中的标题描述
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_des = ((TextView) findViewById(R.id.tv_des));
        cb_box = ((CheckBox) findViewById(R.id.cb_box));

        //获取自定义以及原生属性的操作,AttributeSet attrs对象中获取
        initAttrs(attrs);

        tv_title.setText(mDesTitle);
    }

    private void initAttrs(AttributeSet attrs) {
        //获取属性的总个数
        /*Log.i(tag, "attrs.getAttributeCount()=" + attrs.getAttributeCount());
        //获取属性名称以及属性值
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            Log.i(tag, "attrs.getAttributeName()=" + attrs.getAttributeName(i));
            Log.i(tag, "attrs.getAttributeValue()=" + attrs.getAttributeValue(i));
            Log.i(tag, "======================================");
        }*/
        //通过名空间+属性名称获取属性值
        mDesTitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mDesOn = attrs.getAttributeValue(NAMESPACE, "deson");
        mDesOff = attrs.getAttributeValue(NAMESPACE, "desoff");
        Log.i(tag, "destitle=" + mDesTitle);
        Log.i(tag, "deson=" + mDesOn);
        Log.i(tag, "desoff=" + mDesOff);
    }

    /**
     * 判断是否开启
     *
     * @return 返回当前SettingImageView是否选中状态，true开启    false关闭
     */
    public boolean isCheck() {
        return cb_box.isChecked();
    }

    public void setCheck(boolean isCheck) {
        cb_box.setChecked(isCheck);
        if (isCheck()) {
            tv_des.setText(mDesOn);
        } else {
            tv_des.setText(mDesOff);

        }
    }
}
