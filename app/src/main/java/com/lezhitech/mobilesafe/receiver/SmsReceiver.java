package com.lezhitech.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.lezhitech.mobilesafe.R;
import com.lezhitech.mobilesafe.service.LocationService;
import com.lezhitech.mobilesafe.utils.ConstantValue;
import com.lezhitech.mobilesafe.utils.SpUtil;

public class SmsReceiver extends BroadcastReceiver {
    private static final String tag = "SmsReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(tag,"smsReceiver enter");
        boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
        if(open_security){
            //1,获取短信内容
            Object[] objects = (Object[])intent.getExtras().get("pdus");
            //2,循环遍历短信过程
            for (Object object:objects) {
                //3,获取短信对象
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
                //4,获取短信对象基本信息
                String originatingAddress = smsMessage.getOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();
                Log.i(tag,"originatingAddress="+originatingAddress);
                Log.i(tag,"messageBody="+messageBody);
                //5,判断是否包含播放音乐关键字
                if(messageBody.contains("#*alarm*#")){
                    //6,播放音乐（准备音乐，MediaPlayer）
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                if(messageBody.contains("#*location*#")){
                    //7，开启一个服务，在服务中获取经纬度信息，发送给安全号码
                   context.startService(new Intent(context, LocationService.class));
                }
            }
        }
    }
}
