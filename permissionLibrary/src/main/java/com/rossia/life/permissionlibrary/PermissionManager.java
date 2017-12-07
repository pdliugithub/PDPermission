package com.rossia.life.permissionlibrary;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author pd_liu on 2017/11/6.
 *         适配系统权限.
 *         动态请求系统权限.
 *         <p>
 *         创建对象{@link #newInstance()} .
 *         单个权限检查、申请{@link #checkPermissionSingle(Activity, String, int)}.
 *         多个权限检查、申请{@link #checkPermissionMany(Activity, String[], int)}.
 *         单个权限结果回传{@link #requestPermissionSingleResult(int, String[], int[])}.
 *         多个权限结果回传{@link #requestPermissionManyResult(int, String[], int[])}.
 *         设置单个权限的结果{@link #setPermissionResultSingleCallback(PermissionResultCallback)} 获取授权的详细结果{@link GrantedType}.
 *         设置多个权限的Callback{@link #setPermissionManyResultCallback(PermissionManyResultCallback)} 获取拒签的详细结果{@link DeniedType}.
 *         Note: 虽然申请单个权限调用{@link #checkPermissionMany(Activity, String[], int)}多个权限的功能，也可以正常使用
 *         但是，这会对性能有所印象，不推荐这样做，请根据情况进行合理的使用
 *         </p>
 */

public class PermissionManager {

    private static final String TAG = "PermissionManager";

    /**
     * 权限申请请求Code.
     */
    private int mRequestCode;

    /**
     * 存储权限申请的结果
     * 保证权限的顺序
     */
    private LinkedHashMap<String, Integer> nResultPermission;

    /**
     * @see #newInstance() .
     */
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
     * 单个权限申请Callback.
     */
    private PermissionResultCallback mPermissionResultCallback;

    /**
     * 多个权限申请Callback.
     */
    private PermissionManyResultCallback mPermissionManyResultCallback;

    /**
     * 定义授权成功回掉的参数。
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GRANT_NO_NEED, GRANT_ALREADY, GRANT_PRE_DENIED})
    public @interface GrantedType {
    }

    /**
     * <p>
     * Result: 默认授权
     * Description: 不需要进行请求权限。
     * Condition：Android version 小于 {@link Build.VERSION_CODES.M}时.
     * </p>
     */
    public static final int GRANT_NO_NEED = 1;

    /**
     * <p>
     * Result: 成功授权
     * Description：用户已经授权此权限.
     * Condition: Android version 大于 {@link Build.VERSION_CODES.M}时.
     * 当检查是否需要申请权限时，已经成功授权此权限了
     * </p>
     */
    public static final int GRANT_ALREADY = 2;

    /**
     * <p>
     * Result: 成功授权
     * Description：用户拒绝过权限，但是并没有勾选Don't ask again选项.
     * Condition: Android version 大于 {@link Build.VERSION_CODES.M}时.
     * 此次申请权限已经成功地授权
     * </p>
     */
    public static final int GRANT_PRE_DENIED = 3;

    /**
     * 定义拒签回掉的参数。
     */
    @IntDef({DENIED_THIS, DENIED_ALL})
    public @interface DeniedType {
    }

    /**
     * <p>
     * Result: Deny 拒签
     * Desccription: 用户拒绝了权限，但是没有勾选Don't ask again选项。
     * Condition：Android version 大于 {@link Build.VERSION_CODES.M}时.
     * 动态请求权限，用户勾选了拒绝选项
     * </p>
     */
    public static final int DENIED_THIS = 5;
    /**
     * Result: Deny 拒签
     * Description: 用户拒绝了权限，并且勾选了Don't ask again选项。
     * Condition: Android version 大于 {@link Build.VERSION_CODES.M}时.
     * 动态请求权限，用户勾选了拒绝、不再提示选项
     */
    public static final int DENIED_ALL = 6;

    /**
     * Check permission
     *
     * @param activity    Activity
     * @param permission  请求权限
     * @param requestCode 请求Code
     * @return
     * @see #requestPermissionManyResult(int, String[], int[]) .
     */
    public PermissionManager checkPermissionSingle(Activity activity, String permission, int requestCode) {

        //判断系统的版本.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //检查系统的权限.
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {

                //保存当前的权限请求Code.
                mRequestCode = requestCode;

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    //此方法操作情景：用户拒绝过权限，但是并没有勾选Don't ask again选项.
                    //请求权限
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                } else {
                    //此方法操作情景：用户拒绝过权限，并勾选Don't ask again选项.不需要进行请求权限，需要用户手动去设置中打开权限
                    denied(DENIED_ALL);

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

    public PermissionManager checkPermissionMany(Activity activity, String[] permissions, int requestCode) {

        if (activity == null || permissions == null) {
            return this;
        }
        //保存申请权限码
        mRequestCode = requestCode;
        nResultPermission = new LinkedHashMap<>(10);
        /*
        需要进行申请的权限容器
         */
        ArrayList<String> needRequestPermissions = new ArrayList<>();

        //判断系统的版本.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*
            检查需要申请的每一个权限
             */
            for (int i = 0; i < permissions.length; i++) {
                //Current permission.
                String currentPermission = permissions[i];

                if (ActivityCompat.checkSelfPermission(activity, currentPermission) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, currentPermission)) {
                        //此方法操作情景：用户拒绝过权限，但是并没有勾选Don't ask again选项.
                        //将需要进行动态请求的权限保存起来,便于统一申请权限.
                        needRequestPermissions.add(currentPermission);

                    } else {
                        //此方法操作情景：用户拒绝过权限，并勾选Don't ask again选项.不需要进行请求权限，需要用户手动去设置中打开权限
                        //保存结果，不需要申请
                        nResultPermission.put(currentPermission, DENIED_ALL);
                    }

                } else {
                    //已经开启权限，保存结果，不需要申请
                    nResultPermission.put(currentPermission, GRANT_ALREADY);
                }
            }

            //如果没有需要申请的权限，则Callback.
            if (needRequestPermissions.isEmpty()) {
                manyResultCallback();
            }

            //申请权限
            for (int i = 0; i < needRequestPermissions.size(); i++) {
                ActivityCompat.requestPermissions(activity, needRequestPermissions.toArray(new String[needRequestPermissions.size()]), mRequestCode);
            }

        } else {
            //不需要申请权限,直接启动.
            for (int i = 0; i < permissions.length; i++) {
                nResultPermission.put(permissions[i], GRANT_NO_NEED);
            }
            //结果回调
            manyResultCallback();
        }


        return this;
    }

    /**
     * Request permission result.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 授权结果
     * @return {@link PermissionManager}
     * @see #checkPermissionSingle(Activity, String, int) .
     */
    public PermissionManager requestPermissionSingleResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

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
     * 同时申请多个权限
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 授权结果
     * @return {@link PermissionManager}
     * @see #checkPermissionSingle(Activity, String, int) .
     */
    public PermissionManager requestPermissionManyResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == mRequestCode) {

            for (int i = 0; i < grantResults.length; i++) {

                int grantResult = grantResults[i];
                String permission = permissions[i];

                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    /*
                    之前被拒签过，但是没有勾选Don't ask again选项
                    保存当前权限状态
                     */
                    nResultPermission.put(permission, GRANT_PRE_DENIED);
                } else {
                    /*
                    当前请求权限被拒签
                    保存当前权限状态
                     */
                    nResultPermission.put(permission, GRANT_PRE_DENIED);
                }

            }
            //Callback.
            manyResultCallback();

        }

        return this;
    }

    /**
     * 权限已经授权，进行授权回掉。
     */
    private void granted(@GrantedType int type) {
        if (mPermissionResultCallback != null) {
            mPermissionResultCallback.grantResultCall(type);
        }
    }

    /**
     * 拒签，进行回掉
     */
    private void denied(@DeniedType int type) {
        if (mPermissionResultCallback != null) {
            mPermissionResultCallback.denyResultCall(type);
        }
    }

    /**
     * 对ManyResultCallback数据统一的处理
     */
    private void manyResultCallback() {

        if (mPermissionManyResultCallback != null) {

            //确保内部数据安全，进行包装数据.
            LinkedHashMap<String, Integer> result = new LinkedHashMap<>(10);
            result.putAll(nResultPermission);

            mPermissionManyResultCallback.resultMany(result);
        }
    }

    /**
     * 设置单个请求成功后的回掉
     *
     * @param resultCallback {@link PermissionResultCallback} .
     */
    public void setPermissionResultSingleCallback(PermissionResultCallback resultCallback) {
        mPermissionResultCallback = resultCallback;
    }

    /**
     * 设置多个请求成功后的回掉
     *
     * @param permissionManyResultCallback {@link PermissionManyResultCallback} .
     */
    public void setPermissionManyResultCallback(PermissionManyResultCallback permissionManyResultCallback) {
        mPermissionManyResultCallback = permissionManyResultCallback;
    }

    /**
     * 权限Callback接口。
     */
    public interface PermissionResultCallback {
        /**
         * 拒签函数回掉。
         *
         * @param denyType 拒签类型。{@link GrantedType}
         */
        void denyResultCall(@DeniedType int denyType);

        /**
         * 授权回掉函数。
         *
         * @param grantType 授权类型{@link GrantedType} .
         */
        void grantResultCall(@GrantedType int grantType);
    }

    /**
     * 同时申请多个权限时的回掉接口
     *
     * @see #checkPermissionMany(Activity, String[], int) .
     */
    public interface PermissionManyResultCallback {
        /**
         * 权限申请后的回掉
         *
         * @param result 所有权限申请的结果
         * @return 兼容后期维护
         */
        String resultMany(LinkedHashMap<String, Integer> result);
    }
}
