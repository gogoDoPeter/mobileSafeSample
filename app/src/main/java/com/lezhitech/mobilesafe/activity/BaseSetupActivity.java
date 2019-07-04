package com.lezhitech.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //2,创建手势管理对象，用作管理在onTouchEvent(MotionEvent event)中传递过来的手势动作
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //监听手势的移动
                if (e1.getX() - e2.getX() > 0) {
                    //从右向左滑动，下一页
                    showNextPage();
                }
                if (e1.getX() - e2.getX() < 0) {
                    //从左向右滑动，上一页
                    showPrePage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    protected abstract void showNextPage();
    protected abstract void showPrePage();

    //1,监听屏幕上响应的事件类型
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3,通过手势处理类接收多种类型事件，用于处理
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    public void nextPage(View view){
        showNextPage();
    }
    public void prePage(View view){
        showPrePage();
    }
}
