package com.rossia.life.permissionlibrary;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author pd_liu on 2017/11/6.
 *         适配系统权限.
 *         动态请求系统权限.
 *         <p>
 *         获取当前的对象{@link #newInstance()} .
 *         检查权限{@link #checkPermission(Activity, String, int)}.
 *         权限结果回传{@link #requestPermissionsResult(int, String[], int[])}.
 *         获取授权的结果{@link #setPermissionGrantedListener(PermissionGrantedResultCallback)}.
 *         获取拒签的结果{@link #setPermissionDeniedListener(PermissionDeniedResultCallback)}.
 *         </p>
 */

public class PermissionManager {

    private static final String TAG = "PermissionManager";

    /**
     * 当前的请求Code.
     */
    private int mRequestCode;

    private PermissionManager() {
    }

    /**
     * Get new instance.
     *
     * @return {@link PermissionManager} .
     */
    public static PermissionManager newInstance() {
        return new PermissionManager();
    }

    /**
     * 授权、拒签对象.
     */
    private PermissionGrantedResultCallback mPermissionGrantedResult;

    private PermissionDeniedResultCallback mPermissionDeniedResult;

    /**
     * 定义授权成功回掉的参数。
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GRANT_NO_NEED, GRANT_ALREADY, GRANT_PRE_DENIED})
    public @interface GrantedType {
    }

    /**
     * 不需要进行请求权限。
     */
    public static final int GRANT_NO_NEED = 1;
    /**
     * 已经授权
     */
    public static final int GRANT_ALREADY = 2;
    /**
     * 开启授权，用户拒绝过权限，但是并没有勾选Don't ask again选项.
     */
    public static final int GRANT_PRE_DENIED = 3;

    /**
     * 定义拒签回掉的参数。
     */
    @IntDef({DENIED_THIS, DENIED_ALL})
    public @interface DeniedType {
    }

    /**
     * 用户拒绝了权限，但是没有勾选Don't ask again选项。
     */
    public static final int DENIED_THIS = 5;
    /**
     * 用户拒绝了权限，并且勾选了Don't ask again选项。
     */
    public static final int DENIED_ALL = 6;

    /**
     * Check permission
     *
     * @param activity    Activity
     * @param permission  请求权限
     * @param requestCode 请求Code
     * @return
     * @see #requestPermissionsResult(int, String[], int[]) .
     */
    public PermissionManager checkPermission(Activity activity, String permission, int requestCode) {

        //判断系统的版本.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //检查系统的权限.
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {

                //保存当前的权限请求Code.
                mRequestCode = requestCode;

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    //此方法操作情景：用户拒绝过权限，但是并没有勾选Don't ask again选项.
                    Log.e(TAG, "----------------------------------------------------此方法操作情景：用户拒绝过权限，但是并没有勾选Don't ask again选项.");
                    //请求权限
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                } else {
                    //此方法操作情景：用户拒绝过权限，并勾选Don't ask again选项.
                    denied(DENIED_ALL);
                    Log.e(TAG, "----------------------------------------------------此方法操作情景：用户拒绝过权限，并勾选Don't ask again选项");
                    //用户已经勾选Don't ask again选项，不需要进行请求权限，需要用户手动去设置中打开权限
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);

                }

            } else {
                //已经开启权限,直接启动
                granted(GRANT_ALREADY);
            }

        } else {
            //不需要申请权限,直接启动.
            granted(GRANT_NO_NEED);
        }


        return this;
    }

    /**
     * Request permission result.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 授权结果
     * @see #checkPermission(Activity, String, int) .
     */
    public PermissionManager requestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == mRequestCode) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //之前被拒签过，但是没有勾选Don't ask again选项
                granted(GRANT_PRE_DENIED);
            } else {
                //当前请求权限被拒签
                denied(DENIED_THIS);
            }

        }
        return this;
    }


    /**
     * 权限已经授权，进行授权回掉。
     */
    private void granted(@GrantedType int type) {
        if (mPermissionGrantedResult != null) {
            mPermissionGrantedResult.grantResultCall(type);
        }
    }

    /**
     * 拒签，进行回掉
     */
    private void denied(@DeniedType int type) {
        if (mPermissionDeniedResult != null) {
            mPermissionDeniedResult.denyResultCall(type);
        }
    }

    /**
     * 设置请求成功后的回掉
     *
     * @param grantedResultCallback {@link PermissionGrantedResultCallback} .
     */
    public void setPermissionGrantedListener(PermissionGrantedResultCallback grantedResultCallback) {
        this.mPermissionGrantedResult = grantedResultCallback;
    }

    /**
     * 设置拒签后的回掉
     *
     * @param deniedResultCallback {@link PermissionDeniedResultCallback} .
     */
    public void setPermissionDeniedListener(PermissionDeniedResultCallback deniedResultCallback) {
        this.mPermissionDeniedResult = deniedResultCallback;
    }

    /**
     * 权限授权接口。
     */
    public interface PermissionGrantedResultCallback {
        /**
         * 授权回掉函数。
         *
         * @param grantType 授权类型。{@link #@{@link GrantedType}} .
         */
        void grantResultCall(@GrantedType int grantType);
    }

    /**
     * 权限拒签接口。
     */
    public interface PermissionDeniedResultCallback {
        /**
         * 拒签函数回掉。
         *
         * @param denyType 拒签类型。
         */
        void denyResultCall(int denyType);
    }
}
