package com.lezhitech.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lezhitech.mobilesafe.R;
import com.lezhitech.mobilesafe.utils.ConstantValue;
import com.lezhitech.mobilesafe.utils.SpUtil;


public class SetupOverActivity extends Activity {
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
    }
}
