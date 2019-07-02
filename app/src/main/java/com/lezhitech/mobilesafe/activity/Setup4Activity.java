package com.lezhitech.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lezhitech.mobilesafe.R;
import com.lezhitech.mobilesafe.utils.ConstantValue;
import com.lezhitech.mobilesafe.utils.SpUtil;

public class Setup4Activity extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
    }
    public void nextPage(View view){
        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
        startActivity(intent);
        finish();
        SpUtil.putBoolean(this, ConstantValue.SETUP_OVER,true);
    }
    public void prePage(View view){
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);
        finish();
    }
}
