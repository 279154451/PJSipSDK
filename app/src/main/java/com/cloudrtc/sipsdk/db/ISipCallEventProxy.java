package com.cloudrtc.sipsdk.db;

import com.cloudrtc.binder.CallRecordBean;

/**
 * 创建时间：2020/11/5
 * 创建人：singleCode
 * 功能描述：通话记录数据库代理
 **/
public interface ISipCallEventProxy {
    /**
     * 通话通话记录
     * @param callRecord
     */
    void onSaveCallRecord(CallRecordBean callRecord);

    /**
     *
     * @param responseCode  100：通话建立 101：通话结束
     * @param actionName
     * @param response
     */
    void onCallResult(int responseCode, String actionName, String response);
}
