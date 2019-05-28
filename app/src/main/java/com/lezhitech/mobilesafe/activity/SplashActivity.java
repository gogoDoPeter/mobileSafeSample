package com.lezhitech.mobilesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.lezhitech.mobilesafe.R;

public class SplashActivity extends AppCompatActivity {

    private TextView tv_version_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除掉当前activity头title,当SplashActivity继承Activity时生效
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除掉当前activity头title,当SplashActivity继承AppCompatActivity时生效
//        if (getSupportActionBar() != null){
//            getSupportActionBar().hide();
//        }
        setContentView(R.layout.activity_splash);

        initUI();
        initData();
    }

    /**
     * 获取数据方法
     */
    private void initData() {
        tv_version_name.setText("版本名称:"+getVersionName());
    }

    /**
     * 获取应用版本名称
     * @return 应用版本名称 null表示异常
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        tv_version_name = (TextView)findViewById(R.id.tv_version_name);
    }


}
