# PJSipSDK
基于pjsip的音视频通话SDK封装

```
1、org目录

    pjsip native层对接代码。不要动！！！

2、mediaengine目录

    媒体库native层对接代码，不要动！！！

3、sipsdk目录

    基于sip通话流程进行的封装，不要动！！！

4、binder目录

    由于sip通话过程中会存在大量的内存消耗等问题，所以建议将SipServer运行在单独的进程中所以需要IPC通信

5、APPSipHelper

  SipServer的相关的帮助类

    （1）、启动SipServer  ：initSipService
     (2)、绑定SipServer，并注册sip账户：bindSipService
     (3)、发起sip通话：makeCall
     (4)、通过ISipCallEventProxy，获取通话记录
6、SipService

    SipServer抽象类，可参考XSipService

7、SipAccountHelper
    Sip账号工具类
    （1）、注册
    （2）、解注册
    （3）、监听注册状态

8、SipCallHelper
    Sip通话工具类
    （1）、发起通话
    （2）、挂断通话
    （3）、接听来电
    （4）、发送IM消息

 9、SipCallRecordHelper
   Sip通话记录工具类
   （1）、记录当前通话信息
   （2）、通过IPC上报通话记录
```