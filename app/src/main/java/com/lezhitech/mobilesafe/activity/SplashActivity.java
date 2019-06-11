package com.lezhitech.mobilesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.lezhitech.mobilesafe.R;
import com.lezhitech.mobilesafe.utils.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends AppCompatActivity {

    private TextView tv_version_name;
    private int mLocalVersionCode;
    protected static final String tag = "SplashActivity";

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
        tv_version_name.setText("版本名称:" + getVersionName());
        mLocalVersionCode = getVersionCode();
        checkVersion();
    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
//                super.run();
                try {
//                    http://192.168.1.10:8080/update.json
                    URL url = new URL("http://10.0.2.2:8080/update.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    //get is default, POST need to set
                    connection.setRequestMethod("GET");
                    Log.i(tag,"connection.getResponseCode()="+connection.getResponseCode());
                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
                        String json = StreamUtil.stream2String(is);
                        Log.i(tag, json);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 获取应用版本号
     *
     * @return 应用版本号  0表示异常
     */
    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 获取应用版本名称
     *
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
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
    }


}
