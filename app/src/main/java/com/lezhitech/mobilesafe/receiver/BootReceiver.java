package com.lezhitech.mobilesafe.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lezhitech.mobilesafe.utils.ConstantValue;
import com.lezhitech.mobilesafe.utils.SpUtil;

public class BootReceiver extends BroadcastReceiver {
    private static final String tag = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(tag, "Sim change! Alarm...");
        //1,获取sim卡号
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE);
        String simSerialNumber = tm.getSimSerialNumber();
        //2,和sp中保存sim卡号对比
        String sim_number = SpUtil.getString(context, ConstantValue.SIM_NUMBER, "");
        if(!sim_number.equals(sim_number)){
            //3,不一致，发送短信给安全号码
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("5556",null,"SIM Change,Alarm!",null,null);
        }
    }
}
