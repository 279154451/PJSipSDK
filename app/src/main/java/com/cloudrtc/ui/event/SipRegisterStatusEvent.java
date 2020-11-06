package com.cloudrtc.ui.event;


import com.cloudrtc.sipsdk.event.IBaseEvent;

/**
 * 创建时间：2020/8/31
 * 创建人：singleCode
 * 功能描述：
 **/
public class SipRegisterStatusEvent implements IBaseEvent {
    private boolean isRegister;

    public SipRegisterStatusEvent(boolean isRegister) {
        this.isRegister = isRegister;
    }

    public boolean isRegister() {
        return isRegister;
    }
}
