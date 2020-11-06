package com.cloudrtc.sipsdk.api.util;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Parcelable;

import com.tencent.mmkv.MMKV;


/**
 * 创建时间：2020/10/27
 * 创建人：singleCode
 * 功能描述：腾讯MMKV缓存框架
 * 1、轻量、存储速度快
 * 2、支持多进程访问：进程A修改值后，进程B读取的是进程A修改后的值
 **/
public class MMKVUtil {
    private static volatile MMKVUtil util;
    private MMKV mmkv;
    private static String rootDir;
    private static MODE mmkvMode = MODE.MULTI_PROCESS_MODE;
    private MMKVUtil() {
        if(mmkvMode == MODE.MULTI_PROCESS_MODE){
            mmkv = getMMkvWithID("xierMMap");
        }else {
            mmkv = getDefault();
        }
    }
    private enum MODE{
        SINGLE_PROCESS_MODE,//单进程访问
        MULTI_PROCESS_MODE//多进程访问
    }

    public static MMKVUtil getUtil() {
        if (util == null) {
            synchronized (MMKVUtil.class) {
                if (util == null) {
                    util = new MMKVUtil();
                }
            }
        }
        return util;
    }

    /**
     * 初始化 默认支持多进程访问
     * @param application
     */
    public static void init(Application application) {
       init(application,true);
    }
    public static void init(Application application,boolean multiProcess) {
        rootDir = MMKV.initialize(application);
        if(multiProcess){
            mmkvMode = MODE.MULTI_PROCESS_MODE;
        }else {
            mmkvMode = MODE.SINGLE_PROCESS_MODE;
        }
    }

    public MMKV getDefault() {
        return MMKV.defaultMMKV();
    }

    /**
     *
     * @param mmapId
     * @return
     */
    public MMKV getMMkvWithID(String mmapId) {
        return MMKV.mmkvWithID(mmapId, MMKV.MULTI_PROCESS_MODE);
    }

    public void put(String key, Object object) {
        if (object instanceof String) {
            mmkv.putString(key, (String) object);
        } else if (object instanceof Integer) {
            mmkv.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            mmkv.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            mmkv.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            mmkv.putLong(key, (Long) object);
        } else if (object instanceof Double) {
            mmkv.encode(key, (Double) object);
        } else if (object instanceof byte[]) {
            mmkv.encode(key, (byte[]) object);
        } else {
            mmkv.putString(key, object.toString());
        }
    }

    public Object get(String key, Object defaultValue) {
        if (defaultValue instanceof String) {
            return mmkv.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return mmkv.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return mmkv.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Float) {
            return mmkv.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            return mmkv.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Double) {
            return mmkv.decodeDouble(key, (Double) defaultValue);
        } else if (defaultValue instanceof byte[]) {
            return mmkv.decodeBytes(key);
        } else {
            return mmkv.getString(key, defaultValue.toString());
        }
    }

    /**
     * 存储序列化对象
     * @param key
     * @param object
     * @param <T>
     * @return
     */
    public <T extends Parcelable> boolean putParcelable(String key, T object){
        return mmkv.encode(key,object);
    }

    /**
     * 获取序列化对象
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public <T extends Parcelable> T getParcelable(String key,Class<T> tClass){
        return mmkv.decodeParcelable(key,tClass);
    }


    /**
     *SharedPreferences 迁移
     * @param preferences
     */
    public void replaceSp(SharedPreferences preferences){
        if(preferences !=null){
            mmkv.importFromSharedPreferences(preferences);
            clearSp(preferences);
        }
    }

    /**
     * 清空原SP中的缓存
     * @param preferences
     */
    private void clearSp(SharedPreferences preferences){
        if(preferences!=null){
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
        }
    }

    public void remove(String key) {
        mmkv.remove(key);
    }

    public boolean contains(String key) {
        return mmkv.contains(key);
    }

    public void clear() {
        mmkv.clear();
    }
}
