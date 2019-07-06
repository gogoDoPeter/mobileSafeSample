package com.lezhitech.mobilesafe.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lezhitech.mobilesafe.R;
import com.lezhitech.mobilesafe.utils.ConstantValue;
import com.lezhitech.mobilesafe.utils.SpUtil;


public class SetupOverActivity extends Activity {
    private static final String tag = "SetupOverActivity";
    private TextView tv_safe_phone_number;
    private TextView tv_reset_setup;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean is_setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER,false);
        if(is_setup_over) {
            setContentView(R.layout.activity_setup_over);
        }else{
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }
        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        tv_safe_phone_number = (TextView) findViewById(R.id.tv_safe_phone_number);
        String safe_phone = SpUtil.getString(this, ConstantValue.SECURITY_PHONE, "");
        tv_safe_phone_number.setText(safe_phone);
        tv_reset_setup = ((TextView) findViewById(R.id.tv_reset_setup));
        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
