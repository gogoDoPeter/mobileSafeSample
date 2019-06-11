package com.lezhitech.mobilesafe.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lezhitech.mobilesafe.R;
import com.lezhitech.mobilesafe.utils.StreamUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    private static final int UPDATE_VERSION = 100;
    private static final int ENTER_HOME = 101;
    private static final int URL_ERROR = 102;
    private static final int IO_ERROR = 103;
    private static final int JSON_ERROR = 104;
    private TextView tv_version_name;
    private int mLocalVersionCode;
    protected static final String tag = "SplashActivity";

    private String mVersionDes;
    private String mDownloadURL;
    private static final int PERMISSON_REQUESTCODE = 0;
    private boolean isNeedCheckPermission = true;
    /**
     * 需要进行检测的权限数组 这里只列举了几项 可以根据自己的项目需求来添加
     */
    protected String[] needPermissions = {
//            Manifest.permission.ACCESS_COARSE_LOCATION,//定位权限
//            Manifest.permission.ACCESS_FINE_LOCATION,//定位权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储卡写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE,//存储卡读取权限
//            Manifest.permission.READ_PHONE_STATE//读取手机状态权限
//            Manifest.permission.REQUEST_INSTALL_PACKAGES
    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case URL_ERROR:
                    Log.e(tag, "URL error");
                    enterHome();
                    break;
                case IO_ERROR:
                    Log.e(tag, "IO error");
                    enterHome();
                    break;
                case JSON_ERROR:
                    Log.e(tag, "Json error");
                    enterHome();
                    break;
            }
        }
    };
    private File mUpdateApkfile;

    /**
     * 弹出对话框提示用户更新
     */
    private void showUpdateDialog() {
        //对话框依赖于activity存在的
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置左上角图标
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("版本更新");
        //设置描述内容
        builder.setMessage(mVersionDes);
        //积极按钮，立即更新
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApkByXutils();
            }
        });

        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消对话框，进入主界面
                enterHome();
            }
        });

        //点击取消事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //即使用户点击取消,也需要让其进入应用程序主界面
                enterHome();
                //销毁移除当前对话框
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 下载apk 通过xutils实现download
     */
    private void downloadApkByXutils() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobilesafe1.apk";
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(mDownloadURL, path, new RequestCallBack<File>() {
                @Override
                public void onStart() {
                    Log.i(tag, "download onStart");
                    super.onStart();
                }

                @Override
                public void onCancelled() {
                    Log.i(tag, "download onCancelled");
                    super.onCancelled();
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    Log.i(tag, "download onLoading");
                    Log.i(tag, "download total:" + total + ", current:" + current);
                    super.onLoading(total, current, isUploading);
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功(下载过后的放置在sd卡中apk)
                    Log.i(tag, "download onSuccess");
                    mUpdateApkfile = responseInfo.result;
                    installApk(mUpdateApkfile);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i(tag, "download onFailure");

                }
            });
        }
    }

    /**
     * 安装对应apk
     *
     * @param file 安装文件
     */
    private void installApk(File file) {
        /*
        *   <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <data android:scheme="content" />
            <data android:scheme="file" />
            <data android:mimeType="application/vnd.android.package-archive" />
        </intent-filter>
        */
        /* 8.0 packageinstaller
        *  <intent-filter android:priority="1">
                <action android:name="android.intent.action.VIEW" />
                <action android:name="android.intent.action.INSTALL_PACKAGE" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="file" />
                <data android:scheme="content" />
                <data android:mimeType="application/vnd.android.package-archive" />
            </intent-filter>
        * */
      /*  Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        startActivity(intent);*/

        boolean haveInstallPermission;
        //先获取是否有安装未知来源应用的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            Log.i(tag,"haveInstallPermission="+haveInstallPermission);
            if (!haveInstallPermission) {   //没有权限
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("安装应用需要打开未知来源权限，请去设置中开启权限");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startInstallPermissionSettingActivity();
                        }
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                return;
            }
        }
//        String str = "/CanavaCancel.apk";
//        String fileName = Environment.getExternalStorageDirectory() + str;
        Intent intent2 = new Intent(Intent.ACTION_VIEW);
        intent2.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent2, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        //提供的intent需要设置包名，去打开权限设置界面才能在onActivityResult中接收到【resultCode 等于 RESULT_OK 】
        Uri packageURI = Uri.parse("package:" + getPackageName());
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, 10086);
    }
    /**
     * 下载apk
     */
    private void downloadApkByXutils3() {
        //1,判断sd卡是否可用,是否挂在上
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobilesafe.apk";
//            url = "http://127.0.0.1/server/abc.apk";
            RequestParams params = new RequestParams(mDownloadURL);
            //自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
            params.setSaveFilePath(Environment.getExternalStorageDirectory() + "/myapp/");
            //自动为文件命名
            params.setAutoRename(true);

            x.http().post(params, new Callback.ProgressCallback<File>() {
                @Override
                public void onSuccess(File result) {
                    //apk下载完成后，调用系统的安装方法
                  /*  Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
                    getActivity().startActivity(intent);*/
                    Log.i(tag, "download success");
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i(tag, "download onError");
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.i(tag, "download onCancelled");
                }

                @Override
                public void onFinished() {
                    Log.i(tag, "download onFinished");
                }

                //网络请求之前回调
                @Override
                public void onWaiting() {
                    Log.i(tag, "download onWaiting");
                }

                //网络请求开始的时候回调
                @Override
                public void onStarted() {
                    Log.i(tag, "download onStarted");
                }

                //下载的时候不断回调的方法
                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    //当前进度和文件总大小
                    Log.i(tag, "download onLoading +++");
                    Log.i(tag, "download current：" + current + "，total：" + total);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除掉当前activity头title,当SplashActivity继承Activity时生效
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除掉当前activity头title,当SplashActivity继承AppCompatActivity时生效
//        if (getSupportActionBar() != null){
//            getSupportActionBar().hide();
//        }
        setContentView(R.layout.activity_splash);
        if (isNeedCheckPermission) {
            checkPermissions(needPermissions);
        }

/*    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
    StrictMode.setVmPolicy(builder.build());
    builder.detectFileUriExposure();*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        initUI();
        initData();

//        x.Ext.init(this);
//        x.Ext.setDebug(false); //输出debug日志，开启会影响性能
    }

    private void checkPermissions(String[] needPermissions) {
        Log.i(tag, "checkPermissions +++");

        List<String> needRequestPermissonList = findDeniedPermissions(needPermissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            Log.i(tag, "checkPermissions size=" + needRequestPermissonList.size());
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
        Log.i(tag, "checkPermissions ---");
    }

    /**
     * 判断是否有某项权限
     *
     * @param string_permission 权限
     * @param request_code      请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission, int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }

    /**
     * 获取需要申请权限的列表
     *
     * @param permissions 需要申请的权限数组
     * @return
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        Log.i(tag, "needRequestPermissonList = " + needRequestPermissonList);
        return needRequestPermissonList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(grantResults)) {
                Log.i(tag, "not grant permission");
            } else {
                Log.i(tag, "has grant permission");
                isNeedCheckPermission = false;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 检查权限是否已经授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取数据方法
     */
    private void initData() {
        tv_version_name.setText("版本名称:" + getVersionName());
        mLocalVersionCode = getVersionCode();
        checkVersion();
    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
//                    http://192.168.1.10:8080/update.json
                    URL url = new URL("http://10.0.2.2:8080/update.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    //get is default, POST need to set
                    connection.setRequestMethod("GET");
                    Log.i(tag, "connection.getResponseCode()=" + connection.getResponseCode());
                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
//                        String json = StreamUtil.stream2String(is);
                        String json = StreamUtil.streamToString(is);
                        Log.i(tag, "json =================>");
                        Log.i(tag, json);
                        JSONObject jsonObject = new JSONObject(json);
                        mDownloadURL = jsonObject.getString("DownloadURL");
                        String versionCode = jsonObject.getString("VersionCode");
//                        mVersionDes = jsonObject.getString("VersionDes");
                        mVersionDes = URLDecoder.decode(jsonObject.getString("VersionDes"), "UTF-8");
                        String versionName = jsonObject.getString("VersionName");
                        Log.i(tag, mDownloadURL);
                        Log.i(tag, versionCode);
                        Log.i(tag, mVersionDes);
                        Log.i(tag, versionName);

                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            //弹出对话框，提示用户更新版本  对话框属于UI，目前处于thread中，不能再这里弹  消息机制
                            msg.what = UPDATE_VERSION;
                        } else {
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 3000) {
                        try {
                            Thread.sleep(3000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 进入应用程序主界面
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //在开启一个新的界面后,将导航界面关闭(导航界面只可见一次)
        this.finish();
    }

    /**
     * 获取应用版本号
     *
     * @return 应用版本号  0表示异常
     */
    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 获取应用版本名称
     *
     * @return 应用版本名称 null表示异常
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086 &&  resultCode == RESULT_OK) {
//            installProcess();
            installApk(mUpdateApkfile);
        }
        if(requestCode == 0){
            enterHome();
        }
    }
}
