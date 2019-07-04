package com.lezhitech.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import com.lezhitech.mobilesafe.R;

public class Setup1Activity extends BaseSetupActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    protected void showNextPage() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
        //开启平移动画
        overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
    }

    @Override
    protected void showPrePage() {
        //空实现
    }

}
