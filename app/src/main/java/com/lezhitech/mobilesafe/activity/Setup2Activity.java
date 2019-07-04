package com.lezhitech.mobilesafe.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.lezhitech.mobilesafe.R;
import com.lezhitech.mobilesafe.utils.ConstantValue;
import com.lezhitech.mobilesafe.utils.SpUtil;
import com.lezhitech.mobilesafe.utils.ToastUtil;
import com.lezhitech.mobilesafe.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private String simSerialNumber = "";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        initData();
    }

    @Override
    protected void showNextPage() {
        String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        if(!TextUtils.isEmpty(sim_number)){
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else{
            ToastUtil.show(this,"请绑定SIM");
        }
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    /**
     * 初始化data
     */
    private void initData() {
        //5,存储序列卡号
        TelephonyManager manager = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, ConstantValue.PERMISSON_REQUESTCODE);
        } else {
            //TODO
            simSerialNumber = manager.getSimSerialNumber();
            initUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case ConstantValue.PERMISSON_REQUESTCODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    TelephonyManager manager = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
                    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
                    simSerialNumber = manager.getSimSerialNumber();
                    initUI();
                }else{
                    ToastUtil.show(getApplicationContext(),"需要申请权限");
                    //返回上级菜单
                    Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        final SettingItemView siv_bind_sim = (SettingItemView) findViewById(R.id.siv_bind_sim);
        //1,回显（读取已有的绑定状态，用于回显，SP中是否存储了SIM卡的序列号）
        String sim_number = SpUtil.getString(getApplicationContext(), ConstantValue.SIM_NUMBER, "");
        //2,判断是否对应SIM卡为空
        if(TextUtils.isEmpty(sim_number)){
            siv_bind_sim.setCheck(false);
        }else{
            siv_bind_sim.setCheck(true);
        }
        siv_bind_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //3,获取原有状态
                boolean is_check = siv_bind_sim.isCheck();
                //4,状态取反，状态设置给当前条目
                siv_bind_sim.setCheck(!is_check);
                if(!is_check){
                    if(TextUtils.isEmpty(simSerialNumber)){
                        ToastUtil.show(getApplicationContext(),"SIM卡序列号为空");
                        SpUtil.remove(getApplicationContext(),ConstantValue.SIM_NUMBER);
                    }else{
                        SpUtil.putString(getApplicationContext(),ConstantValue.SIM_NUMBER,simSerialNumber);
                    }

                }else{
                    //6,删除sp中存储序列号的节点
                    SpUtil.remove(getApplicationContext(),ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

}
