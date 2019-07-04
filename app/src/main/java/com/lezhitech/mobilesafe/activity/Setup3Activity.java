package com.lezhitech.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lezhitech.mobilesafe.R;
import com.lezhitech.mobilesafe.utils.ConstantValue;
import com.lezhitech.mobilesafe.utils.SpUtil;
import com.lezhitech.mobilesafe.utils.ToastUtil;

public class Setup3Activity extends BaseSetupActivity {

    private EditText et_phone_number;
    private Button bt_select_number;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initUI();
    }

    @Override
    protected void showNextPage() {
        String phone = et_phone_number.getText().toString();
        if(!TextUtils.isEmpty(phone)){
            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);
            finish();
            SpUtil.putString(this, ConstantValue.SECURITY_PHONE,phone);
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else{
            ToastUtil.show(this,"请输入号码");
        }
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    /**
     * 初始化UI资源
     */
    private void initUI() {
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        bt_select_number = ((Button) findViewById(R.id.bt_select_number));
        //获取联系人电话号码回显过程
        String phone = SpUtil.getString(this, ConstantValue.SECURITY_PHONE, "");
        et_phone_number.setText(phone);
        bt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ContactListActivity.class);
                startActivityForResult(intent,0);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            String phone = data.getStringExtra("phone");
            phone = phone.replace("-","").replace(" ","").trim();
            et_phone_number.setText(phone);
            SpUtil.putString(this, ConstantValue.SECURITY_PHONE,phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
