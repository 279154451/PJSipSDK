/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua2;

public class AccountPresConfig extends PersistentObject {
  private long swigCPtr;

  protected AccountPresConfig(long cPtr, boolean cMemoryOwn) {
    super(pjsua2JNI.AccountPresConfig_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(AccountPresConfig obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsua2JNI.delete_AccountPresConfig(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public void setHeaders(SipHeaderVector value) {
    pjsua2JNI.AccountPresConfig_headers_set(swigCPtr, this, SipHeaderVector.getCPtr(value), value);
  }

  public SipHeaderVector getHeaders() {
    long cPtr = pjsua2JNI.AccountPresConfig_headers_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SipHeaderVector(cPtr, false);
  }

  public void setPublishEnabled(boolean value) {
    pjsua2JNI.AccountPresConfig_publishEnabled_set(swigCPtr, this, value);
  }

  public boolean getPublishEnabled() {
    return pjsua2JNI.AccountPresConfig_publishEnabled_get(swigCPtr, this);
  }

  public void setPublishQueue(boolean value) {
    pjsua2JNI.AccountPresConfig_publishQueue_set(swigCPtr, this, value);
  }

  public boolean getPublishQueue() {
    return pjsua2JNI.AccountPresConfig_publishQueue_get(swigCPtr, this);
  }

  public void setPublishShutdownWaitMsec(long value) {
    pjsua2JNI.AccountPresConfig_publishShutdownWaitMsec_set(swigCPtr, this, value);
  }

  public long getPublishShutdownWaitMsec() {
    return pjsua2JNI.AccountPresConfig_publishShutdownWaitMsec_get(swigCPtr, this);
  }

  public void setPidfTupleId(String value) {
    pjsua2JNI.AccountPresConfig_pidfTupleId_set(swigCPtr, this, value);
  }

  public String getPidfTupleId() {
    return pjsua2JNI.AccountPresConfig_pidfTupleId_get(swigCPtr, this);
  }

  public void readObject(ContainerNode node) throws Exception {
    pjsua2JNI.AccountPresConfig_readObject(swigCPtr, this, ContainerNode.getCPtr(node), node);
  }

  public void writeObject(ContainerNode node) throws Exception {
    pjsua2JNI.AccountPresConfig_writeObject(swigCPtr, this, ContainerNode.getCPtr(node), node);
  }

  public AccountPresConfig() {
    this(pjsua2JNI.new_AccountPresConfig(), true);
  }

}
