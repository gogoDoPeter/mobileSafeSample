package com.lezhitech.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.lezhitech.mobilesafe.R;
import com.lezhitech.mobilesafe.utils.ConstantValue;
import com.lezhitech.mobilesafe.utils.SpUtil;
import com.lezhitech.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUpdate();

    }

    private void initUpdate() {
        final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
        boolean isCheckOpenUpdate = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
        siv_update.setCheck(isCheckOpenUpdate);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = siv_update.isCheck();
                siv_update.setCheck(!isCheck);
                SpUtil.putBoolean(getApplicationContext(),ConstantValue.OPEN_UPDATE,!isCheck);
            }
        });
    }
}
