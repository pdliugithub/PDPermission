package com.rossia.life.pdpermission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.rossia.life.permissionlibrary.PermissionManager;

/**
 * @author pd_liu 2017/11/29.
 *         <p>
 *         验证权限申请结果
 *         </p>
 */
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String KEY_TAG = "MainActivity";
    /**
     * 权限管理类
     */
    private PermissionManager mPermissionManager;

    /**
     * 相机权限标识码
     */
    private static final int REQUEST_CODE_CAMERA = 254;

    private PermissionManager.PermissionGrantedResultCallback grantedResultCallback = new PermissionManager.PermissionGrantedResultCallback() {
        @Override
        public void grantResultCall(@PermissionManager.GrantedType int grantType) {
            Log.e(KEY_TAG, "--------------------------grantType：" + grantType);
            /*
            调用系统相机
             */
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mPermissionManager = PermissionManager.newInstance();
        mPermissionManager.setPermissionGrantedListener(grantedResultCallback);
    }

    public void startCamera(View view) {
        mPermissionManager.checkPermission(this, Manifest.permission.CAMERA, REQUEST_CODE_CAMERA);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mPermissionManager.requestPermissionsResult(requestCode, permissions, grantResults);
    }
}
