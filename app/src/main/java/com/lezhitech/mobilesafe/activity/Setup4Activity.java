package com.lezhitech.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lezhitech.mobilesafe.R;
import com.lezhitech.mobilesafe.utils.ConstantValue;
import com.lezhitech.mobilesafe.utils.SpUtil;
import com.lezhitech.mobilesafe.utils.ToastUtil;

public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cb_open_security;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        initUI();

    }

    @Override
    protected void showNextPage() {
        boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        if(open_security){
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);
            finish();
            SpUtil.putBoolean(this, ConstantValue.SETUP_OVER,true);
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else{
            ToastUtil.show(this,"请开启防盗功能");
        }
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        cb_open_security = (CheckBox) findViewById(R.id.cb_open_security);
        //1,回显是否开启防盗状态
        boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        //2,根据状态,修改checkbox后续的文字显示
        cb_open_security.setChecked(open_security);
        if(open_security){
            cb_open_security.setText("手机防盗已开启");
        }else{
            cb_open_security.setText("手机防盗已关闭");
        }
        //3,点击过程中,监听选中状态发生改变过程,
        cb_open_security.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //4,isChecked是点击后的状态,存储点击后状态
                SpUtil.putBoolean(getApplicationContext(),ConstantValue.OPEN_SECURITY,isChecked);
                //5,根据开启关闭状态,去修改显示的文字
                if(isChecked){
                    cb_open_security.setText("手机防盗已开启");
                }else{
                    cb_open_security.setText("手机防盗已关闭");
                }
            }
        });
    }
}
