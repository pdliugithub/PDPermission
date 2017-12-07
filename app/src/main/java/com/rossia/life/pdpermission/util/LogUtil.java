package com.rossia.life.pdpermission.util;

import android.util.Log;

import com.rossia.life.pdpermission.config.EnvironmentConfig;

/**
 * @author pd_liu on 2017/11/29.
 *         <p>
 *         简单地封装调试Log工具类
 *         </p>
 *         <p>
 *         Note: 关于项目工具类可查看{@link https://github.com/pdliugithub/AndroidUtil}
 *         为了维护项目的整洁原则, 也是避免以后项目中的依赖冲突问题
 *         此项目中尽量少的依赖、不依赖
 *         </p>
 */

public class LogUtil {

    private LogUtil() {
        throw new UnsupportedOperationException("Can not be instantiated!");
    }

    /**
     * 打印Error类型的Log
     * 是否打印依据：{@link EnvironmentConfig.sDebug}
     *
     * @param tag log tag
     * @param msg log message.
     */
    public static void e(String tag, String msg) {
        if (EnvironmentConfig.sDebug) {
            Log.e(tag, msg);
        }
    }
}
