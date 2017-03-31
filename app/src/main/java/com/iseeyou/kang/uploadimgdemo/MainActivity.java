package com.iseeyou.kang.uploadimgdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iseeyou.kang.uploadimgdemo.bean.UpLoadBean;
import com.iseeyou.kang.uploadimgdemo.util.ImageLoader;
import com.iseeyou.kang.uploadimgdemo.util.PhotoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity implements PhotoUtils.OnPhotoResultListener {

    // 我自己的测试地址
    String UP_LOAD_FILE = "http://120.77.251.141:39080/youe/commons/uploadFile";
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 100; // 权限请求码

    @BindView(R.id.headimg)  ImageView headimg;

    private BottomMenuDialog mBottomMenuDialog = null;
    public PhotoUtils photoUtils = null;//拍照工具类
    private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // 如果是6.0则应该请求需要的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        }
        photoUtils = new PhotoUtils(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在上传中...");
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionGen.with(this)
                    .addRequestCode(REQUEST_CODE_ASK_PERMISSIONS)
                    .permissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE, //内存
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = REQUEST_CODE_ASK_PERMISSIONS)
    public void doPermissionSuccess() {
        Toast.makeText(this, "权限请求成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = REQUEST_CODE_ASK_PERMISSIONS)
    public void doPermissionFail() {
        Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.upLoad)
    public void onClick(View view) {
        showPhotoDialog();
    }

    /**
     * 选择照片或者拍照Dialog
     */
    private void showPhotoDialog() {
        // 应该再判断一次6.0权限问题 但是我刚进来就判断了这里就没判断了.
        mBottomMenuDialog = new BottomMenuDialog(this, "拍照", "相册", "取消");
        mBottomMenuDialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomMenuDialog != null && mBottomMenuDialog.isShowing()) {
                    mBottomMenuDialog.dismiss();
                }
                photoUtils.takePicture(MainActivity.this);
            }
        });
        mBottomMenuDialog.setMiddleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomMenuDialog != null && mBottomMenuDialog.isShowing()) {
                    mBottomMenuDialog.dismiss();
                }
                photoUtils.selectPicture(MainActivity.this);
            }
        });
        mBottomMenuDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoUtils.INTENT_CROP:
            case PhotoUtils.INTENT_TAKE:
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(this, requestCode, resultCode, data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 当选择图片或者拍摄图片拿到结果之后
     *
     * @param uri 本地图片URL
     */
    @Override
    public void onPhotoResult(Uri uri) {
        if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
            File file = new File(uri.getPath());
            // 我这里拿我自己后台接口测试  只需要更换接口名称字段就可以了 上传用的OkhttpUtils
            upLoadImg(file);
        }
    }

    /**
     * 上传
     * @param file
     */
    private void upLoadImg(File file) {
        mProgressDialog.show();
        OkHttpUtils.post()
                .addFile("file", file.getPath(), file) // 文件字段  = img你的接口字段
                .url(UP_LOAD_FILE)
                .addParams("toType", "0")
                .addParams("toid", "6")  // 更换你自己的字段
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        // 上传失败
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        Gson gson = new Gson();
                        UpLoadBean bean = gson.fromJson(response, UpLoadBean.class);
                        if (bean.isOk()) {
                            String imgUrl = "http://120.77.251.141:8081" + bean.getRes();
                            ImageLoader.load(MainActivity.this,imgUrl,headimg);
                        }
                    }
                });
    }


    @Override
    public void onPhotoCancel() {
        Toast.makeText(this, "已取消拍摄", Toast.LENGTH_SHORT).show();
    }
}
