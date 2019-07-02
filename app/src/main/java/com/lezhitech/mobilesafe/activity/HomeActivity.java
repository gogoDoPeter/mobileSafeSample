package com.lezhitech.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lezhitech.mobilesafe.R;
import com.lezhitech.mobilesafe.utils.ConstantValue;
import com.lezhitech.mobilesafe.utils.Md5Utils;
import com.lezhitech.mobilesafe.utils.SpUtil;
import com.lezhitech.mobilesafe.utils.ToastUtil;

public class HomeActivity extends Activity {

    private static final int PHONE_GUARD = 0;
    private static final int SETTTING_CENTER = 8;
    protected static final String tag = "HomeActivity";
    private GridView gv_home;
    private String[] mTitleStrs;
    private int[] mDrawableIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        initData();
    }

    private void initData() {
        //准备数据(文字(9组),图片(9张))
        mTitleStrs = new String[]{
                "手机防盗","通信卫士","软件管理","进程管理",
                "流量统计","手机杀毒","缓存清理","高级工具","设置中心"
        };

        mDrawableIds = new int[]{
                R.drawable.home_safe,R.drawable.home_callmsgsafe,
                R.drawable.home_apps,R.drawable.home_taskmanager,
                R.drawable.home_netmanager,R.drawable.home_trojan,
                R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings
        };

        //九宫格控件设置数据适配器(等同ListView数据适配器)
        gv_home.setAdapter(new MyAdapter());
        //注册九宫格单个条目点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case PHONE_GUARD:
                        //开启对话框
                        showPhoneGuardDialog();
                        break;
                    case SETTTING_CENTER:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void showPhoneGuardDialog() {
        String psd = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
        if(TextUtils.isEmpty(psd)){
            showSetPsdDialog();
        }else{
            showConfirmPsdDialog();
        }
    }

    private void showConfirmPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this,R.layout.dialog_confirm_psd,null);
        dialog.setView(view);
        dialog.show();

        Button bt_confirm = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String confirmPsd = et_confirm_psd.getText().toString().trim();
                if(!TextUtils.isEmpty(confirmPsd)){
                    String saveMd5Psd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
                    Log.i(tag,"saveMd5Psd="+saveMd5Psd);
                    String md5ConfirmPsd = Md5Utils.encoder(confirmPsd);
                    Log.i(tag,"md5ConfirmPsd="+md5ConfirmPsd);
                    if(saveMd5Psd.equals(md5ConfirmPsd)){
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();

                    }else {
                        ToastUtil.show(getApplicationContext(),"输入密码不正确");
                    }
                }else {
                    ToastUtil.show(getApplicationContext(),"输入密码为空");
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showSetPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this,R.layout.dialog_set_psd,null);
        dialog.setView(view);
        dialog.show();

        Button bt_confirm = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
                String psd = et_set_psd.getText().toString().trim();
                String confirmPsd = et_confirm_psd.getText().toString().trim();
                if(!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)){
                    if(psd.equals(confirmPsd)){
                        Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        String encoderMd5Psd = Md5Utils.encoder(psd);
                        Log.i(tag,"encoderMd5Psd="+encoderMd5Psd);
                        //通过SP保存密码,MD5 convert
                        SpUtil.putString(getApplicationContext(),ConstantValue.MOBILE_SAFE_PSD,encoderMd5Psd);
                    }else {
                        ToastUtil.show(getApplicationContext(),"确认密码不一致");
                    }
                }else {
                    ToastUtil.show(getApplicationContext(),"输入密码为空");
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void initUI() {
        gv_home = (GridView)findViewById(R.id.gv_home);

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            //条目的总数	文字组数 == 图片张数
            return mTitleStrs.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStrs[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_title.setText(mTitleStrs[position]);
            iv_icon.setBackgroundResource(mDrawableIds[position]);
            return view;
        }
    }
}
