package com.rossia.life.pdpermission;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rossia.life.pdpermission.util.LogUtil;
import com.rossia.life.permissionlibrary.PermissionManager;
import com.rossia.life.permissionlibrary.PermissionManager.GrantedType;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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

    private void startCameraIntent() {
        /*
        调用系统相机
        */
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }

    private PermissionManager.PermissionResultCallback resultCallback = new PermissionManager.PermissionResultCallback() {
        @Override
        public void denyResultCall(int denyType) {
            LogUtil.e(KEY_TAG, "--------------------------grantType：" + denyType);
        }

        @Override
        public void grantResultCall(@GrantedType int grantType) {
            LogUtil.e(KEY_TAG, "--------------------------grantType：" + grantType);
            startCameraIntent();
        }
    };

    private PermissionManager.PermissionManyResultCallback manyResultCallback = new PermissionManager.PermissionManyResultCallback() {
        @Override
        public String resultMany(LinkedHashMap<String, Integer> result) {
            Set<Map.Entry<String, Integer>> set = result.entrySet();
            Iterator<Map.Entry<String, Integer>> it = set.iterator();

            while (it.hasNext()) {
                Map.Entry<String, Integer> next = it.next();
                String key = next.getKey();
                Integer value = next.getValue();

                LogUtil.e(KEY_TAG, "---------------------key:\t" + key + "\t\t value:\t" + value);
            }

            startCameraIntent();
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mPermissionManager = PermissionManager.newInstance();
        mPermissionManager.setPermissionResultSingleCallback(resultCallback);
        mPermissionManager.setPermissionManyResultCallback(manyResultCallback);
    }

    public void startCamera(View view) {
        mPermissionManager.checkPermissionMany(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 288);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mPermissionManager.requestPermissionManyResult(requestCode, permissions, grantResults);
    }
}
