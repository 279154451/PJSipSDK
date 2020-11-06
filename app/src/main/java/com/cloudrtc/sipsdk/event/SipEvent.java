package com.cloudrtc.sipsdk.event;


/**
 * 创建时间：2020/8/21
 * 创建人：singleCode
 * 功能描述：
 **/
public class SipEvent implements IBaseEvent {
    public static final int MAKE_CALL = 0;//打电话
    public static final int HANG_UP_CALL = 1;//挂断、拒绝
    public static final int ANSWER_CALL = 2;//接听
    public static final int SEND_IM = 3;//发送消息
    public static final int SWITCH_TO_AUDIO = 4;//切换到只音频通话
    public static final int REGISTER = 5;//注册
    public static final int UNREGISTER = 6;//注销
    private int eventCode;
    private Object eventData;
    public SipEvent(int eventCode) {
        this.eventCode = eventCode;
    }

    public SipEvent(int eventCode, Object eventData) {
        this.eventCode = eventCode;
        this.eventData = eventData;
    }

    public Object getEventData() {
        return eventData;
    }

    public int getEventCode() {
        return eventCode;
    }

    @Override
    public String toString() {
        return "UiCallEvent{" +
                "eventCode=" + eventCode +
                ", eventData=" + eventData +
                '}';
    }
}
