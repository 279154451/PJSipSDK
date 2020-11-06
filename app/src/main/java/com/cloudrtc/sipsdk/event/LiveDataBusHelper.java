package com.cloudrtc.sipsdk.event;


import com.jeremyliao.liveeventbus.LiveEventBus;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

/**
 * 创建时间：2020/10/27
 * 创建人：singleCode
 * 功能描述：基于LiveDataBus消息总线
 **/
public class LiveDataBusHelper {

    /**
     * 注册事件监听 无黏性
     * @param owner
     * @param eventType  事件类型
     * @param observer  监听器
     * @param <T>
     */
    public static<T extends IBaseEvent> void register(LifecycleOwner owner, @NonNull Class<T> eventType, Observer<T> observer){
        LiveEventBus.get(eventType).observe(owner,observer);
    }

    /**
     * 注册事件监听 有黏性
     * @param owner
     * @param eventType  事件类型
     * @param observer  监听器
     * @param <T>
     */
    public static<T extends IBaseEvent> void registerSticky(LifecycleOwner owner, @NonNull Class<T> eventType, Observer<T> observer){
        LiveEventBus.get(eventType).observeSticky(owner,observer);
    }
    /**
     * 进程内发送消息
     * @param eventType  消息类型
     * @param event  消息实例
     * @param <T>
     */
    public static<T extends IBaseEvent>  void post(@NonNull Class<T> eventType, @NonNull T event){
        LiveEventBus.get(eventType).post(event);
    }

    /**
     * 进程内延迟发送消息
     * @param eventType 消息类型
     * @param event 消息实例
     * @param delay 延迟时间 毫秒
     * @param <T>
     */
    public static<T extends IBaseEvent> void postDelay(@NonNull Class<T> eventType, @NonNull T event, long delay){
        LiveEventBus.get(eventType).postDelay(event,delay);
    }

    /**
     * 应用内跨进程发送消息
     * @param eventType  消息类型
     * @param event  消息实例
     * @param <T>
     */
    public static<T extends IBaseEvent>  void postAcrossProcess(@NonNull Class<T> eventType, @NonNull T  event){
        LiveEventBus.get(eventType).postAcrossProcess(event);
    }

    /**
     * 跨应用发送消息
     * @param eventType 消息类型
     * @param event  消息实列
     * @param <T>
     */
    public static<T extends IBaseEvent> void postAcrossApp(@NonNull Class<T> eventType, @NonNull T  event){
        LiveEventBus.get(eventType).postAcrossApp(event);
    }


}
