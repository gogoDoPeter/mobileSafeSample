package com.lezhitech.mobilesafe.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lezhitech.mobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactListActivity extends Activity {

    private ListView lv_contact;
    protected static final String tag = "ContactListActivity";
    private List<HashMap<String,String>> contactList = new ArrayList<HashMap<String, String>>();

    private mAdapter mAdapter;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //8,填充数据适配器
            mAdapter = new mAdapter();
            lv_contact.setAdapter(mAdapter);
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initUI();
        initData();
    }

    private void initUI() {
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mAdapter != null){

                    //1,获取点中条目的索引对应的指向集合中的对象
                    HashMap<String, String> hashMap = mAdapter.getItem(position);
                    //2,获取当前条目指向集合对应的手机号码
                    String phone = hashMap.get("phone");
                    //3,将此号码传给第三个导航界面用;
                    //结束此界面回到前一个导航界面时候，将数据返回过去
                    Intent intent = new Intent();
                    intent.putExtra("phone",phone);
                    setResult(0,intent);

                    finish();
                }
            }
        });
    }

    /**
     * 获取系统联系人数据方法
     */
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                //1,获取内容解析器对象
                ContentResolver contentResolver = getContentResolver();
                //2,做查询系统联系人数据库表过程(注意添加读取联系人权限)
                //"content://com.android.contacts/raw_contacts"
                Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},
                        null, null, null);
                contactList.clear();
                //3,循环游标，直到没有数据为止
                while(cursor.moveToNext()){
                    String id = cursor.getString(0);
//                    Log.i(tag, "id="+id);
                    //4，根据用户唯一性id值，查询data表和mimetype表生成的视图，获取data以及mimetype字段
                    Cursor cursorIdx = contentResolver.query(Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"},
                            "raw_contact_id = ?",
                            new String[]{id}, null);
                    //5,循环获取每一位联系人的姓名、电话号码和数据类型
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    while(cursorIdx.moveToNext()){
                        String data = cursorIdx.getString(0);
                        String type = cursorIdx.getString(1);
//                        Log.i(tag,"data = "+data);
//                        Log.i(tag,"type = "+type);
                        //6,填充数据给hashMap
                        if(type.equals("vnd.android.cursor.item/phone_v2")&& !TextUtils.isEmpty(data)){
                            hashMap.put("phone",data);
                        }
                        if(type.equals("vnd.android.cursor.item/name")&& !TextUtils.isEmpty(data)){
                            hashMap.put("name",data);
                        }
                    }
                    contactList.add(hashMap);
                    cursorIdx.close();
                }
                cursor.close();
                //7,消息机制,发送一个空的消息,告知主线程可以去使用子线程已经填充好的数据集合
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private class mAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
            tv_name.setText(getItem(position).get("name"));
            tv_phone.setText(getItem(position).get("phone"));
            return view;
        }
    }
}
