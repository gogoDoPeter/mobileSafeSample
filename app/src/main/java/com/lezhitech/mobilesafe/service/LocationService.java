package com.lezhitech.mobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

public class LocationService extends Service {

    private static final String Tag = "LocationService";
    /*
    经度	longitude;  纬度  latitude;
    经度0°——180°（东行,标注E）0°——180°（西行,标注W）
    纬度0°——90°N、0°——90°S
     */
    private double mLastLongitude = 200.0;
    private double mLastLatitude = 200.0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //第一次启动服务会调用
        Log.i(Tag, "onCreate");
        //获取手机的经纬度坐标
        //1,获取位置管理者对象
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //2,以最优的方式获取经纬度坐标()
        Criteria criteria = new Criteria();
        //允许花费
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//指定获取经纬度的精确度
        String bestProvider = locationManager.getBestProvider(criteria, true);
        //3,在一定时间间隔，一定移动距离后获取经纬度坐标
        MyLocationListener myLocationListener = new MyLocationListener();
        //检查是否具有运行时获取Location权限
        //TODO 临时方案
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(bestProvider, 1, 0, myLocationListener);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //当服务销毁时调用
        Log.i(Tag, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //每次启动服务，都会调用
        Log.i(Tag, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);

    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(Tag, "onLocationChanged");
            //经度
            double longitude = location.getLongitude();
            //纬度
            double latitude = location.getLatitude();
            Log.i(Tag, "longitude="+longitude+",latitude="+latitude);
            Log.i(Tag, "mLastLongitude="+mLastLongitude+",mLastLatitude="+mLastLatitude);
            if(mLastLongitude != longitude || mLastLatitude != latitude){
                Log.i(Tag, "send message of location");
                //4,发送短信(添加权限)
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage("5556", null, "longitude = " + longitude + ",latitude = " + latitude, null, null);
                mLastLongitude = longitude;
                mLastLatitude = latitude;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
